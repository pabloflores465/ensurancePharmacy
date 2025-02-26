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
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const products = ref([
    { id: 1, name: "Botellas Médicas", price: "Q50.00", image: "@/src/assets/botellas.jpg" },
    { id: 2, name: "Farmacia General", price: "Q150.00", image: "@/src/assets/farmacia.png" },
    { id: 3, name: "Medicina Variada", price: "Q30.00", image: "@/src/assets/medicina.png" },
    { id: 4, name: "Muletas Ortopédicas", price: "Q250.00", image: "@/src/assets/muletas.jpg" },
    { id: 5, name: "Pastillas", price: "Q20.00", image: "@/src/assets/pastillas.jpg" },
    { id: 6, name: "Píldoras Premium", price: "Q35.00", image: "@/src/assets/pildoras.webp" },
    { id: 7, name: "Más Productos", price: "Consultar", image: "@/src/assets/mas.jpeg" },
  ]);
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
  </style>