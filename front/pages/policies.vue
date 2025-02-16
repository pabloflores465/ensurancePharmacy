<script setup lang="ts">
  const search = useSearch()
</script>

<template>
  <main class="bg-image-[url(https://cdn.pixabay.com/photo/2020/11/03/15/32/man-5710164_1280.jpg)]">
    <Search v-if="search"/>
    <Policy/>
  </main>
</template>
