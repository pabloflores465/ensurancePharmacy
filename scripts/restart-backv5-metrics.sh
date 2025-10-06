#!/bin/bash

# Script para reiniciar backv5 con servidor de métricas

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔧 Reiniciando backv5 con servidor de métricas...${NC}"

# Directorio del script y directorio raíz del proyecto
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# 1. Encontrar y detener todos los procesos backv5 corriendo
echo -e "${YELLOW}📍 Buscando procesos backv5...${NC}"
BACKV5_PIDS=$(ps aux | grep "java.*8082.*pharmacy-backend" | grep -v grep | awk '{print $2}' || true)

if [ ! -z "$BACKV5_PIDS" ]; then
    echo -e "${YELLOW}🛑 Deteniendo procesos backv5: $BACKV5_PIDS${NC}"
    echo "$BACKV5_PIDS" | xargs kill -9 2>/dev/null || true
    sleep 2
    echo -e "${GREEN}✅ Procesos backv5 detenidos${NC}"
else
    echo -e "${BLUE}ℹ️  No hay procesos backv5 corriendo${NC}"
fi

# 2. Verificar que el JAR existe
BACKV5_JAR="$ROOT_DIR/backv5/target/backv5-1.0-SNAPSHOT.jar"
if [ ! -f "$BACKV5_JAR" ]; then
    echo -e "${RED}❌ JAR no encontrado: $BACKV5_JAR${NC}"
    echo -e "${YELLOW}📦 Compilando backv5...${NC}"
    (cd "$ROOT_DIR/backv5" && mvn clean package -DskipTests)
fi

# 3. Crear directorios necesarios
mkdir -p "$ROOT_DIR/logs"
mkdir -p "$ROOT_DIR/databases/pharmacy"

# 4. Iniciar backv5 con servidor de métricas
echo -e "${GREEN}🚀 Iniciando backv5 con métricas en puerto 9464...${NC}"

export METRICS_HOST=0.0.0.0
export METRICS_PORT=9464
export SERVER_PORT=8082

# Iniciar en background
nohup java --enable-preview -jar "$BACKV5_JAR" > "$ROOT_DIR/logs/backv5.log" 2>&1 &
BACKV5_PID=$!

echo -e "${GREEN}✅ backv5 iniciado con PID: $BACKV5_PID${NC}"

# 5. Esperar a que el servidor inicie
echo -e "${BLUE}⏳ Esperando a que backv5 inicie...${NC}"
sleep 10

# 6. Verificar que los endpoints responden
echo -e "${BLUE}🔍 Verificando endpoints...${NC}"

# Verificar API
if curl -s http://localhost:8082/api2/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ API backv5: http://localhost:8082/api2 - OK${NC}"
else
    echo -e "${YELLOW}⚠️  API backv5: No responde aún (puede tardar en iniciar)${NC}"
fi

# Verificar métricas
if curl -s http://localhost:9464/metrics | grep -q "jvm_memory"; then
    echo -e "${GREEN}✅ Métricas backv5: http://localhost:9464/metrics - OK${NC}"
else
    echo -e "${RED}❌ Métricas backv5: No responde${NC}"
    echo -e "${YELLOW}📋 Ver logs: tail -f $ROOT_DIR/logs/backv5.log${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ backv5 reiniciado exitosamente con métricas${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${BLUE}📊 Endpoints disponibles:${NC}"
echo -e "  • API:      http://localhost:8082/api2"
echo -e "  • Métricas: http://localhost:9464/metrics"
echo ""
echo -e "${BLUE}📋 Comandos útiles:${NC}"
echo -e "  • Ver logs:    tail -f $ROOT_DIR/logs/backv5.log"
echo -e "  • Ver proceso: ps aux | grep backv5"
echo -e "  • Detener:     kill $BACKV5_PID"
echo ""
