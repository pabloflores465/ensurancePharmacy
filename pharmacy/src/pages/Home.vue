<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <HeaderComponent />
  
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

    <FooterComponent />
</template>

<script>
import HeaderComponent from '@/components/Header.vue';
import FooterComponent from '@/components/Footer.vue';

export default {
    components: {
        HeaderComponent,
        FooterComponent
    },
    data() {
        return {
            search: '',
            medicines: [
                { name: 'Paracetamol 500mg', price: 'Q25.00', image: '@/assets/medicina.png' },
                { name: 'Ibuprofeno 200mg', price: 'Q30.00', image: '@/assets/farmacia.png' },
                { name: 'Amoxicilina 500mg', price: 'Q40.00', image: 'https://via.placeholder.com/150' }
            ]
        };
    }
};


</script>
  <style scoped>
  /* Estilos generales */
  body {
      font-family: 'Arial', sans-serif;
      background-color: #f8f9fa;
  }
  
  /* Header */
  header {
      background-color: #2563eb; /* Azul fuerte */
      padding: 16px;
      color: white;
      font-weight: bold;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  
  /* Navegación */
  nav a {
      margin: 0 15px;
      color: white;
      text-decoration: none;
      font-size: 16px;
      font-weight: 500;
      transition: color 0.3s ease-in-out;
  }
  
  nav a:hover {
      color: #e2e8f0; /* Gris claro */
  }
  
  nav .login-btn {
      background-color: white;
      color: #2563eb;
      padding: 8px 16px;
      border-radius: 5px;
      font-weight: bold;
      transition: background-color 0.3s ease-in-out;
  }
  
  nav .login-btn:hover {
      background-color: #e2e8f0;
  }
  
  /* Hero/Banner */
  .hero {
      background-color: #bfdbfe; /* Azul claro */
      padding: 60px 20px;
      text-align: center;
  }
  
  .hero h2 {
      font-size: 2.5rem;
      font-weight: bold;
      color: #111827;
  }
  
  .hero p {
      font-size: 1.2rem;
      color: #4b5563;
  }
  
  .hero input {
      width: 50%;
      padding: 12px;
      margin-top: 10px;
      border-radius: 8px;
      border: none;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      font-size: 16px;
  }
  
  /* Sección de Productos Destacados */
  .products {
      background-color: #f1f5f9; /* Gris claro */
      padding: 50px 20px;
      text-align: center;
  }
  
  .products h2 {
      font-size: 2rem;
      font-weight: bold;
      margin-bottom: 20px;
  }
  
  .product-grid {
      display: flex;
      justify-content: center;
      gap: 20px;
  }
  
  .product-card {
      background: white;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      width: 280px;
      text-align: center;
  }
  
  .product-card img {
      width: 80px;
      height: 80px;
      object-fit: contain;
      margin-bottom: 10px;
  }
  
  .product-card h3 {
      font-size: 1.2rem;
      font-weight: bold;
  }
  
  .product-card p {
      font-size: 1rem;
      color: #16a34a; /* Verde */
      font-weight: bold;
  }
  
  /* Sección Beneficios */
  .benefits {
      background-color: #e5e7eb; /* Gris más claro */
      padding: 40px 20px;
      text-align: center;
  }
  
  .benefits h2 {
      font-size: 2rem;
      font-weight: bold;
  }
  
  .benefits p {
      font-size: 1rem;
      color: #4b5563;
  }
  
  /* Mapa de Sucursales */
  .map-section {
      background-color: #f1f5f9;
      padding: 50px 20px;
      text-align: center;
  }
  
  .map-placeholder {
      background-color: #9ca3af; /* Gris */
      height: 250px;
      border-radius: 10px;
  }
  
  /* Footer */
  footer {
      background-color: #2563eb;
      color: white;
      padding: 20px;
      text-align: center;
      font-size: 14px;
      font-weight: bold;
  }
  </style>
  