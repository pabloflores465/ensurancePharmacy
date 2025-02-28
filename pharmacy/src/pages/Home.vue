<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <div class="dashboard">
      <!-- Sidebar Izquierdo -->
      <aside class="sidebar">
        <div class="contact-icons">
          <button class="icon-btn">💬</button>
          <button class="icon-btn">📞</button>
          <button class="icon-btn">📱</button>
        </div>
        <ul class="sidebar-menu">
          <li>Equipo Médico</li>
          <router-link to="/aseguradoras" class="nav-item">Aseguradoras Afiliadas</router-link>
          <li>Canje de puntos</li>
          <li>Pagos</li>
        </ul>
      </aside>
  
      <!-- Contenido Principal -->
      <main class="content">
        <!-- Carrusel de Ofertas -->
        <section class="carousel">
          <img :src="carouselImages[currentImage]" class="carousel-img" />
          <button @click="prevImage" class="carousel-btn left">◀</button>
          <button @click="nextImage" class="carousel-btn right">▶</button>
        </section>
  
        <!-- Sección de Redes Sociales y Ofertas -->
        <section class="offers">
          <h2 class="section-title">Redes Sociales</h2>
          <div class="offers-container">
            <div v-for="(offer, index) in offers" :key="index" class="offer-card">
              <img :src="offer.image" :alt="offer.title" class="offer-img" />
              <p class="offer-title">{{ offer.title }}</p>
            </div>
          </div>
        </section>
      </main>
    </div>
  </template>
  
  <script setup>
  import { ref } from "vue";
  
  // Lista de imágenes del carrusel (URLs)
  const carouselImages = ref([
  "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.freepik.es%2Ffotos-vectores-gratis%2Fmedicamentos-dibujo&psig=AOvVaw0yIWmNY1b5NZT8f5l8AnK_&ust=1740680728122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCLitqpX74YsDFQAAAAAdAAAAABAE",
  "https://www.google.com/url?sa=i&url=https%3A%2F%2Fmedicamentos.mspas.gob.gt%2F&psig=AOvVaw0yIWmNY1b5NZT8f5l8AnK_&ust=1740680728122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCLitqpX74YsDFQAAAAAdAAAAABAG",
  "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.quironsalud.com%2Fblogs%2Fes%2Falergologia-infantil%2Falergia-medicamentos-infancia&psig=AOvVaw0yIWmNY1b5NZT8f5l8AnK_&ust=1740680728122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCLitqpX74YsDFQAAAAAdAAAAABAN"
]);
  
  // Lista de ofertas con imágenes desde URL
  const offers = ref([
  { title: "Omron - Presión Arterial", image: "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.latimes.com%2Fespanol%2Feeuu%2Farticulo%2F2024-12-23%2Fla-fda-afirma-que-muchos-medicamentos-para-el-resfriado-no-funcionan-hara-cambios&psig=AOvVaw0yIWmNY1b5NZT8f5l8AnK_&ust=1740680728122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCLitqpX74YsDFQAAAAAdAAAAABAc" },
  { title: "Sukrol - Suplemento Cerebral", image: "https://d2j6dbq0eux0bg.cloudfront.net/images/68223696/3347644729.jpg" },
  { title: "10% Descuento en ISDIN", image: "https://www.isdin.com/assets/uploads/2021/09/fotoprotector-banner.jpg" }
]);


  
  // Estado del carrusel
  const currentImage = ref(0);
  
  // Funciones para cambiar imágenes del carrusel
  const prevImage = () => {
    currentImage.value = (currentImage.value - 1 + carouselImages.value.length) % carouselImages.value.length;
  };
  
  const nextImage = () => {
    currentImage.value = (currentImage.value + 1) % carouselImages.value.length;
  };
  </script>
  
  <style scoped>
  /* Layout principal */
  .dashboard {
    display: flex;
    height: 100vh;
    background-color: #f8f9fa;
  }
  
  /* Sidebar */
  .sidebar {
    width: 250px;
    background: #729ef1;
    color: white;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .contact-icons {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }
  
  .icon-btn {
    background: white;
    color: #002366;
    padding: 8px;
    border-radius: 50%;
    cursor: pointer;
  }
  
  .sidebar-menu {
    list-style: none;
    padding: 0;
  }
  
  .sidebar-menu li {
    padding: 10px;
    border-bottom: 1px solid white;
    text-align: center;
  }
  
  /* Contenido Principal */
  .content {
    flex-grow: 1;
    padding: 20px;
  }
  
  /* Carrusel */
  .carousel {
    position: relative;
    width: 100%;
    max-width: 900px;
    margin: 0 auto;
  }
  
  .carousel-img {
    width: 100%;
    border-radius: 10px;
  }
  
  .carousel-btn {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    background: rgba(0, 0, 0, 0.5);
    color: white;
    border: none;
    padding: 10px;
    cursor: pointer;
  }
  
  .left {
    left: 10px;
  }
  .right {
    right: 10px;
  }
  
  /* Sección de Ofertas */
  .offers {
    margin-top: 20px;
  }
  
  .section-title {
    text-align: center;
    font-size: 24px;
    color: #002366;
    margin-bottom: 10px;
  }
  
  .offers-container {
    display: flex;
    justify-content: center;
    gap: 15px;
  }
  
  .offer-card {
    background: white;
    padding: 10px;
    text-align: center;
    border-radius: 5px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .offer-img {
    width: 100%;
    border-radius: 5px;
  }
  
  .offer-title {
    font-weight: bold;
    margin-top: 5px;
  }
  </style>