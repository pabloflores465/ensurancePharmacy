<template>
  <div v-if="!hospitalSelected" class="hospital-selector-overlay">
    <div class="hospital-selector-content">
      <h2 class="text-2xl font-bold mb-4">Seleccionar Hospital</h2>
      <p class="mb-4">Seleccione un hospital para continuar</p>
      
      <div v-if="loading" class="flex items-center justify-center py-4">
        <div class="spinner"></div>
        <span class="ml-3">Cargando configuración...</span>
      </div>
      
      <div v-else-if="loadError" class="bg-red-100 p-4 mb-4 rounded border-l-4 border-red-500">
        <p class="text-red-700">{{ loadError }}</p>
        <button 
          @click="loadHospitalPorts" 
          class="mt-3 bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded text-sm"
        >
          Reintentar
        </button>
      </div>
      
      <div v-else-if="Object.keys(hospitalPorts).length === 0" class="bg-yellow-100 p-4 mb-4 rounded border-l-4 border-yellow-500">
        <p class="text-yellow-700">No se encontraron configuraciones de hospitales en env.json.</p>
        <p class="text-yellow-700 mt-2">Asegúrese de que el archivo contenga entradas VITE_HOSPITAL_PORT_X.</p>
      </div>
      
      <div v-else>
        <select 
          v-model="selectedPort" 
          class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-4"
        >
          <option value="" disabled>Seleccione un hospital</option>
          <option 
            v-for="(port, key) in hospitalPorts" 
            :key="key" 
            :value="port"
          >
            Hospital {{ key.split('_')[2] }} (Puerto: {{ port }})
          </option>
        </select>
        
        <button 
          @click="confirmSelection"
          class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg w-full"
          :disabled="!selectedPort"
        >
          Continuar
        </button>
        
        <div class="mt-4 text-sm text-gray-600">
          <p>IP del servidor: <strong>{{ serverIP }}</strong></p>
          <p v-if="serverIP === 'No configurada'" class="text-red-600 mt-1">
            ¡Advertencia! No se ha configurado VUE_APP_IP en el archivo .env.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HospitalSelector',
  data() {
    return {
      hospitalPorts: {},
      hospitalSelected: false,
      selectedPort: '',
      loading: false,
      loadError: '',
      serverIP: process.env.VUE_APP_IP || 'No configurada'
    };
  },
  emits: ['hospital-selected'],
  mounted() {
    this.loadHospitalPorts();
  },
  methods: {
    async loadHospitalPorts() {
      this.loading = true;
      this.loadError = '';
      
      try {
        const response = await fetch('/env.json');
        if (!response.ok) {
          throw new Error(`Error HTTP: ${response.status}`);
        }
        
        const envData = await response.json();
        
        // Filter only VITE_HOSPITAL_PORT entries
        const ports = {};
        for (const key in envData) {
          if (key.startsWith('VITE_HOSPITAL_PORT_')) {
            ports[key] = envData[key];
          }
        }
        
        this.hospitalPorts = ports;
        
        // Si no hay puertos configurados, mostrar error
        if (Object.keys(ports).length === 0) {
          this.loadError = "No se encontraron configuraciones de hospitales en env.json. Agregue al menos una entrada VITE_HOSPITAL_PORT_X.";
        }
      } catch (error) {
        console.error('Error loading hospital ports:', error);
        this.loadError = `Error al cargar configuración: ${error.message || 'Error de conexión'}`;
      } finally {
        this.loading = false;
      }
    },
    confirmSelection() {
      if (this.selectedPort) {
        localStorage.setItem('selectedHospitalPort', this.selectedPort);
        this.hospitalSelected = true;
        this.$emit('hospital-selected', this.selectedPort);
      }
    }
  }
}
</script>

<style scoped>
.hospital-selector-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.hospital-selector-content {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  max-width: 400px;
  width: 90%;
}

.spinner {
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 3px solid #3498db;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 