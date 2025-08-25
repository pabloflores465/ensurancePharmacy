# pharmacy

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

---

# Pharmacy Frontend — Guía Unificada

Este documento unifica cómo correr, testear, construir y desplegar el frontend de Pharmacy, e integra la configuración de puertos previamente documentada en `README-PUERTOS.md`.

## Requisitos

- Node.js (recomendado LTS 18+)
- npm
- Python 3 (usado por `getip.py` en desarrollo)
- Docker y Docker Compose (opcional)

## Instalación

```bash
npm install
```

## Desarrollo (local)

```bash
npm run serve
```

Este comando ejecuta `python3 getip.py` y levanta Vue CLI dev server.

Variables de entorno útiles (archivo `.env` o variables del sistema):

- `VUE_APP_PHARMACY_API_URL`  (p. ej.: `http://localhost:8082/api2`)
- `VUE_APP_ENSURANCE_API_URL` (p. ej.: `http://localhost:8081/api`)

## Pruebas

```bash
npm run test:unit         # Ejecuta Jest una vez (modo CI)
npm run test:unit:watch   # Observa cambios y re-ejecuta
npm run test:unit:file    # Ejecuta Jest sobre un archivo (usa -- <ruta>)
```

## Build

```bash
npm run build
```

## Ejecución con Docker

Archivo: `docker-compose.pharmacy.yaml`

Comandos típicos:

```bash
# Limpieza de contenedores en puertos usados (perfil cleanup)
docker compose -f docker-compose.pharmacy.yaml --profile cleanup up

# Levantar (con build) backend y frontend de Pharmacy
docker compose -f docker-compose.pharmacy.yaml up --build
```

Puertos por defecto según `docker-compose.pharmacy.yaml`:

- Backend Pharmacy (contenedor 8081) expuesto en host: `${PHARM_BACKEND_HOST_PORT:-8082}`
- Frontend Pharmacy (contenedor 8080) expuesto en host: `${PHARM_FRONTEND_HOST_PORT:-8089}`

Variables de entorno relevantes para el frontend en Docker:

- `VUE_APP_PHARMACY_API_URL`  (default: `http://localhost:8082/api2`)
- `VUE_APP_ENSURANCE_API_URL` (default: `http://localhost:8081/api`)

## Configuración de Puertos (combinado)

La app incluye una interfaz para elegir puertos de conexión a los backends:

1. Backend de Pharmacy (predeterminado lógico de backend: 8081; expuesto en host usualmente 8082 vía Docker)
2. Backend de Ensurance (predeterminado lógico de backend: 8080; expuesto en host usualmente 8081 vía Docker)

Opciones de configuración:

- Al iniciar la app puede mostrarse un diálogo para configurar puertos.
- Desde la barra de navegación: opción "⚙️ Configurar Puertos" y guardar cambios.

Recomendaciones:

- Asegura que los puertos configurados coincidan con donde realmente corren los backends.
- Si usas Docker Compose de este repo, considera los mapeos de puertos indicados arriba.

Solución de problemas:

1. Verifica coincidencia entre puertos configurados en la UI y los puertos reales de backend.
2. Asegúrate de que los servidores backend estén iniciados antes de usar la app.
3. Si hay firewall, confirma que los puertos estén abiertos.

Persistencia:

La elección de puertos se guarda en `localStorage`. Para reiniciar, borra el almacenamiento local del navegador o reconfigura desde la opción de puertos.
