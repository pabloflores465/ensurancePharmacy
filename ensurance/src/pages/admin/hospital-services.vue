<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import axios from "axios";

interface Hospital {
  idHospital: number;
  name: string;
  address: string;
  phone: string;
  email: string;
  enabled: number;
}

interface Category {
  idCategory: number;
  name: string;
  enabled: number;
}

interface InsuranceService {
  idInsuranceService: number;
  name: string;
  description: string;
  category: Category;
  subcategory: Category;
  price: number;
  coveragePercentage: number;
  enabled: number;
}

interface HospitalService {
  idHospitalService: number;
  hospital: Hospital;
  insuranceService: InsuranceService;
  enabled: number;
}

// Inicializar como arrays vacíos para evitar errores de null
const hospitals = ref<Hospital[]>([]);
const services = ref<InsuranceService[]>([]);
const hospitalServices = ref<HospitalService[]>([]);
const loading = ref(true);
const error = ref("");
const success = ref("");
const ip = import.meta.env.VITE_IP || "localhost";

// Filtros y selección
const selectedHospitalId = ref<number | null>(null);
const selectedServiceId = ref<number | null>(null);
const serviceFilter = ref("");

// Estado del modal
const modalOpen = ref(false);

// Computed para filtrar servicios
const filteredServices = computed(() => {
  if (!services.value) return [];
  
  return services.value.filter(service => 
    service.name.toLowerCase().includes(serviceFilter.value.toLowerCase()) ||
    service.description.toLowerCase().includes(serviceFilter.value.toLowerCase()) ||
    service.category?.name.toLowerCase().includes(serviceFilter.value.toLowerCase()) ||
    service.subcategory?.name.toLowerCase().includes(serviceFilter.value.toLowerCase())
  );
});

// Computed para obtener servicios aprobados para el hospital seleccionado
const approvedServices = computed(() => {
  if (!selectedHospitalId.value || !hospitalServices.value) return [];
  
  return hospitalServices.value.filter(
    hs => hs.hospital?.idHospital === selectedHospitalId.value
  );
});
const insurance = parseInt(window.location.port);
const insurance_port = insurance-30;
// Funciones para cargar datos
const fetchHospitals = async () => {
  try {
    const response = await axios.get(`http://${ip}:${insurance_port}/api/hospital`);
    if (response.data) {
      hospitals.value = response.data;
    } else {
      hospitals.value = [];
      console.warn("La respuesta del servidor no contiene datos de hospitales");
    }
  } catch (err) {
    console.error("Error al cargar hospitales:", err);
    error.value = "Error al cargar hospitales. Por favor, intente nuevamente.";
    hospitals.value = [];
  }
};

const fetchServices = async () => {
  try {
    const response = await axios.get(`http://${ip}:${insurance_port}/api/insurance-services`);
    if (response.data) {
      services.value = response.data.filter((s: InsuranceService) => s.enabled === 1);
    } else {
      services.value = [];
      console.warn("La respuesta del servidor no contiene datos de servicios");
    }
  } catch (err) {
    console.error("Error al cargar servicios:", err);
    error.value = "Error al cargar servicios. Por favor, intente nuevamente.";
    services.value = [];
  }
};

const fetchHospitalServices = async () => {
  if (!selectedHospitalId.value) return;
  
  try {
    loading.value = true;
    
    const response = await axios.get(`http://${ip}:${insurance_port}/api/hospital-services?hospital=${selectedHospitalId.value}`);
    if (response.data) {
      hospitalServices.value = response.data;
    } else {
      hospitalServices.value = [];
      console.warn("La respuesta del servidor no contiene datos de servicios del hospital");
    }
  } catch (err) {
    console.error("Error al cargar servicios del hospital:", err);
    error.value = "Error al cargar servicios del hospital. Por favor, intente nuevamente.";
    hospitalServices.value = [];
  } finally {
    loading.value = false;
  }
};

// Otras funciones
const openModal = () => {
  if (!selectedHospitalId.value) {
    error.value = "Por favor, seleccione un hospital primero.";
    return;
  }
  
  modalOpen.value = true;
};

