<template>
  <div id="app">
    <router-view /> <!-- Aquí se mostrarán las páginas según la ruta -->
  </div>
</template>

<script>
export default {
  name: "App"
};
</script>

<style>
/* Estilos globales opcionales */
body {
  font-family: Arial, sans-serif;
  background-color: #f5f5f5;
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
