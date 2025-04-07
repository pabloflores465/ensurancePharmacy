<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import axios from "axios";

interface Category {
  idCategory: number;
  name: string;
  enabled: number;
}

interface HospitalServiceItem {
  hospitalServiceId: string;
  name: string;
  imported: boolean;
  insuranceServiceId?: number;
  categories?: string[];
  subcategories?: string[];
  copay?: number;
  pay?: number;
  total?: number;
}

// Estado
const hospitalServices = ref<HospitalServiceItem[]>([]);
const categories = ref<Category[]>([]);
const loading = ref(true);
const error = ref("");
const success = ref("");
const ip = import.meta.env.VITE_IP || "localhost";

// Estado del modal de aprobación
const showApprovalModal = ref(false);
const selectedService = ref<HospitalServiceItem | null>(null);
const selectedCategoryId = ref<number | null>(null);
const selectedSubcategoryId = ref<number | null>(null);
const coveragePercentage = ref(80); // porcentaje por defecto
const serviceDescription = ref("");
const modalError = ref("");

// Filtro de búsqueda
const searchQuery = ref("");

// Servicios filtrados
const filteredServices = computed(() => {
  if (!searchQuery.value) {
    return hospitalServices.value;
  }
  
  const query = searchQuery.value.toLowerCase();
  return hospitalServices.value.filter(service => 
    service.name.toLowerCase().includes(query)
  );
});

// Cargar datos
const fetchHospitalServices = async () => {
  try {
    loading.value = true;
    error.value = "";
    
    const response = await axios.get(`http://${ip}:8080/api/insurance-services/hospital-services`);
    hospitalServices.value = response.data || [];
  } catch (err) {
    console.error("Error al cargar servicios del hospital:", err);
    error.value = "Error al cargar servicios del hospital. Por favor, intente nuevamente.";
    hospitalServices.value = [];
  } finally {
    loading.value = false;
  }
};

const fetchCategories = async () => {
  try {
    const response = await axios.get(`http://${ip}:8080/api/category`);
    categories.value = response.data || [];
  } catch (err) {
    console.error("Error al cargar categorías:", err);
    error.value = "Error al cargar categorías. Por favor, intente nuevamente.";
    categories.value = [];
  }
};

// Abrir modal para aprobar servicio
const openApprovalModal = (service: HospitalServiceItem) => {
  selectedService.value = service;
  selectedCategoryId.value = null;
  selectedSubcategoryId.value = null;
  coveragePercentage.value = 80;
  serviceDescription.value = "";
  modalError.value = "";
  showApprovalModal.value = true;
};

// Cerrar modal
const closeApprovalModal = () => {
  showApprovalModal.value = false;
  selectedService.value = null;
};

// Aprobar servicio
const approveService = async () => {
  if (!selectedService.value || !selectedCategoryId.value || !selectedSubcategoryId.value) {
    modalError.value = "Por favor, complete todos los campos obligatorios.";
    return;
  }
  
  try {
    loading.value = true;
    modalError.value = "";
    
    const serviceData = {
      hospitalServiceId: selectedService.value.hospitalServiceId,
      categoryId: selectedCategoryId.value,
      subcategoryId: selectedSubcategoryId.value,
      coveragePercentage: coveragePercentage.value,
      description: serviceDescription.value || `Servicio importado del hospital: ${selectedService.value.name}`
    };
    
    await axios.post(`http://${ip}:8080/api/insurance-services/approve-hospital-service`, serviceData);
    
    // Actualizar la lista de servicios
    await fetchHospitalServices();
    
    success.value = "Servicio aprobado correctamente.";
    closeApprovalModal();
    
    // Limpiar mensaje de éxito después de 3 segundos
    setTimeout(() => {
      success.value = "";
    }, 3000);
  } catch (err) {
    console.error("Error al aprobar servicio:", err);
    modalError.value = "Error al aprobar el servicio. Por favor, intente nuevamente.";
  } finally {
    loading.value = false;
  }
};

// Cargar datos iniciales
onMounted(async () => {
  try {
    await Promise.all([fetchHospitalServices(), fetchCategories()]);
  } catch (err) {
    console.error("Error al cargar datos iniciales:", err);
    error.value = "Error al cargar datos. Por favor, recargue la página.";
  }
});
</script>

