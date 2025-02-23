<script setup lang="ts">
import axios from "axios";
import { ref, computed } from "vue";
import { useEdit } from "@/composables/useEdit"; // Ajusta la ruta según tu proyecto

interface Pharmacy {
  idPharmacy: number;
  name: string;
  address: string;
  phone: number;
  email: string;
  enabled: number;
}

interface Medicine {
  idMedicine: number;
  name: string;
  description: string;
  price: number;
  pharmacy: Pharmacy;
  enabled: number;
  activePrinciple: string;
  presentation: string;
  stock: number;
  brand: string;
}

// Variable reactiva para almacenar los medicamentos
const medicines = ref<Medicine[]>([]);

const fetchMedicines = async () => {
  try {
    const response = await axios.get("http://localhost:8080/api/medicine");
    medicines.value = response.data;
    console.log("Medicines obtenidas:", medicines.value);
  } catch (error) {
    console.error("Error al obtener medicines:", error);
  }
};

fetchMedicines();

// Variable para controlar si se está en modo edición (por ejemplo, para mostrar inputs en lugar de solo texto)
const edit = useEdit();

// Variables para filtrar
const searchText = ref("");
const attributeOptions = ["name", "brand", "presentation", "activePrinciple", "description"];
const selectedAttributes = ref<string[]>([]);

// Computed que filtra la lista de medicamentos según el texto y los atributos seleccionados
const filteredMedicines = computed(() => {
  return medicines.value.filter(med => {
    const text = searchText.value.toLowerCase();
    if (!text) return true; // Si no hay texto, no se filtra
    // Si no se selecciona ningún atributo, se buscan en todos
    if (selectedAttributes.value.length === 0) {
      return (
          med.name.toLowerCase().includes(text) ||
          med.brand.toLowerCase().includes(text) ||
          med.presentation.toLowerCase().includes(text) ||
          med.activePrinciple.toLowerCase().includes(text) ||
          med.description.toLowerCase().includes(text)
      );
    } else {
      // Se filtra únicamente en los atributos seleccionados
      return selectedAttributes.value.some(attr => {
        switch (attr) {
          case "name":
            return med.name.toLowerCase().includes(text);
          case "brand":
            return med.brand.toLowerCase().includes(text);
          case "presentation":
            return med.presentation.toLowerCase().includes(text);
          case "activePrinciple":
            return med.activePrinciple.toLowerCase().includes(text);
          case "description":
            return med.description.toLowerCase().includes(text);
          default:
            return false;
        }
      });
    }
  });
});
</script>

<template>
  <div class="p-4">
    <!-- Filtro de texto -->
    <div class="mb-4">
      <label class="block font-semibold mb-2">Buscar:</label>
      <input
          type="text"
          v-model="searchText"
          placeholder="Escribe para filtrar..."
          class="border p-2 w-full"
      />
    </div>

    <!-- Filtro por atributos (checkboxes) -->
    <div class="mb-4">
      <label class="block font-semibold mb-2">Filtrar en:</label>
      <div v-for="option in attributeOptions" :key="option" class="flex items-center mb-1">
        <input
            type="checkbox"
            :value="option"
            v-model="selectedAttributes"
            class="mr-2"
        />
        <span>{{ option }}</span>
      </div>
    </div>

    <!-- Lista de medicamentos filtrados -->
    <div class="responsive-grid">
      <!-- Vista normal -->
      <div v-if="!edit" v-for="medicine in filteredMedicines" :key="medicine.idMedicine" class="card p-4 border mb-4">
        <h2 class="font-bold text-lg">{{ medicine.name }}</h2>
        <p><strong>Code:</strong> {{ medicine.idMedicine }}</p>
        <p><strong>Active Principle:</strong> {{ medicine.activePrinciple }}</p>
        <p><strong>Presentation:</strong> {{ medicine.presentation }}</p>
        <p><strong>Units:</strong> {{ medicine.stock }}</p>
        <p><strong>Brand:</strong> {{ medicine.brand }}</p>
        <p><strong>Price:</strong> {{ medicine.price }}</p>
        <p><strong>Description:</strong> {{ medicine.description }}</p>
        <p><strong>Pharmacy:</strong> {{ medicine.pharmacy.name }}</p>
      </div>

      <!-- Vista edición (ejemplo, se puede personalizar) -->
      <div v-if="edit" v-for="medicine in filteredMedicines" :key="medicine.idMedicine" class="card p-4 border mb-4">
        <span class="text-primary font-semibold">Name:</span>
        <input type="text" class="field mb-8" :value="medicine.name" />
        <span class="text-primary font-semibold">Code:</span>
        <input type="text" class="field mb-8" :value="medicine.idMedicine" />
        <span class="text-primary font-semibold">Active Principle:</span>
        <input type="text" class="field mb-8" :value="medicine.activePrinciple" />
        <span class="text-primary font-semibold">Presentation:</span>
        <input type="text" class="field mb-8" :value="medicine.presentation" />
        <span class="text-primary font-semibold">Units:</span>
        <input type="number" class="field mb-8" :value="medicine.stock" />
        <div><Switch class="mb-4" label="Recipe Required" /></div>
        <span class="text-primary font-semibold">Brand:</span>
        <input type="text" class="field mb-8" :value="medicine.brand" />
        <Dropdown />
      </div>
    </div>
  </div>
</template>

<style scoped>
.responsive-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
}
.card {
  background-color: #fff;
  border-radius: 0.5rem;
  padding: 1rem;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}
</style>
