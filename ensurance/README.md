# Vue 3 + TypeScript + Vite

This template should help get you started developing with Vue 3 and TypeScript in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

---

# Ensurance Frontend — Guía Unificada

Este documento unifica cómo correr, testear, construir y desplegar el frontend de Ensurance, e integra la configuración de puertos previamente documentada en `README-PUERTOS.md`.

## Requisitos

- Node.js (recomendado LTS 18+)
- npm
- Python 3 (usado por `getip.py` en desarrollo)
- Docker y Docker Compose (opcional para ejecución en contenedores)

## Instalación

```bash
npm install
```

## Desarrollo (local)

```bash
npm run dev
```

Este comando ejecuta `python3 getip.py` y levanta Vite con `--host` para exponer la app en tu red local.

Variables de entorno útiles (archivo `.env` o variables de entorno del sistema):

- `VITE_ENSURANCE_API_URL` (por ejemplo: `http://localhost:8081/api`)
- `VITE_PHARMACY_API_URL`  (por ejemplo: `http://localhost:8082/api2`)

## Pruebas

```bash
npm run test        # Vitest en modo interactivo
npm run test:run    # Vitest en modo headless (CI)
npm run test:ui     # Interfaz UI de Vitest
```

## Build y Preview

```bash
npm run build   # Compila TypeScript y genera build de Vite
npm run preview # Sirve el build localmente
```

## Ejecución con Docker

Archivo: `docker-compose.ensurance.yaml`

Comandos típicos:

```bash
# Limpieza de contenedores en puertos usados (perfil cleanup)
docker compose -f docker-compose.ensurance.yaml --profile cleanup up

# Levantar (con build) backend y frontend de Ensurance
docker compose -f docker-compose.ensurance.yaml up --build
```

Puertos por defecto según `docker-compose.ensurance.yaml`:

- Backend Ensurance (contenedor 8080) expuesto en host: `${ENS_BACKEND_HOST_PORT:-8081}`
- Frontend Ensurance (contenedor 5173) expuesto en host: `${ENS_FRONTEND_HOST_PORT:-5175}`

Variables de entorno relevantes para el frontend en Docker:

- `VITE_ENSURANCE_API_URL` (default: `http://localhost:8081/api`)
- `VITE_PHARMACY_API_URL`  (default: `http://localhost:8082/api2`)

## Configuración de Puertos (combinado)

La app incluye una interfaz para elegir puertos de conexión a los backends:

1. Backend de Ensurance (predeterminado lógico de backend: 8080; expuesto en host usualmente 8081 vía Docker)
2. Backend de Pharmacy (predeterminado lógico de backend: 8081; expuesto en host usualmente 8082 vía Docker)

Opciones de configuración:

- Al iniciar la app puede mostrarse un diálogo para configurar puertos.
- Desde el menú de administración: ingresar como admin, ir a "Admin" > "Configurar Puertos API" y guardar cambios.

Recomendaciones:

- Asegura que los puertos configurados coincidan con donde realmente corren los backends.
- Si usas Docker Compose de este repo, considera los mapeos de puertos indicados arriba.

Solución de problemas:

1. Verifica coincidencia entre puertos configurados en la UI y los puertos reales de backend.
2. Asegúrate de que los servidores backend estén iniciados antes de usar la app.
3. Si hay firewall, confirma que los puertos estén abiertos.

Persistencia:

La elección de puertos se guarda en `localStorage`. Para reiniciar, borra el almacenamiento local del navegador o usa el menú de administración.

Learn more about the recommended Project Setup and IDE Support in the [Vue Docs TypeScript Guide](https://vuejs.org/guide/typescript/overview.html#project-setup).
