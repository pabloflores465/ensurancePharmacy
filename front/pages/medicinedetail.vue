<template>
    <div class="w-full p-6 bg-white">
      <!-- Sección de imágenes -->
      <div class="flex flex-col md:flex-row gap-6">
        <div class="md:w-1/2">
          <!-- Imagen principal -->
          <img
            :src="producto.fotos[0]"
            alt="Imagen principal del producto"
            class="w-full h-auto rounded-md shadow-md"
          />
          <!-- Galería de imágenes -->
          <div class="grid grid-cols-3 gap-2 mt-4">
            <img
              v-for="(foto, index) in producto.fotos"
              :key="index"
              :src="foto"
              alt="Foto del producto"
              class="w-full h-auto rounded-md object-cover cursor-pointer hover:opacity-80 transition-opacity"
            />
          </div>
        </div>
  
        <!-- Detalles del producto -->
        <div class="md:w-1/2">
          <h1 class="text-2xl font-bold mb-2">{{ producto.descripcion }}</h1>
          <p class="text-gray-700 mb-4">{{ producto.principioActivo }}</p>
          
          <div class="space-y-3">
            <div>
              <span class="font-semibold">Código: </span>
              <span>{{ producto.codigo }}</span>
            </div>
            <div>
              <span class="font-semibold">Categorías: </span>
              <span v-for="(categoria, index) in producto.categorias" :key="index">
                <span
                  class="bg-blue-100 text-blue-800 text-xs font-medium mr-1 px-2.5 py-0.5 rounded"
                >
                  {{ categoria }}
                </span>
              </span>
            </div>
            <div>
              <span class="font-semibold">Concentración: </span>
              <span>{{ producto.concentracion }}</span>
            </div>
            <div>
              <span class="font-semibold">Presentación: </span>
              <span>{{ producto.presentacion }}</span>
            </div>
            <div>
              <span class="font-semibold">Número de Unidades: </span>
              <span>{{ producto.numeroUnidades }}</span>
            </div>
            <div>
              <span class="font-semibold">Marca: </span>
              <span>{{ producto.marca }}</span>
            </div>
            <div>
              <span class="font-semibold">Requiere Receta: </span>
              <span>
                {{ producto.requiereReceta ? "Sí" : "No" }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const producto = ref({
    codigo: "MED12345",
    categorias: ["Analgésico", "Antiinflamatorio"],
    principioActivo: "Ibuprofeno",
    descripcion: "Ibuprofeno es un medicamento antiinflamatorio no esteroideo que se utiliza para aliviar el dolor, la inflamación y la fiebre.",
    fotos: [
      "https://via.placeholder.com/400x300?text=Imagen+Principal",
      "https://via.placeholder.com/400x300?text=Imagen+2",
      "https://via.placeholder.com/400x300?text=Imagen+3"
    ],
    concentracion: "200 mg",
    presentacion: "Tabletas",
    numeroUnidades: 30,
    marca: "Pharma Inc.",
    requiereReceta: false
  });
  </script>
  
  <!-- No es necesario un bloque de estilos ya que Tailwind CSS se encarga del diseño -->