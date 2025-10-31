#!/bin/bash

# Script para aplicar configuración de emails personalizados por alerta

echo "=========================================="
echo "📧 APLICAR EMAILS PERSONALIZADOS"
echo "=========================================="
echo ""

echo "Este script reemplazará la configuración actual de Alertmanager"
echo "con una configuración que envía emails personalizados para cada alerta."
echo ""
echo "Cambios principales:"
echo "  ✅ Asunto personalizado con nombre de la alerta"
echo "  ✅ HTML rico con información específica de cada alerta"
echo "  ✅ Diseño visual diferente para CRITICAL, WARNING e INFO"
echo "  ✅ Enlaces directos a dashboards"
echo "  ✅ Acciones recomendadas específicas"
echo "  ✅ Timeline detallada"
echo "  ✅ Todas las labels y annotations disponibles"
echo ""

read -p "¿Deseas continuar? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Operación cancelada"
    exit 1
fi

# Backup de configuración actual
echo ""
echo "📦 Creando backup de configuración actual..."
BACKUP_FILE="monitoring/alertmanager/alertmanager.yml.backup.$(date +%Y%m%d_%H%M%S)"
cp monitoring/alertmanager/alertmanager.yml.template "$BACKUP_FILE"
echo "✅ Backup creado: $BACKUP_FILE"

# Copiar nueva configuración
echo ""
echo "📝 Aplicando nueva configuración personalizada..."
cp monitoring/alertmanager/alertmanager-personalizado.yml monitoring/alertmanager/alertmanager.yml
echo "✅ Configuración actualizada"

# Reiniciar Alertmanager
echo ""
echo "🔄 Reiniciando Alertmanager..."
docker compose -f docker-compose.full.yml restart alertmanager
sleep 5
echo "✅ Alertmanager reiniciado"

# Verificar que Alertmanager está corriendo
echo ""
echo "🔍 Verificando estado de Alertmanager..."
if curl -s http://localhost:9094/api/v1/status > /dev/null 2>&1; then
    echo "✅ Alertmanager está funcionando correctamente"
else
    echo "❌ Error: Alertmanager no responde"
    echo "Restaurando backup..."
    cp "$BACKUP_FILE" monitoring/alertmanager/alertmanager.yml
    docker compose -f docker-compose.full.yml restart alertmanager
    exit 1
fi

# Mostrar configuración
echo ""
echo "=========================================="
echo "✅ CONFIGURACIÓN APLICADA EXITOSAMENTE"
echo "=========================================="
echo ""

echo "📧 Tipos de emails configurados:"
echo ""
echo "1. 🔴 CRITICAL - Email Rojo"
echo "   - Asunto: 🔴 [CRÍTICO] [NombreAlerta] - Resumen"
echo "   - Diseño: Banner rojo pulsante, urgente"
echo "   - Incluye: Timeline, acción inmediata, enlaces"
echo ""
echo "2. ⚠️ WARNING - Email Naranja"
echo "   - Asunto: ⚠️ [WARNING] [NombreAlerta] - Resumen"
echo "   - Diseño: Banner naranja, advertencia"
echo "   - Incluye: Descripción, acción recomendada, links"
echo ""
echo "3. ℹ️ INFO - Email Azul"
echo "   - Asunto: ℹ️ [INFO] [NombreAlerta] - Resumen"
echo "   - Diseño: Banner azul, informativo"
echo "   - Incluye: Información básica, timestamp"
echo ""

echo "📋 Cada email incluye:"
echo "  • Nombre específico de la alerta en el asunto"
echo "  • Descripción personalizada de cada alerta"
echo "  • Resumen (summary annotation)"
echo "  • Acción recomendada (action annotation)"
echo "  • Servicio, componente, instancia afectada"
echo "  • Enlaces a dashboards específicos"
echo "  • Timeline completa"
echo "  • Todas las labels técnicas"
echo ""

echo "🧪 Para probar los emails personalizados:"
echo "  ./test-alertas-interactivo.sh"
echo ""

echo "💬 Slack también está configurado con mensajes personalizados"
echo "   (Recuerda configurar el webhook URL)"
echo ""

echo "📊 Ver alertas en:"
echo "  • Prometheus: http://localhost:9090"
echo "  • Alertmanager: http://localhost:9094"
echo ""

echo "=========================================="
echo "¡LISTO!"
echo "=========================================="