const closeModal = () => {
  modalOpen.value = false;
  selectedServiceId.value = null;
  serviceFilter.value = "";
};

const approveService = async () => {
  if (!selectedHospitalId.value || !selectedServiceId.value) {
    error.value = "Por favor, seleccione un hospital y un servicio.";
    return;
  }
  
  try {
    loading.value = true;
    error.value = "";
    
    const hospitalService = {
      hospitalId: selectedHospitalId.value,
      serviceId: selectedServiceId.value,
      notes: ""
    };
    
    await axios.post(`http://${ip}:${insurance_port}/api/hospital-services/approve`, hospitalService);
    
    success.value = "Servicio aprobado correctamente para el hospital.";
    
    // Recargar los servicios del hospital
    await fetchHospitalServices();
    closeModal();
    
    // Limpiar mensaje de éxito después de 3 segundos
    setTimeout(() => {
      success.value = "";
    }, 3000);
  } catch (err) {
    console.error("Error al aprobar servicio:", err);
    error.value = "Error al aprobar el servicio. Es posible que ya esté aprobado para este hospital.";
  } finally {
    loading.value = false;
  }
};

const revokeService = async (hospitalService: HospitalService) => {
  if (!confirm(`¿Está seguro de revocar el servicio "${hospitalService.insuranceService?.name || 'N/A'}" para este hospital?`)) {
    return;
  }
  
  try {
    loading.value = true;
    error.value = "";
    
    await axios.delete(`http://${ip}:${insurance_port}/api/hospital-services/${hospitalService.idHospitalService}`);
    
    success.value = "Aprobación de servicio revocada correctamente.";
    
    // Recargar los servicios del hospital
    await fetchHospitalServices();
    
    // Limpiar mensaje de éxito después de 3 segundos
    setTimeout(() => {
      success.value = "";
    }, 3000);
  } catch (err) {
    console.error("Error al revocar servicio:", err);
    error.value = "Error al revocar la aprobación del servicio.";
  } finally {
    loading.value = false;
  }
};

const onHospitalChange = async () => {
  if (selectedHospitalId.value) {
    await fetchHospitalServices();
  } else {
    hospitalServices.value = [];
  }
};

