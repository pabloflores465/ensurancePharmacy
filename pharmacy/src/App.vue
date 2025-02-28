<template>
  <div id="app">
    <Header />
    <router-view /> <!-- Aquí se mostrarán las páginas según la ruta -->
    
  </div>
</template>

<script>
import Header from '@/components/Header.vue';

export default {
  name: "App",
  components: { Header  }
};
</script>

<style>
/* Estilos globales opcionales */
body {
  font-family: Arial, sans-serif;
  background-color: #f1f9ff;
  margin: 0;
}
</style>


<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
