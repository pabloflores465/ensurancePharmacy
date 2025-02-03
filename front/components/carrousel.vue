<template>
  <div class="relative w-full overflow-hidden">
    <!-- Contenedor de diapositivas -->
    <div
      class="flex transition-transform duration-500 ease-out"
      :style="{ transform: `translateX(-${activeIndex * 100}%)` }"
    >
      <div v-for="(slide, index) in slides" :key="index" class="min-w-full">
        <img
          :src="slide.image"
          :alt="slide.alt"
          class="w-full h-64 object-cover"
        />
      </div>
    </div>

    <!-- Botones de navegación -->
    <Button
      :click="prev"
      styles="absolute left-0 top-1/2 transform -translate-y-1/2 bg-opacity-50 text-white p-0 m-2 focus:outline-none"
    >
    <svg stroke="#121827" fill="currentColor" stroke-width="0" viewBox="0 0 24 24" height="40px" width="40px" xmlns="http://www.w3.org/2000/svg"><polyline fill="none" stroke-width="2" points="7 2 17 12 7 22" transform="matrix(-1 0 0 1 24 0)"></polyline></svg>
    </Button>
    <Button
      :click="next"
      class="absolute right-0 top-1/2 transform -translate-y-1/2 bg-opacity-50 text-white p-0 m-2 focus:outline-none"
    >
    <svg stroke="#121827" fill="currentColor" stroke-width="0" viewBox="0 0 24 24" height="40px" width="40px" xmlns="http://www.w3.org/2000/svg"><polyline fill="none" stroke-width="2" points="7 2 17 12 7 22"></polyline></svg>
    </Button>

    <!-- Indicadores -->
    <div
      class="absolute bottom-0 left-1/2 transform -translate-x-1/2 flex space-x-2 p-4"
    >
      <span
        v-for="(slide, index) in slides"
        :key="index"
        @click="goToSlide(index)"
        class="w-3 h-3 rounded-full cursor-pointer"
        :class="{
          'bg-white': activeIndex === index,
          'bg-gray-400': activeIndex !== index,
        }"
      ></span>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

// Diapositivas con imágenes relacionadas a seguros
const slides = [
  { image: "/attend.jpg", alt: "Seguro de Auto" },
  {
    image: "https://source.unsplash.com/800x400/?insurance,home",
    alt: "Seguro de Hogar",
  },
  {
    image: "https://source.unsplash.com/800x400/?insurance,life",
    alt: "Seguro de Vida",
  },
];

const activeIndex = ref(0);

// Función para avanzar al siguiente slide (vuelve al primero al llegar al final)
const next = () => {
  activeIndex.value = (activeIndex.value + 1) % slides.length;
};

// Función para retroceder al slide anterior (vuelve al último si se está en el primero)
const prev = () => {
  activeIndex.value = (activeIndex.value - 1 + slides.length) % slides.length;
};

// Función para ir a un slide específico al hacer clic en un indicador
const goToSlide = (index) => {
  activeIndex.value = index;
};
</script>

<style scoped>
/* Puedes agregar estilos específicos para este componente si es necesario */
</style>
