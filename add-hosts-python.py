#!/usr/bin/env python3
"""
Script para agregar hosts a CheckMK usando Python API interno
"""

import sys
import os

# Agregar path de CheckMK
sys.path.insert(0, '/omd/sites/ensurance/lib/python3/cmk/gui')

# Hosts a agregar
HOSTS = {
    'prometheus': {'ip': 'ensurance-prometheus-full', 'alias': 'Prometheus Server'},
    'grafana': {'ip': 'ensurance-grafana-full', 'alias': 'Grafana Dashboard'},
    'alertmanager': {'ip': 'ensurance-alertmanager-full', 'alias': 'Alert Manager'},
    'rabbitmq': {'ip': 'ensurance-rabbitmq-full', 'alias': 'RabbitMQ Message Broker'},
    'netdata': {'ip': 'ensurance-netdata-full', 'alias': 'Netdata Monitoring'},
    'node-exporter': {'ip': 'ensurance-node-exporter-full', 'alias': 'Node Exporter'},
    'pushgateway': {'ip': 'ensurance-pushgateway-full', 'alias': 'Prometheus Pushgateway'},
    'ensurance-app': {'ip': 'ensurance-pharmacy-full', 'alias': 'Ensurance Pharmacy App'},
}

# Crear archivo de configuración simple
config_content = """# Hosts configuration for Ensurance Pharmacy
# Generated automatically

all_hosts = []
ipaddresses = {}
host_attributes = {}

"""

for hostname, info in HOSTS.items():
    config_content += f"""
# Host: {hostname}
all_hosts.append('{hostname}')
ipaddresses['{hostname}'] = '{info['ip']}'
host_attributes['{hostname}'] = {{
    'alias': '{info['alias']}',
    'ipaddress': '{info['ip']}',
}}
"""

# Escribir configuración
config_file = '/omd/sites/ensurance/etc/check_mk/conf.d/ensurance_hosts.mk'
with open(config_file, 'w') as f:
    f.write(config_content)

print(f"✓ Configuración escrita en: {config_file}")
print(f"✓ Hosts agregados: {len(HOSTS)}")
print("")
print("Hosts configurados:")
for hostname, info in HOSTS.items():
    print(f"  • {hostname:20} -> {info['ip']}")
