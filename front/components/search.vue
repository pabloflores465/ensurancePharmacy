<script setup lang="ts">
withDefaults(
  defineProps<{
    styles?: string;
  }>(),
  {
    styles: "",
  }
);
</script>
<template>
  <div
    :class="[
      'flex items-center rounded-full py-1 px-2 w-full bg-white',
      styles,
    ]"
  >
    <svg
      stroke="currentColor"
      fill="#121827"
      stroke-width="0"
      viewBox="0 0 512 512"
      height="30px"
      width="30px"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path
        d="M505 442.7L405.3 343c-4.5-4.5-10.6-7-17-7H372c27.6-35.3 44-79.7 44-128C416 93.1 322.9 0 208 0S0 93.1 0 208s93.1 208 208 208c48.3 0 92.7-16.4 128-44v16.3c0 6.4 2.5 12.5 7 17l99.7 99.7c9.4 9.4 24.6 9.4 33.9 0l28.3-28.3c9.4-9.4 9.4-24.6.1-34zM208 336c-70.7 0-128-57.2-128-128 0-70.7 57.2-128 128-128 70.7 0 128 57.2 128 128 0 70.7-57.2 128-128 128z"
      ></path>
    </svg>
    <input
      class="rounded-l-full w-full py-2 px-4 text-gray-700 leading-tight focus:outline-none"
      id="search"
      type="text"
      placeholder="Busca algo..."
    />
  </div>
</template>
