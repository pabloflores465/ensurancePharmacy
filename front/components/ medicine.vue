<template>
  <NuxtLink to="/medicinedetail">
    <div
      class="bg-[var(--primary-color)]/80 backdrop-blur-md rounded-lg shadow-xl text-white w-[320px] m-4 overflow-hidden transition-transform duration-300 hover:scale-105"
    >
      <img
        src="../public/paracetamol.jpg"
        alt="Paracetamol"
        class="w-full h-48 object-cover aspect-square"
      />
      <div class="py-2 px-4">
        <p class="text-lg font-bold mb-4 text-[var(--accent-color)] hover:text-[var(--hover-color)] line-clamp-2">Paracetamolaaa</p>
        <p class="text-sm mb-1 text-zinc-300">
          <span class="font-semibold">Código:</span> 123
        </p>
        <p class="text-sm mb-3 text-zinc-300">
          <span class="font-semibold">Principio Activo:</span> Licomisina
        </p>
      </div>
    </div>
  </NuxtLink>
</template>
