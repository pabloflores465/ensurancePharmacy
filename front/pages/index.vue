<script setup lang="ts">
interface Comment {
  id: number;
  text: string;
  author: string;
  date: string;
  id_prev: number | null;
}
const sampleComments: Comment[] = [
  { id: 1, text: "Este es el comentario raíz", author: "Alice", date: "2025-02-26", id_prev: null },
  { id: 2, text: "Esta es una respuesta al comentario raíz", author: "Bob", date: "2025-02-26", id_prev: 1 },
  { id: 3, text: "Otro comentario raíz", author: "Charlie", date: "2025-02-26", id_prev: null },
  { id: 4, text: "Respuesta al comentario de Charlie", author: "Diana", date: "2025-02-26", id_prev: 3 },
  { id: 5, text: "Respuesta anidada a Bob", author: "Eve", date: "2025-02-26", id_prev: 2 },
];
</script>

<template>
  <main class="bg-image-[url('/carCrash.jpg')]">
    <Carrousel />
    <button
      @click="() => notify({ id: 1, title: 'hola', description: 'hola' })"
      class="btn"
    >
      notify
    </button>
    <Comments :comments="sampleComments" />
  </main>
</template>
