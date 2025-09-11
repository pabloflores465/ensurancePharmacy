# Escenarios de Calidad - Proyecto Final Farmacia y Seguros

## Descripción General

Este documento presenta tres escenarios de calidad críticos para el sistema integrado de Farmacia y Seguros, basados en los atributos de calidad más importantes para garantizar el correcto funcionamiento del sistema en producción.

---

## Escenario de Calidad #1: Rendimiento del Sistema

| Elemento | Instrucción |
|----------|-------------|
| **Estímulo** | Un usuario del sistema de farmacia realiza una búsqueda de medicamentos disponibles durante las horas pico de operación (10:00 AM - 12:00 PM) |
| **Fuente del estímulo** | Usuario autenticado del sistema de farmacia accediendo desde la interfaz web |
| **Ambiente** | Sistema en producción con carga normal de trabajo, base de datos SQLite con 10,000+ registros de medicamentos, 50 usuarios concurrentes |
| **Respuesta** | El sistema procesa la consulta, accede a la base de datos, filtra los resultados según disponibilidad y presenta la lista de medicamentos al usuario |
| **Medida de respuesta** | La búsqueda debe completarse en menos de 2 segundos en el 95% de los casos, con un tiempo máximo de respuesta de 5 segundos |

---

## Escenario de Calidad #2: Seguridad de Autenticación

| Elemento | Instrucción |
|----------|-------------|
| **Estímulo** | Un usuario intenta acceder al sistema de seguros con credenciales incorrectas de forma repetitiva (más de 3 intentos fallidos en 5 minutos) |
| **Fuente del estímulo** | Potencial atacante o usuario legítimo con credenciales olvidadas intentando acceso desde internet |
| **Ambiente** | Sistema en producción con conexión a internet, módulo de autenticación activo, logs de seguridad habilitados |
| **Respuesta** | El sistema detecta los intentos fallidos, bloquea temporalmente la cuenta, registra el evento en los logs de seguridad y notifica al administrador |
| **Medida de respuesta** | La cuenta debe bloquearse automáticamente por 15 minutos después del tercer intento fallido, el evento debe registrarse en menos de 1 segundo, y la notificación debe enviarse en menos de 30 segundos |

---

## Escenario de Calidad #3: Disponibilidad del Sistema

| Elemento | Instrucción |
|----------|-------------|
| **Estímulo** | El servicio backend de seguros (BackV4) experimenta una falla inesperada durante el procesamiento de una reclamación de seguro |
| **Fuente del estímulo** | Falla interna del sistema (error de base de datos, excepción no manejada, o sobrecarga de memoria) |
| **Ambiente** | Sistema en producción con arquitectura de microservicios, contenedores Docker activos, monitoreo de salud habilitado |
| **Respuesta** | El sistema detecta la falla, registra el error, intenta reiniciar el servicio automáticamente, y si es necesario, redirige las solicitudes a un servicio de respaldo o modo degradado |
| **Medida de respuesta** | El tiempo de inactividad no debe exceder 30 segundos, la detección de falla debe ocurrir en menos de 10 segundos, y el sistema debe mantener 99.9% de disponibilidad mensual |

---

## Consideraciones de Implementación

### Escenario #1 - Rendimiento
- **Monitoreo**: Implementar métricas de tiempo de respuesta en todos los endpoints
- **Optimización**: Índices en base de datos SQLite para consultas frecuentes
- **Caching**: Implementar cache en memoria para consultas repetitivas

### Escenario #2 - Seguridad
- **Implementación**: Sistema de rate limiting y bloqueo de cuentas
- **Logging**: Registro detallado de eventos de seguridad
- **Alertas**: Notificaciones automáticas para actividad sospechosa

### Escenario #3 - Disponibilidad
- **Health Checks**: Endpoints de salud para monitoreo continuo
- **Failover**: Mecanismos de recuperación automática
- **Redundancia**: Servicios de respaldo para componentes críticos

---

## Métricas de Calidad Objetivo

| Atributo | Métrica Objetivo | Método de Medición |
|----------|------------------|-------------------|
| **Rendimiento** | < 2 seg (95% casos) | Logs de aplicación + APM |
| **Seguridad** | 0 brechas críticas | Análisis SonarQube + Auditorías |
| **Disponibilidad** | 99.9% uptime mensual | Monitoreo continuo + Alertas |

---

*Documento creado para el Proyecto Final del Curso - Sistema Integrado Farmacia y Seguros*
*Fecha: Septiembre 2025*
