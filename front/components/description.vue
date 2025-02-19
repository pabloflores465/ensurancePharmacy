<script setup lang="ts">
withDefaults(defineProps<{ name: string; show: boolean }>(), {
  name: "prescription",
  show: false,
});
const dark = darkMode();
</script>
<template>
  <dialog
    :class="[
      `card text-primary z-50 m-1 max-w-['150px'] px-2 py-1`,
      dark ? 'dark' : '',
    ]"
    :open="show"
  >
    {{ name }}
  </dialog>
</template>
