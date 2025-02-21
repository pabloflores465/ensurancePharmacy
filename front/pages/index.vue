<script setup lang="ts"></script>

<template>
  <main class="bg-image-[url('/carCrash.jpg')]">
    <Carrousel />
    <Medicine />
    <button @click="() => notify({ id: 1, title: 'hola' })" class="btn">
      notify
    </button>
  </main>
</template>
