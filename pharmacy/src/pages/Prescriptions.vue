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
        <h3 class="text-xl font-bold">
          {{ getPatientName(recipe) }}
        </h3>
        <p v-if="getPatientEmail(recipe)"><strong>Email:</strong> {{ getPatientEmail(recipe) }}</p>
        <p><strong>ID Receta:</strong> {{ recipe._id }}</p>
        <p><strong>Diagnóstico:</strong> {{ getDiagnostic(recipe) }}</p>
        <p v-if="recipe.created_at"><strong>Fecha:</strong> {{ recipe.created_at }}</p>
        <p v-if="recipe.formatted_date"><strong>Fecha:</strong> {{ recipe.formatted_date }}</p>

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
const patientInfo = ref(null);

// Funciones auxiliares para extraer información del paciente
const getPatientName = (recipe) => {
  // Si el paciente es un objeto completo en la receta
  if (recipe.patient && typeof recipe.patient === 'object' && recipe.patient.username) {
    return recipe.patient.username;
  }
  // Si el paciente está referenciado como ID y tenemos la información del paciente guardada
  if (patientInfo.value && recipe.patient === patientInfo.value._id) {
    return patientInfo.value.username;
  }
  // Si doctor_details está presente, usamos el formato doctor_details
  if (recipe.doctor_details) {
    return recipe.patient_details ? recipe.patient_details.username : 'Paciente';
  }
  return 'Paciente';
};

const getPatientEmail = (recipe) => {
  // Si el paciente es un objeto completo en la receta
  if (recipe.patient && typeof recipe.patient === 'object' && recipe.patient.email) {
    return recipe.patient.email;
  }
  // Si el paciente está referenciado como ID y tenemos la información del paciente guardada
  if (patientInfo.value && recipe.patient === patientInfo.value._id) {
    return patientInfo.value.email;
  }
  // Si patient_details está presente
  if (recipe.patient_details) {
    return recipe.patient_details.email;
  }
  return '';
};

const getDiagnostic = (recipe) => {
  // Intentar obtener diagnóstico desde diferentes estructuras posibles
  if (recipe.diagnostic) {
    return recipe.diagnostic;
  }
  if (recipe.medicines && recipe.medicines.length > 0 && recipe.medicines[0].diagnostico) {
    return recipe.medicines[0].diagnostico;
  }
  return 'No especificado';
};

const fetchPrescriptions = async () => {
  try {
    // Obtener el email del usuario del localStorage
    let userEmail = ''; // Valor por defecto
    try {
      const userData = JSON.parse(localStorage.getItem('user'));
      if (userData && userData.email) {
        userEmail = userData.email;
        console.log("Email de usuario obtenido:", userEmail);
      } else {
        // Intentar obtener desde session
        const sessionData = JSON.parse(localStorage.getItem('session'));
        if (sessionData && sessionData.email) {
          userEmail = sessionData.email;
          console.log("Email de usuario obtenido de session:", userEmail);
        } else {
          console.warn("No se encontró el email de usuario en localStorage");
          // Fallback a username si no hay email
          if (userData && userData.name) {
            userEmail = userData.name;
            console.log("Usando nombre de usuario en lugar de email:", userEmail);
          } else if (sessionData && sessionData.name) {
            userEmail = sessionData.name;
            console.log("Usando nombre de usuario de session en lugar de email:", userEmail);
          } else {
            userEmail = 'rrrivera@unis.edu.gt'; // Valor por defecto si no hay información
          }
        }
      }
    } catch (err) {
      console.error("Error al obtener el usuario del localStorage:", err);
      userEmail = 'rrrivera@unis.edu.gt'; // Valor por defecto en caso de error
    }

    // Usando la URL específica proporcionada
    // Corrigiendo la URL (quitando un slash)
    const baseUrl = 'http://172.16.57.55.16.57.55:5050/recipes/email/';
    const url = `${baseUrl}${userEmail}`;
    console.log(`Consultando recetas con URL dinámica: ${url}`);
    
    const response = await axios.get(url, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    });
    
    console.log("Respuesta recibida:", response);
    
    // Verificar si la respuesta es HTML (texto que comienza con <!DOCTYPE o <html)
    if (typeof response.data === 'string' && 
        (response.data.trim().startsWith('<!DOCTYPE') || 
         response.data.trim().startsWith('<html'))) {
      console.error("Se recibió HTML en lugar de JSON:", response.data.substring(0, 200) + '...');
      recipes.value = [];
      errorMessage.value = 'El servidor respondió con HTML en lugar de datos JSON. Verifique la conexión.';
      return;
    }
    
    // Manejo flexible de diferentes formatos de respuesta
    if (response.data) {
      let recetasEncontradas = false;
      
      // Verificamos si existe el objeto patient (formato nuevo)
      if (response.data.patient && Array.isArray(response.data.recipes)) {
        recipes.value = response.data.recipes;
        patientInfo.value = response.data.patient;
        recetasEncontradas = true;
        console.log("Formato 1: Objeto con patient y recipes array");
      } 
      // Verificamos si recipes existe directamente en la respuesta como array
      else if (Array.isArray(response.data.recipes)) {
        recipes.value = response.data.recipes;
        recetasEncontradas = true;
        console.log("Formato 2: Objeto con recipes array");
      } 
      // En caso de que la API devuelva un array directamente
      else if (Array.isArray(response.data)) {
        recipes.value = response.data;
        recetasEncontradas = true;
        console.log("Formato 3: Array directo");
      } 
      // Si la API devuelve un objeto que es la receta directamente
      else if (response.data._id && response.data.medicines) {
        recipes.value = [response.data];
        recetasEncontradas = true;
        console.log("Formato 4: Objeto de receta única");
      }
      // Si no reconocemos el formato, pero hay un objeto receta
      else if (typeof response.data === 'object') {
        // Intentar extraer cualquier array que parezca contener recetas
        const possibleRecipesArrays = Object.values(response.data).filter(val => 
          Array.isArray(val) && val.length > 0 && val[0]._id && val[0].medicines
        );
        
        if (possibleRecipesArrays.length > 0) {
          recipes.value = possibleRecipesArrays[0];
          recetasEncontradas = true;
          console.log("Formato 5: Extraído array de recetas de objeto", possibleRecipesArrays[0]);
        } else {
          // Último intento: buscar propiedades que parezcan recetas individuales
          const possibleRecipes = Object.values(response.data).filter(val => 
            val && typeof val === 'object' && val._id && val.medicines
          );
          
          if (possibleRecipes.length > 0) {
            recipes.value = possibleRecipes;
            recetasEncontradas = true;
            console.log("Formato 6: Extraídas recetas individuales", possibleRecipes);
          }
        }
      }
      
      console.log("RECETAS PROCESADAS:", recipes.value);
      
      if (!recetasEncontradas) {
        console.warn("Formato de respuesta no reconocido:", response.data);
        recipes.value = [];
        errorMessage.value = 'Formato de respuesta no reconocido. Consulte la consola para más detalles.';
      } else if (recipes.value.length === 0) {
        errorMessage.value = 'No se encontraron recetas para este usuario.';
      }
    } else {
      console.warn("La respuesta del API está vacía");
      recipes.value = [];
      errorMessage.value = 'No se encontraron recetas para este usuario.';
    }
  } catch (error) {
    console.error("Error al obtener las recetas:", error);
    recipes.value = [];
    errorMessage.value = `Error al obtener las recetas: ${error.message}. Por favor, inténtelo de nuevo.`;
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