// Cargar datos iniciales
onMounted(async () => {
  try {
    loading.value = true;
    await Promise.all([fetchHospitals(), fetchServices()]);
  } catch (err) {
    console.error("Error al cargar datos iniciales:", err);
    error.value = "Error al cargar datos. Por favor, recargue la página.";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-6">Aprobación de Servicios para Hospitales</h1>
    
    <div v-if="success" class="bg-green-100 border-l-4 border-green-500 text-green-700 p-4 mb-4" role="alert">
      <p>{{ success }}</p>
    </div>
    
    <div v-if="error" class="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4" role="alert">
      <p>{{ error }}</p>
    </div>
    
    <!-- Selector de hospital -->
    <div class="mb-6">
      <label class="block text-sm font-medium text-gray-700 mb-1">Seleccione un Hospital</label>
      <select 
        v-model="selectedHospitalId"
        @change="onHospitalChange"
        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
      >
        <option :value="null">Seleccione un hospital</option>
        <option 
          v-for="hospital in hospitals" 
          :key="hospital.idHospital"
          :value="hospital.idHospital"
          :disabled="hospital.enabled !== 1"
        >
          {{ hospital.name }} {{ hospital.enabled !== 1 ? '(Inactivo)' : '' }}
        </option>
      </select>
    </div>
    
    <!-- Botón para abrir modal y aprobar nuevo servicio -->
    <div class="mb-4 flex justify-between items-center">
      <button
        @click="openModal"
        class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        :disabled="!selectedHospitalId"
      >
        Aprobar Nuevo Servicio
      </button>
    </div>
    
    <!-- Loading spinner -->
    <div v-if="loading" class="text-center py-8">
      <div class="spinner"></div>
      <p class="mt-2 text-gray-600">Cargando datos...</p>
    </div>
    
    <!-- Mensaje cuando no hay hospital seleccionado -->
    <div v-else-if="!selectedHospitalId" class="text-center py-8">
      <p class="text-gray-600">Por favor, seleccione un hospital para ver sus servicios aprobados.</p>
    </div>
    
    <!-- Mensaje cuando no hay servicios aprobados -->
    <div v-else-if="!approvedServices || approvedServices.length === 0" class="text-center py-8">
      <p class="text-gray-600">No hay servicios aprobados para este hospital.</p>
    </div>
    
    <!-- Tabla de servicios aprobados -->
    <div v-else class="overflow-x-auto">
      <table class="min-w-full bg-white border border-gray-200">
        <thead>
          <tr>
            <th class="px-4 py-2 border-b text-left">Servicio</th>
            <th class="px-4 py-2 border-b text-left">Categoría</th>
            <th class="px-4 py-2 border-b text-left">Subcategoría</th>
            <th class="px-4 py-2 border-b text-right">Precio (Q)</th>
            <th class="px-4 py-2 border-b text-center">Cobertura (%)</th>
            <th class="px-4 py-2 border-b text-center">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="hs in approvedServices" :key="hs.idHospitalService" class="hover:bg-gray-50">
            <td class="px-4 py-2 border-b">{{ hs.insuranceService?.name || 'N/A' }}</td>
            <td class="px-4 py-2 border-b">{{ hs.insuranceService?.category?.name || 'N/A' }}</td>
            <td class="px-4 py-2 border-b">{{ hs.insuranceService?.subcategory?.name || 'N/A' }}</td>
            <td class="px-4 py-2 border-b text-right">{{ hs.insuranceService?.price?.toFixed(2) || '0.00' }}</td>
            <td class="px-4 py-2 border-b text-center">{{ hs.insuranceService?.coveragePercentage || '0' }}%</td>
            <td class="px-4 py-2 border-b text-center">
              <button 
                @click="revokeService(hs)"
                class="text-red-600 hover:text-red-800"
              >
                Revocar Aprobación
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Modal para aprobar servicios -->
    <div v-if="modalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-4xl">
        <h2 class="text-xl font-semibold mb-4">Aprobar Servicio para el Hospital</h2>
        
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">Filtrar Servicios</label>
          <input 
            v-model="serviceFilter"
            type="text"
            placeholder="Buscar por nombre, descripción o categoría"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
          />
        </div>
        
        <div class="overflow-y-auto max-h-96">
          <table class="min-w-full bg-white border border-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-4 py-2 border-b text-left">Seleccionar</th>
                <th class="px-4 py-2 border-b text-left">Servicio</th>
                <th class="px-4 py-2 border-b text-left">Categoría</th>
                <th class="px-4 py-2 border-b text-left">Subcategoría</th>
                <th class="px-4 py-2 border-b text-right">Precio (Q)</th>
                <th class="px-4 py-2 border-b text-center">Cobertura (%)</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="service in filteredServices" :key="service.idInsuranceService" class="hover:bg-gray-50">
                <td class="px-4 py-2 border-b">
                  <input 
                    type="radio" 
                    :value="service.idInsuranceService" 
                    v-model="selectedServiceId"
                    class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded-full"
                  />
                </td>
                <td class="px-4 py-2 border-b">{{ service.name }}</td>
                <td class="px-4 py-2 border-b">{{ service.category?.name || 'N/A' }}</td>
                <td class="px-4 py-2 border-b">{{ service.subcategory?.name || 'N/A' }}</td>
                <td class="px-4 py-2 border-b text-right">{{ service.price?.toFixed(2) || '0.00' }}</td>
                <td class="px-4 py-2 border-b text-center">{{ service.coveragePercentage || '0' }}%</td>
              </tr>
              
              <tr v-if="filteredServices.length === 0">
                <td colspan="6" class="px-4 py-4 text-center text-gray-500">
                  No se encontraron servicios que coincidan con su búsqueda.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="flex justify-end space-x-3 mt-4">
          <button
            @click="closeModal"
            class="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400"
          >
            Cancelar
          </button>
          <button
            @click="approveService"
            class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
            :disabled="!selectedServiceId || loading"
          >
            {{ loading ? 'Aprobando...' : 'Aprobar Servicio' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border-left-color: #3b82f6;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 