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
      <div v-for="group in prescriptions" :key="group.prescription.idPrescription" class="prescription-item">
        <h3 class="text-xl font-bold">{{ group.prescription.user.name }}</h3>
        <p><strong>ID Receta:</strong> {{ group.prescription.idPrescription }}</p>
        <p><strong>Diagnóstico:</strong> {{ group.prescription.diagnosis || 'No especificado' }}</p>
        <p><strong>Fecha:</strong> {{ group.prescription.date || 'No disponible' }}</p>

        <!-- Tabla de Medicinas -->
        <table class="medicine-table">
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
            <tr v-for="medicineEntry in group.medicines" :key="medicineEntry.medicine.idMedicine">
              <td>{{ medicineEntry.medicine.name }}</td>
              <td>{{ medicineEntry.medicine.concentration }}</td>
              <td>{{ medicineEntry.medicine.presentacion }}</td>
              <td>{{ medicineEntry.dosis }}</td>
              <td>{{ medicineEntry.frecuencia }}</td>
              <td>{{ medicineEntry.duracion }} días</td>
            </tr>
          </tbody>
        </table>
        <button @click="$router.push({ name: 'PrescriptionPay', params: { id: group.prescription.idPrescription } })">Comprar</button>
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
import { useUserStore } from "@/stores/userStore";
const userStore = useUserStore();

const prescriptions = ref([]);
const errorMessage = ref('');
const ip = process.env.VUE_APP_API_IP || 'localhost';

const fetchPrescriptions = async () => {
  try {
    const response = await axios.get(`http://${ip}:8081/api2/prescription_medicines`);
    const allPrescriptions = response.data;

    // Filtrar recetas que pertenecen al usuario actual
    const userPrescriptions = allPrescriptions.filter(p => p.prescription.user.idUser == userStore.user.idUser);

    // Agrupar recetas por prescriptionId
    const grouped = {};
    userPrescriptions.forEach(p => {
      const prescId = p.id.prescriptionId;
      if (!grouped[prescId]) {
        grouped[prescId] = {
          prescription: p.prescription,
          medicines: []
        };
      }
      grouped[prescId].medicines.push({
        medicine: p.medicine,
        dosis: p.dosis,
        frecuencia: p.frecuencia,
        duracion: p.duracion
      });
    });
    const groupsArray = Object.values(grouped);
    const filteredGroups = [];

    // Para cada receta, llamar a la API de orders y omitir si status es "Completado"
    for (const group of groupsArray) {
      const orderResponse = await axios.get(`http://${ip}:8081/api2/orders?id=${group.prescription.idPrescription}`);
      if (orderResponse.data && orderResponse.data.status === "Completado") {
        // Si la orden está completada, omitir la receta
        continue;
      } else {
        filteredGroups.push(group);
      }
    }

    prescriptions.value = filteredGroups;
    console.log("RECETAS AGRUPADAS:", prescriptions.value);
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

.medicine-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
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
</style>
