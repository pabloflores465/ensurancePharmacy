<!-- eslint-disable vue/multi-word-component-names -->
<!-- Catalog.vue (o como se llame tu componente) -->
<template>
  <Header />
  
  <div class="catalog-container">
    <!-- Filtros -->
    <div class="search-container">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="Buscar producto..."
        class="search-input"
      />
      <input
        type="text"
        v-model="activeIngredientFilter"
        placeholder="Principio activo"
        class="search-input"
      />
      <input
        type="text"
        v-model="brandFilter"
        placeholder="Marca"
        class="search-input"
      />
      <input
        type="number"
        v-model.number="minPrice"
        placeholder="Precio mínimo"
        class="search-input"
      />
      <input
        type="number"
        v-model.number="maxPrice"
        placeholder="Precio máximo"
        class="search-input"
      />
    </div>
    <h2 class="title">🛒 Catálogo de Productos</h2>
  
    <div class="product-grid">
      <div v-for="product in filteredProducts" :key="product.idMedicine" class="product-card">
        <!--<img :src="product.gallery[0]" :alt="product.name" class="product-image" />-->
        <h3 class="product-name">{{ product.name }}</h3>
        <p class="product-price">💰 Q{{ product.price.toFixed(2) }}</p>
        <router-link :to="`/producto/${product.idMedicine}`" class="buy-button">
          🛍️ Comprar
        </router-link>
        <button class="details-button" @click="openModal(product)">Ver detalles</button>
      </div>
    </div>
  </div>

  <!-- Modal (diseño dos columnas) -->
  <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <button class="close-modal" @click="closeModal">✖</button>
      
      <!-- Título del producto -->
      <h3 class="modal-title">{{ selectedProduct.name }}</h3>

      <!-- Contenedor con dos columnas: imágenes y detalles -->
      <div class="modal-body">
        <!-- Columna con la galería de imágenes -->
        <div class="product-images">
          <!--<img
            v-for="(img, index) in selectedProduct.gallery"
            :key="index"
            :src="img"
            class="detail-image"
          />-->
        </div>

        <!-- Columna con la información del producto -->
        <div class="product-info">
          <p><strong>Descripción:</strong> {{ selectedProduct.description }}</p>
          <p><strong>Ingrediente Activo:</strong> {{ selectedProduct.activeMedicament }}</p>
          <p><strong>Marca:</strong> {{ selectedProduct.brand }}</p>
          <p><strong>Precio:</strong> Q{{ selectedProduct.price }}</p>
          <!-- Agrega más campos que necesites, por ejemplo inventario, recomendaciones, etc. -->
          
          <div class="actions">
            <router-link :to="`/producto/${selectedProduct.idMedicine}`" class="buy-button">
              Comprar
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

// 1. Importa tus imágenes locales
/*import botellasImg from '@/assets/botellas.jpg'
import farmaciaImg from '@/assets/farmacia.png'
import medicinaImg from '@/assets/medicina.png'
import muletasImg from '@/assets/muletas.jpg'
import pastillasImg from '@/assets/pastillas.jpg'
import pildorasImg from '@/assets/pildoras.webp'
import masImg from '@/assets/mas.jpeg'*/
import axios from "axios";
const ip = process.env.VUE_APP_IP;

const products = ref([]);
// 2. Define tus productos utilizando las imágenes importadas
const fetchProduct = async () => {
  try {
    const response = await axios.get(`http://${ip}:8081/api2/medicines`);
    products.value = response.data;
    console.log(products.value);
  } catch (error) {
    console.error('Error fetching medicines:', error);
  }
};
fetchProduct();

// Manejo de modal
const showModal = ref(false);
const selectedProduct = ref({});

const openModal = (product) => {
  selectedProduct.value = product;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

// Filtros
const searchQuery = ref('');
const activeIngredientFilter = ref('');
const brandFilter = ref('');
const minPrice = ref(0);
const maxPrice = ref(1000);

const filteredProducts = computed(() => {
  return products.value.filter((product) => {
    const matchesName = product.name.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchesIngredient = product.activeMedicament.toLowerCase().includes(activeIngredientFilter.value.toLowerCase());
    const matchesBrand = product.brand.toLowerCase().includes(brandFilter.value.toLowerCase());
    const matchesMinPrice = product.price >= (minPrice.value || 0);
    const matchesMaxPrice = maxPrice.value ? product.price <= maxPrice.value : true;

    return (
      matchesName &&
      matchesIngredient &&
      matchesBrand &&
      matchesMinPrice &&
      matchesMaxPrice
    );
  });
});
</script>

<style scoped>
.catalog-container {
  text-align: center;
  padding: 20px;
  background-color: #f8f9fa;
}

.search-container {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.search-input {
  width: 200px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 16px;
}

.title {
  font-size: 24px;
  color: #1e40af;
  font-weight: bold;
  margin-bottom: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  padding: 20px;
}

.product-card {
  background: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.2s;
}

.product-card:hover {
  transform: scale(1.05);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

.product-name {
  font-size: 18px;
  margin: 10px 0;
}

.product-price {
  font-size: 16px;
  color: #16a34a;
  font-weight: bold;
}

.buy-button {
  background: #1e40af;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
  text-decoration: none; /* para lucir como botón y no un link subrayado */
}

.buy-button:hover {
  background: #1e3a8a;
}

.details-button {
  background: #4b5563;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 5px;
  font-size: 14px;
  cursor: pointer;
  margin-top: 10px;
  transition: background 0.3s;
}

.details-button:hover {
  background: #374151;
}

/* Modal estilos */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999; /* para que esté sobre otros elementos */
}

.modal-content {
  background: white;
  border-radius: 10px;
  padding: 20px;
  width: 90%;
  max-width: 700px;
  position: relative;
}

.close-modal {
  position: absolute;
  top: 10px;
  right: 15px;
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.modal-title {
  margin-bottom: 16px;
  font-size: 1.5rem;
  color: #1e40af;
}

/* Cuerpo del modal a dos columnas */
.modal-body {
  display: flex;
  flex-wrap: wrap; /* para que en pantallas pequeñas se adapte */
  gap: 20px;
}

/* Columna de imágenes */
.product-images {
  flex: 0 0 200px; /* ancho fijo de 200px */
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* Imágenes dentro de la galería */
.detail-image {
  width: 100%;
  border-radius: 6px;
  object-fit: cover;
}

/* Columna de información */
.product-info {
  flex: 1; /* ocupa el resto del espacio */
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.actions {
  margin-top: 15px;
  text-align: right; /* alineamos el botón a la derecha (opcional) */
}

/* Ajusta si necesitas inputs más anchos */
.search-input {
  width: 50%;
}
</style>