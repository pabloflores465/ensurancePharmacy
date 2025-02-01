<script setup lang="ts">
const toggleSidebar = useToggleSidebar()
function toggle(){
    toggleSidebar.value = !toggleSidebar.value
    console.log(toggleSidebar.value)
}
</script>
<template>
  <div
    class="fixed bottom-[15%] right-[92%] bg-yellow-300 p-2 text-white rounded-full shadow-lg flex items-center justify-center hover:bg-yellow-400 transition-colors"
  >
    <Button label="" :onclick="toggle"/>
  </div>
</template>
