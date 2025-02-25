<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="dashboard-container">
    <h1>Bienvenido, {{ patientName }}</h1>

    <!-- Sección Mis Recetas -->
    <section class="my-prescriptions">
      <h2>Mis Recetas</h2>
      <ul>
        <li v-for="prescription in prescriptions" :key="prescription.id">
          <span>Código: {{ prescription.code }}</span>
          <span>Estado: {{ prescription.status }}</span>
        </li>
      </ul>
      <div>
        <label for="new-prescription-code">Solicitar nueva receta:</label>
        <input type="text" id="new-prescription-code" v-model="newPrescriptionCode" />
        <button @click="requestNewPrescription">Solicitar</button>
      </div>
    </section>

    <!-- Sección Búsqueda de Medicamentos -->
    <section class="search-medicines">
      <h2>Búsqueda de Medicamentos</h2>
      <input type="text" v-model="searchQuery" placeholder="Buscar medicamentos..." />
      <button @click="searchMedicines">Buscar</button>
      <ul>
        <li v-for="medicine in medicines" :key="medicine.id">
          <span>{{ medicine.name }}</span>
          <button @click="addToCart(medicine)" v-if="medicine.isOverTheCounter">Agregar a la lista de compras</button>
        </li>
      </ul>
    </section>

    <!-- Sección Historial de Compras -->
    <section class="purchase-history">
      <h2>Historial de Compras</h2>
      <ul>
        <li v-for="purchase in purchaseHistory" :key="purchase.id">
          <span>{{ purchase.medicineName }}</span>
          <a :href="purchase.invoiceUrl" download>Descargar factura</a>
        </li>
      </ul>
    </section>

    <!-- Sección Notificaciones y Alertas -->
    <section class="notifications">
      <h2>Notificaciones y Alertas</h2>
      <ul>
        <li v-for="notification in notifications" :key="notification.id">
          <span>{{ notification.message }}</span>
        </li>
      </ul>
    </section>

    <!-- Sección Opciones de Perfil -->
    <section class="profile-options">
      <h2>Opciones de Perfil</h2>
      <button @click="editProfile">Editar Información Personal</button>
      <button @click="managePaymentMethods">Métodos de Pago</button>
      <button @click="configureNotifications">Configuración de Notificaciones</button>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const patientName = ref('Nombre del Paciente');
const prescriptions = ref([
  { id: 1, code: 'RX123', status: 'pendiente' },
  { id: 2, code: 'RX124', status: 'en proceso' },
  // ...otras recetas
]);
const newPrescriptionCode = ref('');
const searchQuery = ref('');
const medicines = ref([
  { id: 1, name: 'Medicamento A', isOverTheCounter: true },
  { id: 2, name: 'Medicamento B', isOverTheCounter: false },
  // ...otros medicamentos
]);
const purchaseHistory = ref([
  { id: 1, medicineName: 'Medicamento A', invoiceUrl: '/path/to/invoice1.pdf' },
  { id: 2, medicineName: 'Medicamento B', invoiceUrl: '/path/to/invoice2.pdf' },
  // ...otras compras
]);
const notifications = ref([
  { id: 1, message: 'Su receta RX123 está lista para recoger.' },
  { id: 2, message: 'El medicamento B está agotado.' },
  // ...otras notificaciones
]);

const requestNewPrescription = () => {
  console.log('Solicitando nueva receta con código:', newPrescriptionCode.value);
  // Lógica para solicitar nueva receta
};

const searchMedicines = () => {
  console.log('Buscando medicamentos con query:', searchQuery.value);
  // Lógica para buscar medicamentos
};

const addToCart = (medicine) => {
  console.log('Agregando a la lista de compras:', medicine.name);
  // Lógica para agregar medicamento a la lista de compras
};

const editProfile = () => {
  console.log('Editando información personal');
  // Lógica para editar información personal
};

const managePaymentMethods = () => {
  console.log('Gestionando métodos de pago');
  // Lógica para gestionar métodos de pago
};

const configureNotifications = () => {
  console.log('Configurando notificaciones');
  // Lógica para configurar notificaciones
};
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

h1, h2 {
  color: #1e40af;
}

section {
  margin-bottom: 20px;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin-bottom: 10px;
}

button {
  background-color: #1e40af;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #1e3a8a;
}
</style>