<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-6">Importar Servicios del Hospital</h1>
    
    <div v-if="success" class="bg-green-100 border-l-4 border-green-500 text-green-700 p-4 mb-4" role="alert">
      <p>{{ success }}</p>
    </div>
    
    <div v-if="error" class="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4" role="alert">
      <p>{{ error }}</p>
    </div>
    
    <!-- Filtro de búsqueda -->
    <div class="mb-4">
      <input 
        v-model="searchQuery"
        type="text"
        placeholder="Buscar servicios por nombre..."
        class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
      />
    </div>
    
    <!-- Spinner de carga -->
    <div v-if="loading" class="text-center py-8">
      <div class="spinner"></div>
      <p class="mt-2 text-gray-600">Cargando servicios del hospital...</p>
    </div>
    
    <!-- Mensaje cuando no hay servicios -->
    <div v-else-if="hospitalServices.length === 0" class="text-center py-8">
      <p class="text-gray-600">No se encontraron servicios en el hospital.</p>
    </div>
    
    <!-- Lista de servicios -->
    <div v-else class="overflow-x-auto">
      <table class="min-w-full bg-white border border-gray-200">
        <thead>
          <tr>
            <th class="px-4 py-2 border-b text-left">Nombre del Servicio</th>
            <th class="px-4 py-2 border-b text-right">Costo Total (Q)</th>
            <th class="px-4 py-2 border-b text-center">Estado</th>
            <th class="px-4 py-2 border-b text-center">Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="service in filteredServices" :key="service.hospitalServiceId" class="hover:bg-gray-50">
            <td class="px-4 py-2 border-b">{{ service.name }}</td>
            <td class="px-4 py-2 border-b text-right">{{ (service.total || 0).toFixed(2) }}</td>
            <td class="px-4 py-2 border-b text-center">
              <span 
                :class="service.imported ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'"
                class="px-2 py-1 rounded-full text-xs font-medium"
              >
                {{ service.imported ? 'Importado' : 'Pendiente' }}
              </span>
            </td>
            <td class="px-4 py-2 border-b text-center">
              <button 
                v-if="!service.imported"
                @click="openApprovalModal(service)"
                class="bg-blue-600 hover:bg-blue-700 text-white text-sm px-3 py-1 rounded"
              >
                Aprobar
              </button>
              <span v-else class="text-green-700">Servicio ya aprobado</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Modal de aprobación -->
    <div v-if="showApprovalModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-xl mx-4">
        <h2 class="text-xl font-semibold mb-4">Aprobar Servicio del Hospital</h2>
        
        <div v-if="modalError" class="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-4" role="alert">
          <p>{{ modalError }}</p>
        </div>
        
        <div v-if="selectedService" class="mb-4">
          <p class="mb-2"><strong>Nombre:</strong> {{ selectedService.name }}</p>
          <p class="mb-2"><strong>Costo Total:</strong> Q{{ (selectedService.total || 0).toFixed(2) }}</p>
        </div>
        
        <form @submit.prevent="approveService" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Descripción (opcional)</label>
            <textarea 
              v-model="serviceDescription" 
              rows="2"
              placeholder="Descripción del servicio en el seguro"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            ></textarea>
          </div>
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Categoría *</label>
              <select 
                v-model="selectedCategoryId"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              >
                <option :value="null">Seleccione...</option>
                <option 
                  v-for="category in categories.filter(c => c.enabled === 1)" 
                  :key="category.idCategory"
                  :value="category.idCategory"
                >
                  {{ category.name }}
                </option>
              </select>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Subcategoría *</label>
              <select 
                v-model="selectedSubcategoryId"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              >
                <option :value="null">Seleccione...</option>
                <option 
                  v-for="category in categories.filter(c => c.enabled === 1)" 
                  :key="category.idCategory"
                  :value="category.idCategory"
                >
                  {{ category.name }}
                </option>
              </select>
            </div>
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Porcentaje de Cobertura (%) *
            </label>
            <input 
              v-model.number="coveragePercentage"
              type="number"
              min="0"
              max="100"
              required
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          
          <div class="flex justify-end space-x-3 mt-4">
            <button
              type="button"
              @click="closeApprovalModal"
              class="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400"
            >
              Cancelar
            </button>
            <button
              type="submit"
              class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
              :disabled="loading"
            >
              {{ loading ? 'Aprobando...' : 'Aprobar Servicio' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid #3498db;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 