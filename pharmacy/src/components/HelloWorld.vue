<template>
  <!-- Header con navegación principal -->
  <header class="bg-blue-600 p-4 text-white flex justify-between items-center">
      <h1 class="text-2xl font-bold">Farmacia Salud</h1>
      <nav>
          <a href="#" class="mx-3 hover:underline">Inicio</a>
          <a href="#" class="mx-3 hover:underline">Medicamentos</a>
          <a href="#" class="mx-3 hover:underline">Sucursales</a>
          <a href="#" class="mx-3 hover:underline">Contacto</a>
          <a href="#" class="bg-white text-blue-600 px-3 py-1 rounded">Iniciar Sesión</a>
      </nav>
  </header>

  <!-- Hero/Banner con mensaje principal y buscador -->
  <section class="text-center py-16 bg-blue-200">
      <h2 class="text-4xl font-bold mb-4">Tu salud en nuestras manos</h2>
      <p class="text-lg mb-6">Encuentra tus medicamentos fácilmente</p>
      <input type="text" v-model="search" placeholder="Buscar medicamento..." class="px-4 py-2 rounded w-1/2">
  </section>

  <!-- Sección de productos destacados -->
  <section class="p-8">
      <h2 class="text-3xl font-bold text-center mb-6">Medicamentos en Oferta</h2>
      <div class="grid grid-cols-3 gap-4">
          <div v-for="(med, index) in medicines" :key="index" class="bg-white p-4 shadow rounded-lg text-center">
              <img :src="med.image" :alt="med.name" class="mx-auto">
              <h3 class="text-lg font-semibold mt-2">{{ med.name }}</h3>
              <p class="text-green-600">{{ med.price }}</p>
          </div>
      </div>
  </section>

  <!-- Beneficios de la farmacia -->
  <section class="bg-gray-200 p-8 text-center">
      <h2 class="text-3xl font-bold mb-4">¿Por qué elegirnos?</h2>
      <p>✔ Conexión con hospitales y seguros médicos</p>
      <p>✔ Pagos seguros y descuentos aplicados</p>
      <p>✔ Recibe notificaciones sobre tus medicamentos</p>
  </section>

  <!-- Mapa de sucursales -->
  <section class="p-8 text-center">
      <h2 class="text-3xl font-bold mb-4">Nuestras Sucursales</h2>
      <p>Encuentra la más cercana a ti</p>
      <div class="bg-gray-400 h-64 w-full mt-4"></div> <!-- Placeholder para el mapa -->
  </section>

  <!-- Footer con información básica -->
  <footer class="bg-blue-600 text-white text-center p-4">
      <p>© 2025 Farmacia Salud | Todos los derechos reservados</p>
  </footer>
</template>

<script>
export default {
  data() {
      return {
          search: '', // Almacena el valor ingresado en el buscador
          medicines: [ // Lista de medicamentos en oferta
              { name: 'Paracetamol 500mg', price: 'Q25.00', image: 'https://via.placeholder.com/150' },
              { name: 'Ibuprofeno 200mg', price: 'Q30.00', image: 'https://via.placeholder.com/150' },
              { name: 'Amoxicilina 500mg', price: 'Q40.00', image: 'https://via.placeholder.com/150' }
          ]
      };
  }
};
</script>

<style scoped>
/* Estilos personalizados */
header nav a {
  transition: color 0.3s;
}
header nav a:hover {
  color: yellow;
}
input {
  border: 1px solid #ccc;
  padding: 8px;
}
</style>
