<script setup lang="ts">
const dark = darkMode();
</script>

<template>
  <main :class="[`bg-image-[url('/carCrash.jpg')]`, dark ? 'dark' : '']">
    <Carrousel />
    <Medicine />
    <button @click="() => notify({ title: 'hola' })" class="btn">notify</button>
  </main>
</template>
