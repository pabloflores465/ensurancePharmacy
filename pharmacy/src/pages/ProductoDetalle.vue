<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <div>
      <h1>Detalle del Producto</h1>
  
      <!-- Si el producto existe, mostramos su información -->
      <div v-if="product">
        <h2>{{ product.name }}</h2>
        <!-- Mostrar imagen principal si existe -->
        <img 
          v-if="product.images && product.images.length" 
          :src="product.images[0]" 
          alt="Imagen principal del producto" 
          style="width: 200px; border-radius: 8px;"
        />
  
        <p><strong>Precio:</strong> Q{{ product.price }}</p>
        <p><strong>Descripción:</strong> {{ product.description }}</p>
        <p><strong>Inventario:</strong> {{ product.stock }} unidades</p>
        <p><strong>Ingrediente Activo:</strong> {{ product.activeMedicament }}</p>
        <p><strong>Descripción:</strong> {{ product.description }}</p>
        <p><strong>Concentración:</strong> {{ product.concentration }}</p>
        <p><strong>Presentación:</strong> {{ product.presentacion }}</p>
        <p><strong>Marca:</strong> {{ product.brand }}</p>
        <Comentarios :initialComments="productComments" />
      </div>
      <!-- Si no existe el producto, mostramos un mensaje de error -->
      <div v-else>
        <p>Producto no encontrado.</p>
      </div>
    </div>
  </template>
  
  <script>
  import Comentarios from '@/components/Comentarios.vue';
  import axios from "axios";
  const ip = process.env.VUE_APP_IP;

  export default {
    name: "ProductoDetalle",
    components: {
      Comentarios
    },
    // Se recibe el id del producto vía props (asegúrate de que el router lo pase correctamente)
    props: ["id"],
    data() {
      return {
        product: null,
        productComments: [
          { id: 1, author: 'Juan', text: '¡Muy buen producto!' },
          { id: 2, author: 'Maria', text: 'Me ayudó mucho, gracias!' }
        ]
      };
    },
    mounted() {
      const routeId = this.$route.params.id;
      axios.get(`http://${ip}:8081/api2/medicines`)
          .then(response => {
            // Se asume que la API retorna un array de productos
            const products = response.data;
            console.log(this.$route.params.id);
            this.product = products.find(prod => prod.idMedicine === Number(routeId));
          })
          .catch(error => {
            console.error('Error fetching medicines:', error);
          });
    }
  };
  </script>
  
  <style scoped>
  /* Estilos opcionales */
  </style>