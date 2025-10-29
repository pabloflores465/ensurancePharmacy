#!/bin/bash

# Script para iniciar SOLO los contenedores existentes
# No crea volúmenes ni redes nuevas
# Uso: ./start-containers-only.sh

set -e

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}🚀 INICIAR CONTENEDORES EXISTENTES${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Verificar que Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Error: Docker no está corriendo${NC}"
    echo "Por favor inicia Docker Desktop primero"
    exit 1
fi

echo -e "${GREEN}✓${NC} Docker está corriendo"
echo ""

# Verificar si existe el archivo docker-compose
if [ ! -f "docker-compose.full.yml" ]; then
    echo -e "${RED}❌ Error: docker-compose.full.yml no encontrado${NC}"
    echo "Ejecuta este script desde el directorio del proyecto"
    exit 1
fi

echo -e "${BLUE}[INFO]${NC} Iniciando contenedores existentes..."
echo ""

# Iniciar contenedores sin recrear volúmenes ni redes
docker compose -f docker-compose.full.yml start

echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ CONTENEDORES INICIADOS${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Mostrar estado
echo -e "${BLUE}📊 Estado de los contenedores:${NC}"
echo ""
docker ps --filter "name=ensurance.*-full" --format "table {{.Names}}\t{{.Status}}" | head -20

echo ""
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}🌐 URLs PRINCIPALES${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "📱 Aplicaciones:"
echo "  • Frontend Ensurance:  http://localhost:3100"
echo "  • Frontend Pharmacy:   http://localhost:3101"
echo "  • Backend Ensurance:   http://localhost:3102"
echo "  • Backend Pharmacy:    http://localhost:3103"
echo ""
echo "📈 Monitoreo:"
echo "  • Prometheus:          http://localhost:9090"
echo "  • Grafana:             http://localhost:3302 (admin/changeme)"
echo "  • CheckMK:             http://localhost:5152 (cmkadmin/changeme)"
echo ""
echo "🔧 CI/CD:"
echo "  • Jenkins:             http://localhost:8080"
echo "  • SonarQube:           http://localhost:9000 (admin/admin)"
echo "  • Drone:               http://localhost:8002"
echo ""
echo "🛠️  Herramientas:"
echo "  • Portainer:           https://localhost:60002"
echo "  • RabbitMQ:            http://localhost:15674 (admin/changeme)"
echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}💡 COMANDOS ÚTILES${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo "  • Ver logs:            docker compose -f docker-compose.full.yml logs -f"
echo "  • Detener:             docker compose -f docker-compose.full.yml stop"
echo "  • Reiniciar:           docker compose -f docker-compose.full.yml restart"
echo "  • Estado:              docker ps"
echo ""
