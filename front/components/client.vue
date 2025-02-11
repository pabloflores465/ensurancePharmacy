<script setup lang="ts">
withDefaults(
  defineProps<{
    name?: string;
    cui?: number;
    phone?: string;
    email?: string;
    address?: string;
    birthdate?: string;
    rol?: string;
  }>(),
  {
    name: "John Doe",
    cui: 0,
    phone: "59122620",
    email: "pablopolis2016@gmail.com",
    address: "",
    birthdate: "1-1-2021",

  }
);
</script>

<template>
  <div>
    <div>{}</div>
  </div>
</template>
