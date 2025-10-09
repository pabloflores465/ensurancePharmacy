#!/bin/sh
set -e

echo "Cleaning previous JMeter results..."
rm -rf /results/report/*
rm -f /results/results.jtl

echo "Starting JMeter test..."
/entrypoint.sh "$@"

echo ""
echo "========================================"
echo "JMeter test completed!"
echo "Report generated at /results/report"
echo "========================================"
echo ""
echo "To view the report, start the report server:"
echo "  docker compose -f docker-compose.stress.yml up -d jmeter-report"
echo "  Then open: http://localhost:8085"
echo ""
