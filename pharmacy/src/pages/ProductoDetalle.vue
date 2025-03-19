<!-- eslint-disable vue/multi-word-component-names -->
<template>
    <div>
      <h1>Detalle del Producto</h1>

      <!-- Si el producto existe, mostramos su información -->
      <div v-if="product">
        <div class="product-container" style="display: flex;">
          <div class="product-details" style="flex: 1;">
            <h2>{{ product.name }}</h2>
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
          </div>
          <div class="purchase-section" style="margin-left: 20px;">
            <label for="quantity">Cantidad:</label>
            <input
              type="number"
              id="quantity"
              v-model.number="quantity"
              :min="1"
              :max="product.stock"
              style="width: 60px; margin-left: 10px;"
            />
            <button
              @click="purchaseProduct"
              :disabled="!quantity || quantity < 1 || quantity > product.stock"
              style="margin-left: 10px;"
            >
              Comprar
            </button>
          </div>
        </div>
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
  import { useUserStore } from '@/stores/userStore';


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
        quantity: 1,
        productComments: [
          { idComments: 1, user: { name: 'Juan' }, commentText: '¡Muy buen producto!' },
          { idComments: 2, user: { name: 'Maria' }, commentText: 'Me ayudó mucho, gracias!' }
        ]
      };
    },
    mounted() {
      const routeId = this.$route.params.id;
      axios.get(`http://${ip}:8081/api2/medicines`)
          .then(response => {
            const products = response.data;
            console.log(this.$route.params.id);
            this.product = products.find(prod => prod.idMedicine === Number(routeId));
          })
          .catch(error => {
            console.error('Error fetching medicines:', error);
          });
    },
    methods: {
      purchaseProduct() {
        const userStore = useUserStore();
        const userId = userStore.user.idUser;

        // Obtener todas las órdenes para el usuario
        axios.get(`http://${ip}:8081/api2/orders?userId=${userId}`)
          .then(response => {
            console.log('Response from orders GET:', response.data);
            const orders = response.data;
            const orderInProgress = orders.find(order => order.status === 'En progreso');
            if (orderInProgress) {
              console.log('Found order in progress:', orderInProgress);
              return orderInProgress;
            } else {
              return axios.post(`http://${ip}:8081/api2/orders`, {
                user: { idUser: userId },
                status: 'En progreso'
              }).then(response => {
                console.log('Response from orders POST:', response.data);
                return response.data;
              });
            }
          })
          .then(order => {
            console.log('ORDEN:', order);
            return axios.get(`http://${ip}:8081/api2/order_medicines?id=${order.idOrder}%2C${this.product.idMedicine}`)
                .then(response => {
                  let items = response.data;
                  if (!Array.isArray(items)) items = items ? [items] : [];
                  return items;
                })
                .catch(error => {
                  if (error.response?.status === 404) return [];    // no existe → crear
                  throw error;
                })
                .then(orderMedicines => {
                  const existing = orderMedicines.find(om => om.medicine.idMedicine === this.product.idMedicine);
                  const payload = {
                    orders: order,
                    medicine: { idMedicine: this.product.idMedicine },
                    quantity: this.quantity,
                    cost: this.product.price,
                    total: this.product.price * this.quantity
                  };
                  return existing
                      ? axios.put(`http://${ip}:8081/api2/order_medicines`, payload)
                      : axios.post(`http://${ip}:8081/api2/order_medicines`, payload);
                });
          })
          .then(response => {
            console.log('Orden y medicamento añadidos o actualizados:', response.data);
          })
          .catch(error => {
            console.error('Error en el proceso de la orden:', error);
          });
      }
    }
  };
  </script>

  <style scoped>
  /* Estilos opcionales */
  </style>