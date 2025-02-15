<script setup lang="ts">
  withDefaults(defineProps<{type?:"loading"|"success"|"error", title?:string, description?:string}>(),{
    type: "loading",
    title: "Notification 1",
    description:
      "Tu solicitud está siendo procesada. Por favor, espera mientras completamos la operación.",
  })
  const showNotification:Ref<boolean> = ref(false)
  onMounted(setTimeout(()=>{showNotification.value=false},5000))
</script>

<template>
  <div v-if="showNotification" class="absolute inset-y-0 left-0"></div>
</template>
