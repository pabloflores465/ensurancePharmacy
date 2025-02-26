<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <Header />
    <div class="catalog-container">
      <h2 class="title">🛒 Catálogo de Productos</h2>
  
      <div class="product-grid">
        <div v-for="product in products" :key="product.id" class="product-card">
          <img :src="product.image" :alt="product.name" class="product-image" />
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-price">💰 {{ product.price }}</p>
          <button class="buy-button">🛍️ Comprar</button>
          <!-- Added details button -->
          <button class="details-button" @click="openModal(product)">Ver detalles</button>
        </div>
      </div>
    </div>
    <!-- Modal -->
    <div v-if="modalOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <button class="close-modal" @click="closeModal">✖</button>
        <h3>{{ selectedProduct.name }}</h3>
        <p>{{ selectedProduct.description }}</p>
        <div class="gallery">
          <img v-for="(img, index) in selectedProduct.gallery" :key="index" :src="img" class="gallery-img" />
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const products = ref([
    { id: 1, name: "Botellas Médicas", price: "Q50.00", image: "@/src/assets/botellas.jpg", description: "Descripción de Botellas Médicas", gallery: ["https://via.placeholder.com/300", "https://via.placeholder.com/300/ff0000"] },
    { id: 2, name: "Farmacia General", price: "Q150.00", image: "@/src/assets/farmacia.png", description: "Descripción de Farmacia General", gallery: ["https://via.placeholder.com/300", "https://via.placeholder.com/300/00ff00"] },
    { id: 3, name: "Medicina Variada", price: "Q30.00", image: "@/src/assets/medicina.png", description: "Descripción de Medicina Variada", gallery: ["https://via.placeholder.com/300", "https://via.placeholder.com/300/0000ff"] },
    { id: 4, name: "Muletas Ortopédicas", price: "Q250.00", image: "@/src/assets/muletas.jpg", description: "Descripción de Muletas Ortopédicas", gallery: ["https://via.placeholder.com/300", "https://via.placeholder.com/300/ffff00"] },
    { id: 5, name: "Pastillas", price: "Q20.00", image: "@/src/assets/pastillas.jpg", description: "Descripción de Pastillas", gallery: ["https://via.placeholder.com/300", "https://via.placeholder.com/300/ff00ff"] },
    { id: 6, name: "Píldoras Premium", price: "Q35.00", image: "@/src/assets/pildoras.webp", description: "Descripción de Píldoras Premium", gallery: ["https://via.placeholder.com/300", "https://via.placeholder.com/300/00ffff"] },
    { id: 7, name: "Más Productos", price: "Consultar", image: "@/src/assets/mas.jpeg", description: "Descripción de Más Productos", gallery: ["https://via.placeholder.com/300", "https://via.placeholder.com/300/cccccc"] },
  ]);
  
  const modalOpen = ref(false);
  const selectedProduct = ref({});
  
  const openModal = product => {
    selectedProduct.value = product;
    modalOpen.value = true;
  };
  
  const closeModal = () => {
    modalOpen.value = false;
  };
  </script>
  
  <style scoped>
  .catalog-container {
    text-align: center;
    padding: 20px;
    background-color: #f8f9fa;
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
  }
  
  .buy-button:hover {
    background: #1e3a8a;
  }

  /* Details button */
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
  
  /* Modal styles */
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
  }
  
  .modal-content {
    background: white;
    border-radius: 10px;
    padding: 20px;
    width: 90%;
    max-width: 600px;
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
  
  .gallery {
    margin-top: 15px;
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
  }
  
  .gallery-img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 5px;
  }
  </style>