#!/bin/bash

set -e

echo "📊 Iniciando sistema completo con métricas Prometheus"
echo "======================================================"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Directorio raíz
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Compilar backends
echo -e "\n${BLUE}📦 Compilando backends...${NC}"
echo "Compilando backv5 (Pharmacy Backend)..."
(cd "$ROOT_DIR/backv5" && mvn clean package -DskipTests) || {
    echo -e "${YELLOW}⚠️  Error compilando backv5. Asegúrate de tener Maven instalado.${NC}"
    exit 1
}

echo "Compilando backv4 (Ensurance Backend)..."
(cd "$ROOT_DIR/backv4" && mvn clean package -DskipTests) || {
    echo -e "${YELLOW}⚠️  Error compilando backv4. Asegúrate de tener Maven instalado.${NC}"
    exit 1
}

# Instalar dependencias frontend si es necesario
echo -e "\n${BLUE}📦 Verificando dependencias frontend...${NC}"
if [ ! -d "$ROOT_DIR/ensurance/node_modules" ]; then
    echo "Instalando dependencias de ensurance..."
    (cd "$ROOT_DIR/ensurance" && npm install)
fi

if [ ! -d "$ROOT_DIR/pharmacy/node_modules" ]; then
    echo "Instalando dependencias de pharmacy..."
    (cd "$ROOT_DIR/pharmacy" && npm install)
fi

# Función para cleanup al salir
cleanup() {
    echo -e "\n${YELLOW}🛑 Deteniendo todos los servicios...${NC}"
    pkill -P $$
    exit 0
}

trap cleanup SIGINT SIGTERM

# Iniciar backends con métricas
echo -e "\n${BLUE}🚀 Iniciando backends con métricas...${NC}"
echo "Iniciando backv5 en puerto 8082 (métricas en 9464)..."
METRICS_HOST=0.0.0.0 METRICS_PORT=9464 SERVER_PORT=8082 java --enable-preview -jar "$ROOT_DIR/backv5/target/backv5-1.0-SNAPSHOT.jar" > "$ROOT_DIR/backv5.log" 2>&1 &
BACKV5_PID=$!

echo "Iniciando backv4 en puerto 8081 (métricas en 9465)..."
METRICS_HOST=0.0.0.0 METRICS_PORT=9465 SERVER_PORT=8081 java --enable-preview -jar "$ROOT_DIR/backv4/target/backv4-1.0-SNAPSHOT.jar" > "$ROOT_DIR/backv4.log" 2>&1 &
BACKV4_PID=$!

# Esperar a que backends inicien
echo "Esperando a que los backends inicien..."
sleep 5

# Iniciar servidores de métricas de frontend
echo -e "\n${BLUE}📊 Iniciando servidores de métricas frontend...${NC}"
echo "Iniciando metrics server de ensurance (puerto 9466)..."
(cd "$ROOT_DIR/ensurance" && METRICS_HOST=0.0.0.0 METRICS_PORT=9466 npm run metrics) > "$ROOT_DIR/ensurance-metrics.log" 2>&1 &
ENSURANCE_METRICS_PID=$!

echo "Iniciando metrics server de pharmacy (puerto 9467)..."
(cd "$ROOT_DIR/pharmacy" && METRICS_HOST=0.0.0.0 METRICS_PORT=9467 npm run metrics) > "$ROOT_DIR/pharmacy-metrics.log" 2>&1 &
PHARMACY_METRICS_PID=$!

# Iniciar aplicaciones frontend
echo -e "\n${BLUE}🌐 Iniciando aplicaciones frontend...${NC}"
echo "Iniciando ensurance frontend..."
(cd "$ROOT_DIR/ensurance" && npm run dev) > "$ROOT_DIR/ensurance-dev.log" 2>&1 &
ENSURANCE_PID=$!

echo "Iniciando pharmacy frontend..."
(cd "$ROOT_DIR/pharmacy" && npm run serve) > "$ROOT_DIR/pharmacy-dev.log" 2>&1 &
PHARMACY_PID=$!

# Esperar a que todo inicie
echo -e "\n${YELLOW}⏳ Esperando a que todos los servicios inicien...${NC}"
sleep 10

# Mostrar información
echo -e "\n${GREEN}✅ Todos los servicios están corriendo!${NC}"
echo ""
echo "======================================================"
echo "📊 ENDPOINTS DE MÉTRICAS PROMETHEUS"
echo "======================================================"
echo "  backv5 (Pharmacy):     http://localhost:9464/metrics"
echo "  backv4 (Ensurance):    http://localhost:9465/metrics"
echo "  ensurance (Frontend):  http://localhost:9466/metrics"
echo "  pharmacy (Frontend):   http://localhost:9467/metrics"
echo ""
echo "======================================================"
echo "🌐 APLICACIONES"
echo "======================================================"
echo "  backv5 API:            http://localhost:8082/api2"
echo "  backv4 API:            http://localhost:8081/api"
echo "  ensurance Frontend:    http://localhost:5173"
echo "  pharmacy Frontend:     http://localhost:8080"
echo ""
echo "======================================================"
echo "📝 LOGS"
echo "======================================================"
echo "  backv5:                tail -f backv5.log"
echo "  backv4:                tail -f backv4.log"
echo "  ensurance metrics:     tail -f ensurance-metrics.log"
echo "  pharmacy metrics:      tail -f pharmacy-metrics.log"
echo "  ensurance dev:         tail -f ensurance-dev.log"
echo "  pharmacy dev:          tail -f pharmacy-dev.log"
echo ""
echo "======================================================"
echo -e "${YELLOW}Presiona Ctrl+C para detener todos los servicios${NC}"
echo "======================================================"

# Verificar que los endpoints de métricas responden
echo -e "\n${BLUE}🔍 Verificando endpoints de métricas...${NC}"
sleep 5

check_endpoint() {
    local url=$1
    local name=$2
    if curl -s "$url" > /dev/null 2>&1; then
        echo -e "  ${GREEN}✓${NC} $name: OK"
    else
        echo -e "  ${YELLOW}✗${NC} $name: No responde (puede tardar en iniciar)"
    fi
}

check_endpoint "http://localhost:9464/metrics" "backv5"
check_endpoint "http://localhost:9465/metrics" "backv4"
check_endpoint "http://localhost:9466/metrics" "ensurance"
check_endpoint "http://localhost:9467/metrics" "pharmacy"

echo -e "\n${GREEN}Sistema listo!${NC}"

# Mantener el script corriendo
wait
