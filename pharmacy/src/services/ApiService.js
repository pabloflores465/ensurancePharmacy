/**
 * Servicio para manejar las URLs de las APIs con puertos configurables
 */

// Importar variables de entorno
const ip =
  process.env.VUE_APP_API_HOST || process.env.VUE_APP_IP || "localhost";
const PHARMACY_API_BASE = process.env.VUE_APP_PHARMACY_API_URL || "";
const ENSURANCE_API_BASE = process.env.VUE_APP_ENSURANCE_API_URL || "";

// Configuración de puertos por defecto (dinámica según el puerto del frontend)
const currentPort =
  typeof window !== "undefined" && window.location && window.location.port
    ? window.location.port
    : "";

const dynamicDefaultsByPort = {
  // Pharmacy Frontend port -> Backends mapping
  // DEV
  "3001": { pharmacy: "3003", ensurance: "3002" },
  // QA
  "4001": { pharmacy: "4003", ensurance: "4002" },
  // MAIN
  "8089": { pharmacy: "8082", ensurance: "8081" },
};

// Fallback (when running outside docker-compose mapped ports)
const defaultPortConfig = dynamicDefaultsByPort[currentPort] || {
  pharmacy: "8082",
  ensurance: "8081",
};

// Almacenar la configuración de puertos
let portConfig = { ...defaultPortConfig };

/**
 * Configura los puertos para las APIs
 * @param {Object} ports Configuración de puertos
 */
export const configureApiPorts = (ports) => {
  if (ports.pharmacy) portConfig.pharmacy = ports.pharmacy;
  if (ports.ensurance) portConfig.ensurance = ports.ensurance;

  // Guardar la configuración en localStorage para persistencia
  localStorage.setItem("apiPortConfig", JSON.stringify(portConfig));

  console.log(
    `Puertos configurados: Pharmacy=${portConfig.pharmacy}, Ensurance=${portConfig.ensurance}`
  );
};

/**
 * Carga la configuración de puertos desde localStorage
 */
export const loadPortConfiguration = () => {
  const savedConfig = localStorage.getItem("apiPortConfig");
  if (savedConfig) {
    try {
      const config = JSON.parse(savedConfig);
      portConfig = { ...portConfig, ...config };
      console.log(
        `Configuración de puertos cargada: Pharmacy=${portConfig.pharmacy}, Ensurance=${portConfig.ensurance}`
      );
    } catch (error) {
      console.warn("Error al cargar configuración de puertos:", error);
    }
  }
};

/**
 * Obtiene la URL de la API de farmacia con el puerto configurado
 *
 * @param {string} endpoint - El endpoint de la API sin la barra inicial
 * @returns {string} URL completa con el puerto correcto
 */
export const getPharmacyApiUrl = (endpoint) => {
  // Eliminar la barra inicial del endpoint si existe
  const cleanEndpoint = endpoint.startsWith("/")
    ? endpoint.substring(1)
    : endpoint;
  // Si hay base por variable, usarla
  if (PHARMACY_API_BASE)
    return `${PHARMACY_API_BASE.replace(/\/$/, "")}/${cleanEndpoint}`;
  // Construir la URL completa
  return `http://${ip}:${portConfig.pharmacy}/api2/${cleanEndpoint}`;
};

/**
 * Obtiene la URL de la API de seguros con el puerto configurado
 *
 * @param {string} endpoint - El endpoint de la API sin la barra inicial
 * @returns {string} URL completa con el puerto correcto
 */
export const getEnsuranceApiUrl = (endpoint) => {
  // Eliminar la barra inicial del endpoint si existe
  const cleanEndpoint = endpoint.startsWith("/")
    ? endpoint.substring(1)
    : endpoint;
  // Si hay base por variable, usarla
  if (ENSURANCE_API_BASE)
    return `${ENSURANCE_API_BASE.replace(/\/$/, "")}/${cleanEndpoint}`;
  // Construir la URL completa
  return `http://${ip}:${portConfig.ensurance}/api/${cleanEndpoint}`;
};

// Exportar funciones y configuración por defecto
export default {
  configureApiPorts,
  loadPortConfiguration,
  getPharmacyApiUrl,
  getEnsuranceApiUrl,
  defaultPortConfig,
};
