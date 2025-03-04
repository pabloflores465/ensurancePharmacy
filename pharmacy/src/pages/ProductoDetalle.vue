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
  
        <p><strong>Precio:</strong> {{ product.price }}</p>
        <p><strong>Descripción:</strong> {{ product.description }}</p>
        <p><strong>Inventario:</strong> {{ product.inventory }}</p>
        <p><strong>Ingrediente Activo:</strong> {{ product.activeIngredient }}</p>
        <p><strong>Descripción General:</strong> {{ product.generalDescription }}</p>
        <p><strong>Recomendación de Uso:</strong> {{ product.usageRecommendation }}</p>
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

  export default {
    name: "ProductoDetalle",
    components: {
      Comentarios
    },
    // Permite que el ID llegue como prop (asegúrate de que en el router usas props: true)
    props: ["id"],
    
    data() {
      return {
        product: null,
        productComments: [
          { id: 1, author: 'Juan', text: 'Muy buen producto!' },
          { id: 2, author: 'Maria', text: 'Me ayudó mucho, gracias!' }
        ]
      };
    },
    
    mounted() {
      // Ejemplo local: array de productos “mock”
      const productos = [
        {
          id: '1',
          name: 'Botellas Médicas',
          price: '$35.00',
          images: [
            require('@/assets/botellas.jpg'),
            require('@/assets/medicina.png')
          ],
          description: 'Detalle de Botellas Médicas',
          inventory: 100,
          activeIngredient: 'Acetaminofén',
          generalDescription: 'Producto para uso médico, etc.',
          usageRecommendation: 'Recomendación de uso...'
        },
        {
          id: '2',
          name: 'Farmacia General',
          price: '$25.00',
          images: [
            require('@/assets/farmacia.png'),
            require('@/assets/mas.jpeg')
          ],
          description: 'Detalle de Farmacia General',
          inventory: 50,
          activeIngredient: 'Ibuprofeno',
          generalDescription: 'Preparado para uso general en farmacias.',
          usageRecommendation: 'Recomendado para el alivio del dolor.'
        }
        // Puedes agregar más productos según lo necesites
      ];
    
      // Se filtra el producto que coincida con el ID recibido en la ruta
      this.product = productos.find(prod => prod.id === '1'); // Mostrar el detalle del producto 1
    }
  };
  </script>
  
  <style scoped>
  /* Estilos opcionales */
  </style>