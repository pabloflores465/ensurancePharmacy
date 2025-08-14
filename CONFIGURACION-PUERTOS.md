# Configuración de Puertos y Variables de Entorno

## Variables de Entorno por Proyecto

### Frontend Ensurance (Vite)

```bash
VITE_ENSURANCE_API_URL=http://localhost:8081/api
VITE_PHARMACY_API_URL=http://localhost:8082/api2
VITE_IP=localhost
```

### Frontend Pharmacy (Vue)

```bash
VUE_APP_PHARMACY_API_URL=http://localhost:8082/api2
VUE_APP_ENSURANCE_API_URL=http://localhost:8081/api
VUE_APP_IP=localhost
```

### Frontend Front (Nuxt)

```bash
NUXT_PUBLIC_ENSURANCE_API_URL=http://localhost:8081/api
IP=localhost
```

### Backend Variables (Java)

```bash
# Para Ensurance Backend (backv4)
SERVER_HOST=0.0.0.0
SERVER_PORT=8080
PHARM_BACKEND_API_URL=http://localhost:8082/api2
HOSPITAL_API_URL=http://localhost:8000/api

# Para Pharmacy Backend (backv5)
SERVER_HOST=0.0.0.0
SERVER_PORT=8081
ENS_BACKEND_API_URL=http://localhost:8081/api
HOSPITAL_API_URL=http://localhost:8000/api
```

## Configuración por Entorno (Jenkins)

### DEV (branch dev)

```bash
ENS_BACKEND_HOST_PORT=8081
PHARM_BACKEND_HOST_PORT=8082
```

### UAT (branch test)

```bash
ENS_BACKEND_HOST_PORT=9081
PHARM_BACKEND_HOST_PORT=9082
```

### PROD (branch main)

```bash
ENS_BACKEND_HOST_PORT=80
PHARM_BACKEND_HOST_PORT=81
```

## URLs de Integración Externa (No cambian)

Estas URLs se mantienen fijas ya que son integraciones con sistemas externos:

- Hospital: `http://localhost:8000/api`
- Servicios específicos: `http://localhost:5050/api`

## Funciones de API Configurables

### Frontend Ensurance

```javascript
import { getInsuranceApiUrl, getPharmacyApiUrl } from "~/utils/api";

// Uso correcto
const response = await axios.get(getInsuranceApiUrl("hospital"));
```

### Frontend Pharmacy

```javascript
import ApiService from "@/services/ApiService";

// Uso correcto
const response = await axios.get(ApiService.getPharmacyApiUrl("/medicines"));
const response2 = await axios.get(ApiService.getEnsuranceApiUrl("/users"));
```

### Frontend Front (Nuxt)

```javascript
import { getInsuranceApiUrl } from "~/utils/api";

// Uso correcto
const response = await axios.get(getInsuranceApiUrl("login"));
```

## Cambios Implementados

### ✅ Corregidas URLs Hardcodeadas en:

- `front/pages/login.vue`
- `front/pages/calendar.vue`
- `ensurance/src/utils/api-integration.ts`
- `ensurance/src/components/ApiDiagnostics.vue`
- `pharmacy/src/services/ApiService.js`
- `pharmacy/src/pages/VerificarCompra.vue`
- Todos los backends Java (`backv4` y `backv5`)

### ✅ Actualizados Archivos de Configuración:

- `Jenkinsfile` - Variables de entorno por ambiente
- `docker-compose.ensurance.yaml` - Variables para backends
- `docker-compose.pharmacy.yaml` - Variables para backends

## Resultado

Ahora el proyecto reconoce correctamente los puertos según el entorno:

- **DEV**: Ensurance en 8081, Pharmacy en 8082
- **UAT**: Ensurance en 9081, Pharmacy en 9082
- **PROD**: Ensurance en 80, Pharmacy en 81

No más URLs como `http://localhost/api/login` - todos los puertos se configuran dinámicamente.
