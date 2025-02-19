<script setup lang="ts">
const dark = darkMode();
</script>

<template>
  <main
    :class="[
      `bg-image-[url('https://cdn.prod.website-files.com/6466101d017ab9d60c8d0137/668ce90cd1d21a2f4e8a8536_Repeat%20Prescriptions.jpg')]`,
      dark ? 'dark' : '',
    ]"
  ></main>
</template>

<style scoped></style>
