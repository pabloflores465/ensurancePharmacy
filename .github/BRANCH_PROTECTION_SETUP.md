# Configuración de Protección de Ramas para Revisiones de Código

## Pasos para configurar la protección de ramas en GitHub

### 1. Ir a la configuración del repositorio
- Ve a tu repositorio en GitHub
- Haz clic en **Settings** (Configuración)
- En el menú lateral, selecciona **Branches** (Ramas)

### 2. Configurar reglas de protección para la rama `main`

Haz clic en **Add rule** (Agregar regla) y configura:

#### Configuración básica:
- **Branch name pattern**: `main`
- ✅ **Require a pull request before merging**
  - ✅ **Require approvals**: Mínimo 1 aprobación
  - ✅ **Dismiss stale PR approvals when new commits are pushed**
  - ✅ **Require review from code owners** (opcional)

#### Verificaciones de estado requeridas:
- ✅ **Require status checks to pass before merging**
- ✅ **Require branches to be up to date before merging**

**Selecciona estos checks obligatorios:**
- `Test Backend V4`
- `Test Backend V5` 
- `Test Ensurance Frontend`
- `Test Pharmacy Frontend`
- `SonarQube Pull Request Analysis`

#### Configuración adicional:
- ✅ **Require conversation resolution before merging**
- ✅ **Require signed commits** (opcional, para mayor seguridad)
- ✅ **Include administrators** (aplica reglas también a admins)

### 3. Configurar reglas similares para `develop` y `qa`

Repite el proceso para las ramas `develop` y `qa` con la misma configuración.

### 4. Configurar SonarQube Quality Gate

En tu proyecto de SonarQube, asegúrate de que el Quality Gate esté configurado para:
- **Coverage**: Mínimo 80% en nuevo código
- **Duplicated Lines**: Máximo 3% en nuevo código
- **Maintainability Rating**: A en nuevo código
- **Reliability Rating**: A en nuevo código
- **Security Rating**: A en nuevo código

## Resultado

Con esta configuración:
1. **No se puede hacer merge directo** a las ramas protegidas
2. **Todos los tests deben pasar** antes del merge
3. **SonarQube debe aprobar** el código (Quality Gate)
4. **Se requiere al menos 1 aprobación** de otro desarrollador
5. **Las conversaciones deben estar resueltas** antes del merge

## Secretos utilizados en los workflows

Los workflows utilizan estos secretos que ya tienes configurados:
- `SONAR_TOKEN`: Token de autenticación para SonarQube
- `SONAR_HOST_URL`: URL del servidor SonarQube
- `ENSURANCE_BACK_DEV/QA/MAIN`: Para deployments por ambiente
- `ENSURANCE_FRONT_DEV/QA/MAIN`: Para deployments frontend por ambiente
