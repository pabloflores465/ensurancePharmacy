<script setup lang="ts">
  const search = useSearch()
</script>

<template>
  <div class="bg-[url('https://cdn.pixabay.com/photo/2020/11/03/15/32/man-5710164_1280.jpg')] bg-cover bg-center w-full h-full">
    <Search v-if="search"/>
    <Policy/>
  </div>
</template>
