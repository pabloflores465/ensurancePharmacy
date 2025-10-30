# ============================================================================
# CHECKMK CONFIGURATION - ENSURANCE PHARMACY
# Configuración para monitorear el mismo stack que Netdata
# ============================================================================

# Hosts a monitorear (mismos que Netdata)
all_hosts += [
    "prometheus|monitoring|linux",
    "grafana|monitoring|linux",
    "alertmanager|monitoring|linux",
    "node-exporter|monitoring|linux",
    "pushgateway|monitoring|linux",
    "rabbitmq|messaging|linux",
    "netdata|monitoring|linux",
    "ensurance-backend|application|linux",
    "pharmacy-backend|application|linux",
    "ensurance-frontend|application|linux",
    "pharmacy-frontend|application|linux",
]

# Tags de hosts
host_tags = {
    "prometheus": ["monitoring", "critical"],
    "grafana": ["monitoring", "important"],
    "alertmanager": ["monitoring", "critical"],
    "node-exporter": ["monitoring", "important"],
    "pushgateway": ["monitoring", "normal"],
    "rabbitmq": ["messaging", "critical"],
    "netdata": ["monitoring", "important"],
    "ensurance-backend": ["application", "critical"],
    "pharmacy-backend": ["application", "critical"],
    "ensurance-frontend": ["application", "important"],
    "pharmacy-frontend": ["application", "important"],
}

# Configuración de checks especiales
extra_host_conf["check_mk_agent_target_versions"] = [
    ("2.2.0", [], ALL_HOSTS),
]

# Configuración de servicios activos
active_checks = {
    # HTTP checks para servicios web
    "http": [
        {
            "service_description": "Prometheus HTTP",
            "host_name": ["prometheus"],
            "name": "Prometheus",
            "host": {"address": ("direct", "ensurance-prometheus-full")},
            "mode": ("url", {"uri": "/", "port": 9090}),
        },
        {
            "service_description": "Grafana HTTP",
            "host_name": ["grafana"],
            "name": "Grafana",
            "host": {"address": ("direct", "ensurance-grafana-full")},
            "mode": ("url", {"uri": "/", "port": 3302}),
        },
        {
            "service_description": "AlertManager HTTP",
            "host_name": ["alertmanager"],
            "name": "AlertManager",
            "host": {"address": ("direct", "ensurance-alertmanager-full")},
            "mode": ("url", {"uri": "/", "port": 9093}),
        },
        {
            "service_description": "RabbitMQ Management",
            "host_name": ["rabbitmq"],
            "name": "RabbitMQ",
            "host": {"address": ("direct", "ensurance-rabbitmq-full")},
            "mode": ("url", {"uri": "/", "port": 15672}),
        },
        {
            "service_description": "Netdata HTTP",
            "host_name": ["netdata"],
            "name": "Netdata",
            "host": {"address": ("direct", "ensurance-netdata-full")},
            "mode": ("url", {"uri": "/", "port": 19999}),
        },
    ],
}

# Configuración de Prometheus como datasource
datasource_programs = [
    {
        "id": "prometheus",
        "title": "Prometheus Metrics",
        "program": "/omd/sites/ensurance/local/bin/check_prometheus",
    }
]

# Umbrales personalizados (iguales a Netdata)
checkgroup_parameters.setdefault("cpu_utilization", [])
checkgroup_parameters["cpu_utilization"] = [
    {
        "condition": {},
        "value": {
            "levels": (70.0, 90.0),  # WARNING: 70%, CRITICAL: 90%
        },
    }
] + checkgroup_parameters["cpu_utilization"]

checkgroup_parameters.setdefault("memory_percentage_used", [])
checkgroup_parameters["memory_percentage_used"] = [
    {
        "condition": {},
        "value": {
            "levels": (80.0, 95.0),  # WARNING: 80%, CRITICAL: 95%
        },
    }
] + checkgroup_parameters["memory_percentage_used"]

checkgroup_parameters.setdefault("disk_usage", [])
checkgroup_parameters["disk_usage"] = [
    {
        "condition": {},
        "value": {
            "levels": (75.0, 90.0),  # WARNING: 75%, CRITICAL: 90%
        },
    }
] + checkgroup_parameters["disk_usage"]

# Configuración de gráficas personalizadas (replicando Netdata)
custom_graphs["ensurance_cpu"] = {
    "title": "CPU Usage - Ensurance",
    "metrics": [
        ("user", "area"),
        ("system", "stack"),
        ("wait", "stack"),
        ("steal", "stack"),
    ],
    "range": (0, 100),
    "scalars": [
        ("warn", 70),
        ("crit", 90),
    ],
}

custom_graphs["ensurance_memory"] = {
    "title": "Memory Usage - Ensurance",
    "metrics": [
        ("mem_used", "area"),
        ("mem_cached", "stack"),
        ("mem_buffered", "stack"),
    ],
    "scalars": [
        ("warn", 80),
        ("crit", 95),
    ],
}

custom_graphs["ensurance_disk"] = {
    "title": "Disk Usage - Ensurance",
    "metrics": [
        ("disk_used", "area"),
    ],
    "range": (0, 100),
    "scalars": [
        ("warn", 75),
        ("crit", 90),
    ],
}

custom_graphs["ensurance_network"] = {
    "title": "Network Traffic - Ensurance",
    "metrics": [
        ("if_in_octets", "area"),
        ("if_out_octets", "-area"),
    ],
}

# Configuración de notificaciones (integración con AlertManager)
notification_parameters += [
    {
        "description": "Send to AlertManager",
        "notify_plugin": ("webhook", {
            "url": "http://ensurance-alertmanager-full:9093/api/v1/alerts",
            "method": "post",
        }),
    }
]

# Intervalos de check (igual que Netdata)
extra_host_conf.setdefault("check_interval", [])
extra_host_conf["check_interval"] = [
    (1.0, ["monitoring"], ALL_HOSTS),  # 1 minuto para servicios de monitoreo
    (2.0, ["application"], ALL_HOSTS),  # 2 minutos para aplicaciones
]

# Configuración de performance data para Prometheus
extra_host_conf.setdefault("enable_perfdata", [])
extra_host_conf["enable_perfdata"] = [
    (True, [], ALL_HOSTS),
]
