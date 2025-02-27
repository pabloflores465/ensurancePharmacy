<template>
  <Header />
  <div class="product-details-container">
    <div v-if="product">
      <h2 class="product-title">{{ product.name }}</h2>
      <img :src="product.image" :alt="product.name" class="product-image" />
      <p class="product-description">{{ product.description }}</p>
      
      <!-- Comments Section -->
      <section class="comments-section">
        <h3>Comentarios</h3>
        <div v-if="product.comments.length">
          <div v-for="comment in product.comments" :key="comment.id" class="comment">
            <p>{{ comment.text }}</p>
          </div>
        </div>
        <div v-else>
          <p>No hay comentarios aún.</p>
        </div>
        <form @submit.prevent="submitComment" class="comment-form">
          <textarea v-model="newComment" placeholder="Escribe tu comentario..." class="comment-input"></textarea>
          <button type="submit" class="comment-submit">Enviar</button>
        </form>
      </section>
    </div>
    <div v-else>
      <p>Producto no encontrado.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import Header from '@/components/Header.vue';

const route = useRoute();
const productId = route.params.id;

// Dummy products list for demonstration
const products = [
  { id: '1', name: "Botellas Médicas", image: "@/src/assets/botellas.jpg", description: "Descripción de Botellas Médicas", comments: [] },
  { id: '2', name: "Farmacia General", image: "@/src/assets/farmacia.png", description: "Descripción de Farmacia General", comments: [] },
  { id: '3', name: "Medicina Variada", image: "@/src/assets/medicina.png", description: "Descripción de Medicina Variada", comments: [] }
  // ...existing products...
];

const product = computed(() => products.find(p => p.id === productId));

const newComment = ref('');

const submitComment = () => {
  if (newComment.value.trim() && product.value) {
    product.value.comments.push({ id: Date.now(), text: newComment.value });
    newComment.value = '';
  }
};
</script>

<style scoped>
.product-details-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background: #f8f9fa;
  text-align: center;
  border-radius: 10px;
}

.product-title {
  font-size: 28px;
  color: #1e40af;
  margin-bottom: 20px;
}

.product-image {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 10px;
  margin-bottom: 20px;
}

.product-description {
  font-size: 18px;
  color: #333;
  margin-bottom: 30px;
}

.comments-section {
  text-align: left;
  margin-top: 30px;
}

.comments-section h3 {
  font-size: 22px;
  color: #1e40af;
  margin-bottom: 10px;
}

.comment {
  background: #fff;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 10px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.comment-form {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
}

.comment-input {
  resize: vertical;
  min-height: 80px;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin-bottom: 10px;
}

.comment-submit {
  align-self: flex-end;
  background: #1e40af;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
}

.comment-submit:hover {
  background: #1e3a8a;
}
</style>
