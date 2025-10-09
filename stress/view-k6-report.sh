#!/bin/bash

# view-k6-report.sh - Start K6 HTML Dashboard Server
# Usage: ./view-k6-report.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="$SCRIPT_DIR/../scripts/docker-compose.stress.yml"

echo "========================================="
echo "   K6 HTML Dashboard Viewer"
echo "========================================="
echo ""

# Check if k6 results exist
if ! docker volume ls | grep -q "scripts_k6_results"; then
    echo "❌ No K6 results found!"
    echo ""
    echo "Run a K6 test first:"
    echo "  cd $SCRIPT_DIR/../scripts"
    echo "  TEST_SCRIPT=sample-script.js docker-compose -f docker-compose.stress.yml run --rm k6"
    echo ""
    exit 1
fi

# Check if dashboard was exported
DASHBOARD_EXISTS=$(docker run --rm -v scripts_k6_results:/results alpine sh -c "test -f /results/k6-dashboard && echo 'yes' || echo 'no'")

if [ "$DASHBOARD_EXISTS" = "no" ]; then
    echo "❌ K6 dashboard not found in results!"
    echo ""
    echo "The dashboard is exported when you run a K6 test."
    echo "Make sure the test completed successfully."
    exit 1
fi

echo "✅ K6 dashboard found!"
echo ""

# Check if k6-report container is already running
if docker ps | grep -q "ensurance-k6-report"; then
    echo "✅ K6 report server is already running!"
else
    echo "🚀 Starting K6 report server..."
    cd "$SCRIPT_DIR/../scripts"
    docker-compose -f docker-compose.stress.yml up -d k6-report
    echo "✅ Server started!"
fi

echo ""
echo "========================================="
echo "📊 K6 Dashboard Available:"
echo "========================================="
echo ""
echo "  🌐 Dashboard: http://localhost:5666/k6-dashboard"
echo "  📁 All Files: http://localhost:5666"
echo ""
echo "Opening dashboard in browser..."
sleep 2

# Open in browser
if command -v xdg-open &> /dev/null; then
    xdg-open http://localhost:5666/k6-dashboard
elif command -v open &> /dev/null; then
    open http://localhost:5666/k6-dashboard
else
    echo "Please open manually: http://localhost:5666/k6-dashboard"
fi

echo ""
echo "========================================="
echo "ℹ️  Additional Commands:"
echo "========================================="
echo ""
echo "  Stop server:"
echo "    docker stop ensurance-k6-report"
echo ""
echo "  View logs:"
echo "    docker logs ensurance-k6-report"
echo ""
echo "  Run another test:"
echo "    cd $SCRIPT_DIR && ./run-tests.sh"
echo ""
