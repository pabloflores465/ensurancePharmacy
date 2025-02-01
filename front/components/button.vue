<script setup lang="ts">
withDefaults(
  defineProps<{
    click?: () => void;
    styles?: string;
    label?: string;
    layout?: string;
  }>(),
  {
    click: () => {},
    styles: "",
    label: "Click me",
    layout: "",
  }
);
</script>

<template>
  <div :class="layout">
    <button
      @click="click"
      :class="[
        'bg-yellow-300 hover:bg-yellow-400 text-[#121827] py-2 px-4 rounded-full shadow transition duration-300 ease-in-out transform hover:scale-105 font-bold',
        styles,
      ]"
    >
      <div class="flex items-center">
        <slot></slot>
      </div>
    </button>
  </div>
</template>
