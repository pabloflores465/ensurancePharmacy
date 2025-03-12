<template>
  <div class="comments-container">
    <h3>Comentarios</h3>
    <div v-if="replyComment" class="reply-info">
      Respondiendo a {{ replyComment.user.name }}
      <button @click="cancelReply">Cancelar Respuesta</button>
    </div>
    <div v-if="comments.length">
      <CommentItem
          v-for="comment in topLevelComments"
          :key="comment.idComments"
          :comment="comment"
          :all-comments="comments"
          @reply="setReply"
      />
    </div>
    <div v-else>
      <p>No hay comentarios aún.</p>
    </div>
    <div class="add-comment" v-if="isLoggedIn">
      <input v-model="newCommentText" placeholder="Escribe un comentario..." />
      <button @click="addComment">Agregar Comentario</button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useUserStore } from "@/stores/userStore";
import CommentItem from './CommentItem.vue';

const ip = process.env.VUE_APP_IP;

export default {
  name: 'ProductComments',
  components: { CommentItem },
  props: {
    initialComments: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const userStore = useUserStore();
    const route = useRoute();
    const routeId = route.params.id;
    const comments = ref(props.initialComments);
    const newCommentText = ref('');
    const replyComment = ref(null);
    const isLoggedIn = computed(() => {
      return !!(userStore.user && userStore.user.idUser);
    });

    onMounted(async () => {
      try {
        console.log(ip);
        const res = await fetch(`http://${ip}:8081/api2/comments`);
        if (res.ok) {
          const data = await res.json();
          console.log('Comentarios obtenidos:', data);
          comments.value = data;
        }
      } catch (error) {
        console.error('Error al obtener comentarios', error);
      }
    });

    const topLevelComments = computed(() => {
      const filtered = comments.value.filter(c =>
        !c.prevComment && c.medicine && Number(c.medicine.idMedicine) === Number(routeId)
      );
      console.log('Top level comments filtrados por producto:', filtered);
      return filtered;
    });

    const addComment = async () => {
      if(newCommentText.value) {
        const payload = {
          user: userStore.user,
          commentText: newCommentText.value,
          medicine: { idMedicine: Number(routeId) }
        };

        if (replyComment.value) {
          payload.prevComment = replyComment.value;
        }

        try {
          const res = await fetch(`http://${ip}:8081/api2/comments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
          });
          if(res.ok) {
            const newComment = await res.json();
            comments.value.push(newComment);
            newCommentText.value = '';
            replyComment.value = null;
          } else {
            console.error('Error al crear comentario');
            console.log(JSON.stringify((payload)))
          }
        } catch(error) {
          console.error('Error en la petición POST', error);
        }
      }
    };

    const setReply = (comment) => {
      replyComment.value = comment;
    };

    const cancelReply = () => {
      replyComment.value = null;
    };

    return {
      comments,
      topLevelComments,
      newCommentText,
      addComment,
      replyComment,
      setReply,
      cancelReply,
      isLoggedIn
    };
  }
};
</script>

<style scoped>
.comments-container {
  margin-top: 20px;
}

.reply-info {
  background: #eef;
  padding: 5px 10px;
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