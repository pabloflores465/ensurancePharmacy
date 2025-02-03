<script setup lang="ts"></script>

<template>
  <NuxtLink to="/medicinedetail">
    <div
      class="bg-[#1e293b] text-white w-[320px] m-4 rounded-lg overflow-hidden shadow-lg transition-transform duration-300 hover:scale-105"
    >
      <img
        src="../public/paracetamol.jpg"
        alt="Paracetamol"
        class="w-full h-48 object-cover"
      />
      <div class="p-4">
        <h2 class="text-2xl font-bold mb-2">Paracetamol</h2>
        <p class="text-sm mb-1">
          <span class="font-semibold">Código:</span> 123
        </p>
        <p class="text-sm mb-3">
          <span class="font-semibold">Principio Activo:</span> Licomisina
        </p>
        <Dropdown />
      </div>
    </div>
  </NuxtLink>
</template>
