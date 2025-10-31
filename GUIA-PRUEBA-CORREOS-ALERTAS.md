# Guía de Prueba de Correos y Alertas

## 🎯 Objetivo

Esta guía te ayudará a:
1. **Verificar** que los correos se están enviando correctamente desde Alertmanager
2. **Probar alertas específicas** según tus necesidades

---

## 📧 Parte 1: Verificar Envío de Correos

### Problema Común
Alertmanager reporta que envía correos, pero no llegas a recibirlos.

### Script de Diagnóstico

```bash
./test-email-alertmanager.sh
```

### ¿Qué hace este script?

1. ✅ Verifica que Alertmanager está corriendo
2. 📧 Envía una alerta de prueba
3. 🔍 Revisa logs de SMTP
4. 🔌 Prueba conectividad con Gmail
5. 📋 Muestra configuración actual

### Interpretación de Resultados

#### ✅ **Si el envío es exitoso**
Verás en los logs:
```
level=info msg="Notify successful"
```
- **Acción:** Revisa tu bandeja de entrada y **carpeta de SPAM**

#### ❌ **Si hay error de autenticación**
Verás:
```
level=error msg="notify failed" err="authentication failed"
```

**Causas posibles:**
- Password incorrecta o expirada
- No es una "App Password" de Gmail
- Autenticación de 2 pasos no habilitada

**Solución:**
1. Ve a: https://myaccount.google.com/apppasswords
2. Genera una nueva "App Password"
3. Actualiza el archivo `monitoring/alertmanager/alertmanager.yml.template`:
   ```yaml
   global:
     smtp_auth_password: 'TU_NUEVA_APP_PASSWORD_AQUI'
   ```
4. Reinicia Alertmanager:
   ```bash
   docker compose -f docker-compose.full.yml restart alertmanager
   ```

#### ❌ **Si hay error de conexión**
Verás:
```
level=error msg="notify failed" err="dial tcp: connection refused"
```

**Causas posibles:**
- Firewall bloqueando puerto 587
- Proxy corporativo
- Red sin acceso a internet

**Solución:**
```bash
# Probar conectividad
telnet smtp.gmail.com 587

# Si no funciona, verifica firewall
sudo ufw status
```

---

## 🧪 Parte 2: Probar Alertas Específicas

### Script Interactivo

```bash
./test-alertas-interactivo.sh
```

### Características

✅ **Menú interactivo** para seleccionar categorías
✅ **Selección múltiple** de categorías
✅ **Estimación de tiempo** según selección
✅ **Colores** para mejor visualización
✅ **Resumen** antes de ejecutar

### Categorías Disponibles

| # | Categoría | Alertas | Tiempo Estimado |
|---|-----------|---------|-----------------|
| 1 | **Sistema** | 11 alertas | ~8-12 min |
| 2 | **Aplicaciones** | 8 alertas | ~6-9 min |
| 3 | **RabbitMQ** | 12 alertas | ~10-15 min |
| 4 | **K6** | 8 alertas | ~7-10 min |
| 5 | **CI/CD** | 12 alertas | ~12-18 min |
| 6 | **Monitoreo** | 13 alertas | ~8-12 min |
| 7 | **TODAS** | 64 alertas | ~45-60 min |

### Ejemplo de Uso

#### Escenario 1: Solo probar alertas de aplicaciones
```bash
./test-alertas-interactivo.sh
# Selecciona: 2 (Aplicaciones)
# Luego: 0 (Comenzar)
```

#### Escenario 2: Probar sistema y monitoreo
```bash
./test-alertas-interactivo.sh
# Selecciona: 1 (Sistema)
# Selecciona: 6 (Monitoreo)
# Luego: 0 (Comenzar)
```

#### Escenario 3: Prueba completa
```bash
./test-alertas-interactivo.sh
# Selecciona: 7 (TODAS)
# Luego: 0 (Comenzar)
```

---

## 🔍 Verificar Resultados

### 1. Ver alertas activas en Prometheus
```bash
curl -s http://localhost:9090/api/v1/alerts | jq '.data.alerts[] | {alert: .labels.alertname, state: .state}'
```

