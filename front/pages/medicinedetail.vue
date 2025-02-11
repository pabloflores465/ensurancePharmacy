<script setup>
import { ref } from "vue";

const producto = ref({
  title: "Paracetamol",
  codigo: "MED12345",
  categorias: [
    "Analgésico",
    "Antiinflamatorio",
    "Analgésico",
    "Antiinflamatorio",
    "Analgésico",
    "Antiinflamatorio",
    "Analgésico",
    "Antiinflamatorio",
  ],
  principioActivo: "Ibuprofeno",
  descripcion:
    "Ibuprofeno es un medicamento antiinflamatorio no esteroideo que se utiliza para aliviar el dolor, la inflamación y la fiebre.",
  fotos: [
    "https://via.placeholder.com/400x300?text=Imagen+Principal",
    "https://via.placeholder.com/400x300?text=Imagen+2",
    "https://via.placeholder.com/400x300?text=Imagen+3",
  ],
  concentracion: "200 mg",
  presentacion: "Tabletas",
  numeroUnidades: 30,
  marca: "Pharma Inc.",
  requiereReceta: false,
});
</script>
<template>
  <div class="w-full bg-white bg-[url('/carCrash.jpg')] bg-cover bg-center p-6">
    <div class="flex flex-col gap-6 md:flex-row">
      <div class="md:w-1/2">
        <img
          :src="producto.fotos[0]"
          alt="Imagen principal del producto"
          class="h-auto w-full rounded-md shadow-md"
        />
        <div class="mt-4 grid grid-cols-3 gap-2">
          <img
            v-for="(foto, index) in producto.fotos"
            :key="index"
            :src="foto"
            alt="Foto del producto"
            class="h-auto w-full cursor-pointer rounded-md object-cover transition-opacity hover:opacity-80"
          />
        </div>
      </div>
      <div
        class="rounded-lg bg-[var(--primary-color)]/80 px-6 py-4 backdrop-blur-md md:w-1/2"
      >
        <h1
          class="mb-6 line-clamp-2 text-2xl font-bold text-[var(--accent-color)]"
        >
          {{ producto.title }}
        </h1>
        <p class="mb-6 text-justify text-lg font-bold text-white">
          {{ producto.descripcion }}
        </p>
        <p class="mb-2 text-gray-300">
          <strong class="pe-2">Active Principle: </strong
          >{{ producto.principioActivo }}
        </p>
        <p class="mb-2 text-gray-300">
          <strong class="pe-2">Code: </strong>{{ producto.codigo }}
        </p>
        <p class="mb-2 text-gray-300">
          <strong class="pe-2">Presentation: </strong
          >{{ producto.presentacion }}
        </p>
        <p class="mb-2 text-gray-300">
          <strong class="pe-2">Units: </strong>{{ producto.numeroUnidades }}
        </p>
        <p class="mb-2 text-gray-300">
          <strong class="pe-2">Recipe Required: </strong
          >{{ !producto.requiereReceta ? "No" : "Yes" }}
        </p>
        <p class="mb-6 text-gray-300">
          <strong class="pe-2">Brand: </strong>{{ producto.marca }}
        </p>
        <div class="grid grid-cols-2 gap-2 md:grid-cols-3">
          <span
            v-for="(categoria, index) in producto.categorias"
            :key="index"
            class="rounded bg-[var(--accent-color)] px-2.5 py-1 text-sm break-words font-medium text-[var(--primary-color)]"
          >
            {{ categoria }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
