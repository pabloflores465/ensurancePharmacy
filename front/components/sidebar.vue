<script setup lang="ts">
const toggleSidebar = useToggleSidebar()

function toggle(){
    toggleSidebar.value = !toggleSidebar.value
    console.log(toggleSidebar.value)
}
</script>
<template>
  <div class="p-2 border-r-2 border-white">
    <NuxtLink to="/" class="flex justify-center mb-2">
      <img
        class="rounded-full border-2 border-yellow-300 hover:scale-105"
        src="../public/logo.png"
        height="40px"
        width="40px"
      />
    </NuxtLink> 
    <button @click="toggle" >
      <svg stroke="currentColor" fill="white" stroke-width="0" viewBox="0 0 512 512" height="40px" width="40px" xmlns="http://www.w3.org/2000/svg"><path d="M256 256c52.805 0 96-43.201 96-96s-43.195-96-96-96-96 43.201-96 96 43.195 96 96 96zm0 48c-63.598 0-192 32.402-192 96v48h384v-48c0-63.598-128.402-96-192-96z"></path></svg>
    </button>
  </div>
  <div class="p-2 sm:w-[20%] bg-[#121827] justify-center" :hidden="!toggleSidebar">
    <Search class="mb-2" />
    <NuxtLink to="/signup" class="bg-yellow-300 hover:bg-yellow-400 text-[#121827] py-2 px-4 rounded-full shadow transition duration-300 ease-in-out transform hover:scale-105 font-bold w-full mb-2 flex justify-center"> Sign Up </NuxtLink>
    <Button styles="w-full flex flex justify-center">Log In</Button>
  </div>
</template>
