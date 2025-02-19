<script setup lang="ts">
defineProps({
  show: Boolean,
});
</script>

<template>
  <!-- Contenedor principal -->
  <div class="fixed inset-0 z-50 flex items-center justify-center">
    <!-- Fondo semitransparente -->
    <div class="bg-backdrop absolute inset-0 opacity-50 backdrop-blur-sm"></div>
    <!-- Contenido del modal -->
    <dialog
      class="bg-background relative z-60 w-full max-w-md rounded-lg px-4 py-2"
      :open="show"
    >
      <slot></slot>
    </dialog>
  </div>
</template>
