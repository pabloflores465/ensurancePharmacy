#!/bin/bash
# Script para visualizar reportes HTML de JMeter

echo "Iniciando servidor HTTP para ver reportes JMeter..."
echo "Abrirá en http://localhost:8085"
echo "Presiona Ctrl+C para detener"
echo ""

docker run --rm -v scripts_jmeter_results:/results -p 8085:8085 \
    -w /results/report python:3.9 python -m http.server 8085
