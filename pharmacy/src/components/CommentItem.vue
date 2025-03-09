<template>
  <div class="comment-item">
    <p><strong>{{ comment.user.name }}</strong>:</p>
    <p>{{ comment.commentText }}</p>
    <button @click="$emit('reply', comment)">Responder</button>
    <div class="replies" v-if="childComments.length">
      <CommentItem
          v-for="child in childComments"
          :key="child.idComments"
          :comment="child"
          :all-comments="allComments"
          @reply="$emit('reply', $event)"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: 'CommentItem',
  props: {
    comment: { type: Object, required: true },
    allComments: { type: Array, required: true }
  },
  computed: {
    childComments() {
      return this.allComments.filter(
          c => c.prevComment && c.prevComment.idComments === this.comment.idComments
      );
    }
  }
};
</script>

<style scoped>
.comment-item {
  margin-left: 20px;
  border-left: 1px solid #ccc;
  padding-left: 10px;
  margin-top: 10px;
}
</style>