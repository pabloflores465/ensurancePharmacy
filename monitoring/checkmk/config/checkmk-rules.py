#!/usr/bin/env python3
# CheckMK Rules Configuration - Replica las mismas alertas que Netdata

# System Monitoring Rules
checkgroup_parameters.setdefault('cpu_utilization', [])
checkgroup_parameters['cpu_utilization'] = [
    ({'levels': (70.0, 90.0)}, [], ALL_HOSTS, {}),
]

checkgroup_parameters.setdefault('memory_linux', [])
checkgroup_parameters['memory_linux'] = [
    ({'levels_ram': (80.0, 95.0), 'levels_swap': (20.0, 50.0)}, [], ALL_HOSTS, {}),
]

checkgroup_parameters.setdefault('filesystem', [])
checkgroup_parameters['filesystem'] = [
    ({'levels': (75.0, 90.0), 'levels_low': (10000, 5000)}, [], ALL_HOSTS, {}),
]

print("CheckMK rules loaded")
