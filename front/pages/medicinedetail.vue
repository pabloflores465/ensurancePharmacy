<script setup lang="ts">
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
const dark = darkMode();
</script>
<template>
  <main :class="[`bg-image-[url('/carCrash.jpg')]`, dark ? 'dark' : '']">
    <section class="flex flex-col gap-6 md:flex-row">
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
      <section class="card pt-4 md:w-1/2">
        <h1 class="title mb-6 justify-items-start">
          {{ producto.title }}
        </h1>
        <p class="text-primary mb-6 text-justify text-lg font-bold">
          {{ producto.descripcion }}
        </p>
        <p class="text-secondary mb-2">
          <strong class="pe-2">Active Principle: </strong
          >{{ producto.principioActivo }}
        </p>
        <p class="text-secondary mb-2">
          <strong class="pe-2">Code: </strong>{{ producto.codigo }}
        </p>
        <p class="text-secondary mb-2">
          <strong class="pe-2">Presentation: </strong
          >{{ producto.presentacion }}
        </p>
        <p class="text-secondary mb-2">
          <strong class="pe-2">Units: </strong>{{ producto.numeroUnidades }}
        </p>
        <p class="text-secondary mb-2">
          <strong class="pe-2">Recipe Required: </strong
          >{{ !producto.requiereReceta ? "No" : "Yes" }}
        </p>
        <p class="text-secondary mb-6">
          <strong class="pe-2">Brand: </strong>{{ producto.marca }}
        </p>
        <div class="grid grid-cols-2 gap-2 md:grid-cols-3">
          <span
            v-for="(categoria, index) in producto.categorias"
            :key="index"
            class="bg-accent hover:bg-h-accent rounded px-2.5 py-1 text-sm font-medium break-words text-[var(--primary-color)]"
          >
            {{ categoria }}
          </span>
        </div>
      </section>
    </section>
  </main>
</template>
