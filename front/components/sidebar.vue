<script setup lang="ts">
const toggleSidebar = useToggleSidebar()
</script>
<template>
  <div class="p-2 sm:w-[20%] bg-[#121827] justify-center" :hidden="!toggleSidebar">
    <NuxtLink to="/" class="flex justify-center mb-2">
      <img
        class="rounded-full border-4 border-yellow-300 hover:scale-105"
        src="../public/logo.png"
        height="100px"
        width="100px"
      />
    </NuxtLink>
    <Search class="mb-2" />
    <Button styles="w-full mb-2 flex justify-center"> Sign Up </Button>
    <Button styles="w-full flex justify-center">Log In</Button>
  </div>
</template>
