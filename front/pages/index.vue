<script setup lang="ts"></script>

<template>
  <main class="bg-image-[url('/carCrash.jpg')]">
    <Carrousel />
    <button
      @click="() => notify({ id: 1, title: 'hola', description: 'hola' })"
      class="btn"
    >
      notify
    </button>
  </main>
</template>