### 2. Ver alertas en Alertmanager
```bash
curl -s http://localhost:9094/api/v1/alerts | jq '.data[] | {alertname: .labels.alertname, status: .status.state}'
```

### 3. Ver logs de Alertmanager en tiempo real
```bash
docker logs -f ensurance-alertmanager-full
```

### 4. Verificar correos
- **Gmail:** pablopolis2016@gmail.com
- **Unis:** jflores@unis.edu.gt
- **⚠️ Importante:** Revisa carpeta de SPAM

### 5. Ver alertas en UI
- **Prometheus:** http://localhost:9090
- **Alertmanager:** http://localhost:9094
- **Grafana:** http://localhost:3302

---

## 🐛 Solución de Problemas Comunes

### Problema 1: No llegan correos pero Alertmanager dice que envía

**Diagnóstico:**
```bash
./test-email-alertmanager.sh
```

**Soluciones:**
1. ✅ Verificar carpeta de SPAM
2. 🔑 Regenerar App Password de Gmail
3. 📧 Verificar que los correos en configuración son correctos
4. 🔄 Reiniciar Alertmanager después de cambios

### Problema 2: Alertas no se disparan

**Causas:**
- Umbrales demasiado altos
- Servicios no están exponiendo métricas
- Prometheus no está scrapeando correctamente

**Verificar:**
```bash
# Ver targets de Prometheus
curl -s http://localhost:9090/api/v1/targets | jq '.data.activeTargets[] | {job: .labels.job, health: .health}'

# Ver métricas disponibles
curl -s http://localhost:9090/api/v1/label/__name__/values | jq '.data[]' | grep -i cpu
```

### Problema 3: Script falla con "stress-ng: command not found"

**Solución:**
```bash
sudo apt-get update
sudo apt-get install -y stress-ng
```

### Problema 4: Docker compose no encuentra servicios

**Verificar que los servicios están corriendo:**
```bash
docker compose -f docker-compose.full.yml ps
```

**Levantar servicios necesarios:**
```bash
docker compose -f docker-compose.full.yml up -d
```

---

## 📋 Checklist de Configuración de Email

- [ ] Autenticación de 2 pasos habilitada en Gmail
- [ ] App Password generada
- [ ] App Password actualizada en `alertmanager.yml`
- [ ] Correos destinatarios verificados
- [ ] Alertmanager reiniciado después de cambios
- [ ] Conectividad SMTP verificada (puerto 587)
- [ ] Prueba de email enviada con script
- [ ] Logs de Alertmanager revisados
- [ ] Carpeta de SPAM verificada

---

## 🔄 Flujo Recomendado

1. **Primero:** Verificar configuración de email
   ```bash
   ./test-email-alertmanager.sh
   ```

2. **Segundo:** Probar una categoría pequeña
   ```bash
   ./test-alertas-interactivo.sh
   # Selecciona categoría 2 (Aplicaciones)
   ```

3. **Tercero:** Verificar que llegas correos

4. **Finalmente:** Ejecutar prueba completa si es necesario
   ```bash
   ./test-alertas-interactivo.sh
   # Selecciona opción 7 (TODAS)
   ```

---

## 📞 Soporte Adicional

### Ver toda la configuración de Alertmanager
```bash
docker exec ensurance-alertmanager-full cat /etc/alertmanager/alertmanager.yml
```

### Recargar configuración sin reiniciar
```bash
docker exec ensurance-alertmanager-full killall -HUP alertmanager
```

### Ver métricas de Alertmanager
```bash
curl -s http://localhost:9094/metrics | grep alertmanager_notifications
```

---

## 🎯 Resumen Rápido

### Para verificar correos:
```bash
./test-email-alertmanager.sh
```

### Para probar alertas específicas:
```bash
./test-alertas-interactivo.sh
```

### Para prueba completa (script original):
```bash
./test-todas-las-alertas-completo.sh
```

---

## 📚 Referencias

- **Gmail App Passwords:** https://myaccount.google.com/apppasswords
- **Alertmanager Docs:** https://prometheus.io/docs/alerting/latest/alertmanager/
- **SMTP Troubleshooting:** https://support.google.com/mail/answer/7126229
