<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="prescriptions-container">
    <h2 class="text-2xl font-bold text-center text-blue-800 mb-4">
      Todas las Recetas
    </h2>

    <!-- Mensaje de error si lo hay -->
    <div v-if="errorMessage" class="mb-4 text-red-600 text-center">
      {{ errorMessage }}
    </div>

    <!-- Lista de recetas -->
    <div v-if="prescriptions.length > 0" class="prescriptions-list">
      <div v-for="prescription in prescriptions" :key="prescription.id" class="prescription-item">
        <h3 class="text-xl font-bold">{{ prescription.doctor }}</h3>
        <p><strong>ID Receta:</strong> {{ prescription.id }}</p>
        <p><strong>ID Usuario:</strong> {{ prescription.userId }}</p>
        <p><strong>ID Póliza:</strong> {{ prescription.policyId }}</p>
        <p><strong>Diagnóstico:</strong> {{ prescription.diagnosis }}</p>
        <p><strong>Fecha:</strong> {{ prescription.date }}</p>
        <p><strong>Medicamentos:</strong> {{ prescription.medicines }}</p>
      </div>
    </div>

    <!-- Mensaje si no hay recetas -->
    <div v-else class="text-center text-gray-600">
      No hay recetas disponibles.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const prescriptions = ref([]);
const errorMessage = ref('');
const ip = process.env.VUE_APP_API_IP || 'localhost';

// Nuevo: Obtener el ID del usuario actual, por ejemplo desde localStorage
const userId = localStorage.getItem('userId') || 'defaultUserId'; // ajustar según implementación

const fetchPrescriptions = async () => {
  try {
    // Se añade el parámetro userId a la URL para obtener solo las recetas del usuario actual
    const response = await axios.get(`http://${ip}:8081/api2/prescriptions_medicines?userId=${userId}`);
    prescriptions.value = response.data;
  } catch (error) {
    console.error("Error al obtener las recetas:", error);
    errorMessage.value = 'Error al obtener las recetas. Por favor, inténtelo de nuevo.';
  }
};

onMounted(() => {
  fetchPrescriptions();
});
</script>

<style scoped>
.prescriptions-container {
  padding: 50px;
  background-color: #f8f9fa;
}

.prescriptions-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.prescription-item {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
</style>
