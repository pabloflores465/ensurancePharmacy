#!/usr/bin/env python3
"""
CheckMK Prometheus Exporter
Exporta métricas de CheckMK a formato Prometheus
"""

import requests
import time
from prometheus_client import start_http_server, Gauge, Counter
import sys

# Métricas Prometheus
checkmk_host_up = Gauge('checkmk_host_up', 'Host is up', ['host'])
checkmk_service_state = Gauge('checkmk_service_state', 'Service state', ['host', 'service'])
checkmk_cpu_usage = Gauge('checkmk_cpu_usage_percent', 'CPU usage percentage', ['host'])
checkmk_memory_usage = Gauge('checkmk_memory_usage_percent', 'Memory usage percentage', ['host'])
checkmk_disk_usage = Gauge('checkmk_disk_usage_percent', 'Disk usage percentage', ['host', 'mount'])
checkmk_network_in = Counter('checkmk_network_in_bytes', 'Network bytes in', ['host', 'interface'])
checkmk_network_out = Counter('checkmk_network_out_bytes', 'Network bytes out', ['host', 'interface'])

# Configuración
CHECKMK_URL = "http://localhost:5000/ensurance/check_mk/api/1.0"
CHECKMK_USER = "automation"
CHECKMK_PASSWORD = "automation_secret"

def fetch_checkmk_data():
    """Obtiene datos de CheckMK"""
    try:
        # Obtener hosts
        response = requests.get(
            f"{CHECKMK_URL}/domain-types/host_config/collections/all",
            auth=(CHECKMK_USER, CHECKMK_PASSWORD),
            headers={"Accept": "application/json"}
        )
        
        if response.status_code == 200:
            hosts = response.json().get("value", [])
            
            for host in hosts:
                host_name = host.get("id")
                
                # Estado del host
                host_state = get_host_state(host_name)
                checkmk_host_up.labels(host=host_name).set(1 if host_state == 0 else 0)
                
                # Métricas del host
                metrics = get_host_metrics(host_name)
                
                if "cpu" in metrics:
                    checkmk_cpu_usage.labels(host=host_name).set(metrics["cpu"])
                
                if "memory" in metrics:
                    checkmk_memory_usage.labels(host=host_name).set(metrics["memory"])
                
                if "disk" in metrics:
                    for mount, usage in metrics["disk"].items():
                        checkmk_disk_usage.labels(host=host_name, mount=mount).set(usage)
                
                if "network" in metrics:
                    for iface, data in metrics["network"].items():
                        checkmk_network_in.labels(host=host_name, interface=iface).inc(data.get("in", 0))
                        checkmk_network_out.labels(host=host_name, interface=iface).inc(data.get("out", 0))
                
    except Exception as e:
        print(f"Error fetching CheckMK data: {e}", file=sys.stderr)

def get_host_state(host_name):
    """Obtiene el estado de un host"""
    try:
        response = requests.get(
            f"{CHECKMK_URL}/objects/host/{host_name}",
            auth=(CHECKMK_USER, CHECKMK_PASSWORD),
            headers={"Accept": "application/json"}
        )
        
        if response.status_code == 200:
            data = response.json()
            return data.get("extensions", {}).get("state", 2)
    except:
        pass
    return 2  # UNKNOWN

def get_host_metrics(host_name):
    """Obtiene métricas de un host"""
    metrics = {}
    
    try:
        # Obtener servicios del host
        response = requests.get(
            f"{CHECKMK_URL}/objects/host/{host_name}/collections/services",
            auth=(CHECKMK_USER, CHECKMK_PASSWORD),
            headers={"Accept": "application/json"}
        )
        
        if response.status_code == 200:
            services = response.json().get("value", [])
            
            for service in services:
                service_name = service.get("id")
                perf_data = service.get("extensions", {}).get("metrics", {})
                
                # Parsear métricas según el tipo de servicio
                if "CPU" in service_name:
                    metrics["cpu"] = parse_cpu_metric(perf_data)
                elif "Memory" in service_name or "RAM" in service_name:
                    metrics["memory"] = parse_memory_metric(perf_data)
                elif "Disk" in service_name or "Filesystem" in service_name:
                    if "disk" not in metrics:
                        metrics["disk"] = {}
                    mount = service_name.split()[-1] if " " in service_name else "/"
                    metrics["disk"][mount] = parse_disk_metric(perf_data)
                elif "Interface" in service_name or "Network" in service_name:
                    if "network" not in metrics:
                        metrics["network"] = {}
                    iface = service_name.split()[-1] if " " in service_name else "eth0"
                    metrics["network"][iface] = parse_network_metric(perf_data)
    except:
        pass
    
    return metrics

def parse_cpu_metric(perf_data):
    """Parsea métrica de CPU"""
    if "util" in perf_data:
        return float(perf_data["util"])
    return 0.0

def parse_memory_metric(perf_data):
    """Parsea métrica de memoria"""
    if "usage" in perf_data:
        return float(perf_data["usage"])
    return 0.0

def parse_disk_metric(perf_data):
    """Parsea métrica de disco"""
    if "used_percent" in perf_data:
        return float(perf_data["used_percent"])
    return 0.0

def parse_network_metric(perf_data):
    """Parsea métrica de red"""
    return {
        "in": float(perf_data.get("in", 0)),
        "out": float(perf_data.get("out", 0)),
    }

if __name__ == "__main__":
    # Iniciar servidor Prometheus
    start_http_server(9999)
    print("CheckMK Prometheus Exporter running on port 9999")
    
    # Loop principal
    while True:
        fetch_checkmk_data()
        time.sleep(30)  # Actualizar cada 30 segundos
