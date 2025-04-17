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
    <div v-if="recipes && recipes.length > 0" class="prescriptions-list">
      <div v-for="recipe in recipes" :key="recipe._id" class="prescription-item">
        <h3 class="text-xl font-bold">{{ recipe.patient && recipe.patient.username }}</h3>
        <p v-if="recipe.patient && recipe.patient.email"><strong>Email:</strong> {{ recipe.patient.email }}</p>
        <p><strong>ID Receta:</strong> {{ recipe._id }}</p>
        <p><strong>Diagnóstico:</strong> {{ recipe.diagnostic || 'No especificado' }}</p>
        <p v-if="recipe.created_at"><strong>Fecha:</strong> {{ recipe.created_at }}</p>

        <!-- Tabla de Medicinas -->
        <table class="medicine-table" v-if="recipe.medicines && recipe.medicines.length > 0">
          <thead>
          <tr>
            <th>Nombre</th>
            <th>Concentración</th>
            <th>Presentación</th>
            <th>Dosis</th>
            <th>Frecuencia</th>
            <th>Duración</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="medicine in recipe.medicines" :key="medicine._id">
            <td>{{ medicine.principioActivo }}</td>
            <td>{{ medicine.concentracion }}</td>
            <td>{{ medicine.presentacion }}</td>
            <td>{{ medicine.dosis }}</td>
            <td>{{ medicine.frecuencia }}</td>
            <td>{{ medicine.duracion }}</td>
          </tr>
          </tbody>
        </table>
        <p v-if="recipe.has_insurance" class="insurance-info">Con seguro médico</p>
        <button class="buy-button" @click="$router.push({ name: 'PrescriptionPay', params: { id: recipe._id } })">Comprar</button>
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

const recipes = ref([]);
const errorMessage = ref('');

const fetchPrescriptions = async () => {
  try {
    // Obtener el nombre de usuario (mel) del localStorage
    let username = 'mel'; // Valor por defecto
    try {
      const userData = JSON.parse(localStorage.getItem('user'));
      if (userData && userData.name) {
        username = userData.name; // Tomar el valor "mel" del campo name
        console.log("Nombre de usuario obtenido:", username);
      } else {
        // Intentar obtener desde session
        const sessionData = JSON.parse(localStorage.getItem('session'));
        if (sessionData && sessionData.name) {
          username = sessionData.name;
          console.log("Nombre de usuario obtenido de session:", username);
        } else {
          console.warn("No se encontró el nombre de usuario en localStorage");
        }
      }
    } catch (err) {
      console.error("Error al obtener el usuario del localStorage:", err);
    }

    // Consumir el API con el parámetro username (mel)
    console.log(`Consultando recetas para el usuario: ${username}`);
    const response = await axios.get(` http://172.20.10.3:5051/recipes?username=${username}`);
    
    if (response.data && response.data.recipes) {
      recipes.value = response.data.recipes;
      console.log("RECETAS OBTENIDAS:", recipes.value);
    } else {
      console.warn("La respuesta del API no tiene el formato esperado", response.data);
      recipes.value = [];
      errorMessage.value = 'No se encontraron recetas para este usuario.';
    }
  } catch (error) {
    console.error("Error al obtener las recetas:", error);
    recipes.value = [];
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

.medicine-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
  margin-bottom: 15px;
}

.medicine-table th, .medicine-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

.medicine-table th {
  background-color: #f4f4f4;
  font-weight: bold;
}

.insurance-info {
  color: #4caf50;
  font-weight: bold;
  margin: 10px 0;
}

.buy-button {
  background-color: #4caf50;
  color: white;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  border: none;
  font-weight: bold;
}

.buy-button:hover {
  background-color: #45a049;
}
</style>