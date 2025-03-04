<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="comments-container">
    <h3>Comentarios</h3>
    <div v-if="comments.length">
      <div v-for="comment in comments" :key="comment.id" class="comment">
        <p><strong>{{ comment.author }}</strong>:</p>
        <p>{{ comment.text }}</p>
      </div>
    </div>
    <div v-else>
      <p>No hay comentarios aún.</p>
    </div>
    <div class="add-comment">
      <input v-model="newCommentAuthor" placeholder="Tu nombre" />
      <textarea v-model="newCommentText" placeholder="Escribe un comentario..."></textarea>
      <button @click="addComment">Agregar Comentario</button>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  name: 'ProductComments', // Renamed from 'Comentarios ' to 'ProductComments'
  props: {
    initialComments: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const comments = ref([...props.initialComments]);
    const newCommentAuthor = ref('');
    const newCommentText = ref('');

    const addComment = () => {
      if (newCommentAuthor.value && newCommentText.value) {
        comments.value.push({
          id: Date.now(),
          author: newCommentAuthor.value,
          text: newCommentText.value
        });
        newCommentAuthor.value = '';
        newCommentText.value = '';
      }
    };

    return {
      comments,
      newCommentAuthor,
      newCommentText,
      addComment
    };
  }
};
</script>

<style scoped>
.comments-container {
  margin-top: 20px;
}

.comment {
  background: #f9f9f9;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 10px;
}

.add-comment {
  margin-top: 20px;
}

.add-comment input,
.add-comment textarea {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.add-comment button {
  padding: 10px 15px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.add-comment button:hover {
  background: #1e3a8a;
}
</style>
