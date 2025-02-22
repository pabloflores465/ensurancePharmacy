<script setup lang="ts">
interface Service {
  name: string;
  hospital: string;
  description: string;
  categories: string[];
  subcategories: string[];
  price: number;
}

const services = ref<Service[]>([
  {
    name: "Consulta General",
    hospital: "Hospital Central",
    description: "Revisión médica completa para adultos.",
    categories: ["Medicina", "Terapia", "Medicina", "General", "Consulta"],
    subcategories: ["General"],
    price: 50,
  },
  {
    name: "Radiología",
    hospital: "Clínica Radiológica",
    description: "Imágenes diagnósticas avanzadas.",
    categories: ["Diagnóstico"],
    subcategories: ["Radiología"],
    price: 100,
  },
  {
    name: "Fisioterapia",
    hospital: "Centro de Rehabilitación",
    description: "Tratamientos para mejorar la movilidad y aliviar el dolor.",
    categories: ["Terapia"],
    subcategories: ["Fisioterapia"],
    price: 70,
  },
  {
    name: "Cirugía General",
    hospital: "Hospital Central",
    description: "Procedimientos quirúrgicos generales y de urgencia.",
    categories: ["Cirugía"],
    subcategories: ["General"],
    price: 500,
  },
  {
    name: "Odontología",
    hospital: "Clínica Dental",
    description: "Cuidado dental integral, revisiones y limpiezas.",
    categories: ["Salud Oral"],
    subcategories: ["Revisión", "Limpieza"],
    price: 80,
  },
  {
    name: "Cardiología",
    hospital: "Instituto Cardiovascular",
    description: "Atención y diagnóstico del corazón.",
    categories: ["Medicina"],
    subcategories: ["Cardiología"],
    price: 120,
  },
  {
    name: "Dermatología",
    hospital: "Clínica de la Piel",
    description: "Tratamientos especializados para la piel.",
    categories: ["Medicina"],
    subcategories: ["Dermatología"],
    price: 90,
  },
  {
    name: "Pediatría",
    hospital: "Hospital Infantil",
    description: "Atención médica especializada para niños.",
    categories: ["Medicina"],
    subcategories: ["Pediatría"],
    price: 60,
  },
  {
    name: "Neurología",
    hospital: "Centro Neurológico",
    description: "Diagnóstico y tratamiento de trastornos neurológicos.",
    categories: ["Medicina"],
    subcategories: ["Neurología"],
    price: 130,
  },
  {
    name: "Ginecología",
    hospital: "Centro de Salud Femenina",
    description: "Atención integral para la salud de la mujer.",
    categories: ["Medicina"],
    subcategories: ["Ginecología"],
    price: 110,
  },
  {
    name: "Oftalmología",
    hospital: "Clínica Ocular",
    description: "Exámenes y tratamientos para el cuidado de la vista.",
    categories: ["Salud"],
    subcategories: ["Oftalmología"],
    price: 95,
  },
  {
    name: "Ortopedia",
    hospital: "Instituto Ortopédico",
    description: "Tratamientos para huesos y articulaciones.",
    categories: ["Terapia"],
    subcategories: ["Ortopedia"],
    price: 150,
  },
  {
    name: "Psiquiatría",
    hospital: "Centro de Salud Mental",
    description: "Apoyo y tratamiento en salud mental.",
    categories: ["Salud Mental"],
    subcategories: ["Psiquiatría"],
    price: 140,
  },
  {
    name: "Nutrición",
    hospital: "Centro Nutricional",
    description: "Planes de alimentación y asesoramiento nutricional.",
    categories: ["Bienestar"],
    subcategories: ["Nutrición"],
    price: 65,
  },
  {
    name: "Laboratorio Clínico",
    hospital: "Laboratorio Central",
    description: "Análisis clínicos precisos y rápidos.",
    categories: ["Diagnóstico"],
    subcategories: ["Laboratorio"],
    price: 75,
  },
]);

const edit = useEdit();
</script>

<template>
  <div
    class="responsive-grid bg-image-[url(https://images.unsplash.com/photo-1542744173-8e7e53415bb0?q=80&w=3270&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D)] py-8"
  >
    <div v-if="!edit" v-for="service in services" class="card">
      <h2 class="title mb-6">{{ service.name }}</h2>
      <div class="text-primary mb-4 flex">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M420-280h120v-140h140v-120H540v-140H420v140H280v120h140v140ZM200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H200Zm0-80h560v-560H200v560Zm0-560v560-560Z"
          />
        </svg>
        <p class="me-2 font-semibold">Hospital:</p>
        {{ service.hospital }}
      </div>
      <div class="text-error mb-4 flex">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M560-440q-50 0-85-35t-35-85q0-50 35-85t85-35q50 0 85 35t35 85q0 50-35 85t-85 35ZM280-320q-33 0-56.5-23.5T200-400v-320q0-33 23.5-56.5T280-800h560q33 0 56.5 23.5T920-720v320q0 33-23.5 56.5T840-320H280Zm80-80h400q0-33 23.5-56.5T840-480v-160q-33 0-56.5-23.5T760-720H360q0 33-23.5 56.5T280-640v160q33 0 56.5 23.5T360-400Zm440 240H120q-33 0-56.5-23.5T40-240v-440h80v440h680v80ZM280-400v-320 320Z"
          />
        </svg>
        <p class="me-2 font-semibold">Price:</p>
        {{ service.price }}
      </div>
      <div class="grid grid-flow-row grid-cols-2">
        <div
          v-for="category in service.categories"
          class="text-background bg-accent mx-2 mb-4 rounded-md px-2 py-1 text-sm"
        >
          {{ category }}
        </div>
      </div>
      <p class="text-primary mb-2 font-semibold">Description</p>
      <textarea class="text-secondary flex w-full">{{
        service.description
      }}</textarea>
    </div>

    <div v-if="edit" v-for="service in services" class="card">
      <span class="text-primary font-semibold">{{ service.name }}</span>
      <input type="text" class="field mb-8" />
      <span class="text-primary font-semibold">{{ service.hospital }}</span>
      <input type="text" class="field mb-8" />
      <span class="text-primary font-semibold">{{ service.price }}</span>
      <input type="text" class="field mb-8" />
      <Dropdown class="me-2 mb-6" />
      <span class="text-primary font-semibold">{{ service.description }}</span>
      <textarea type="text" class="field mb-8" />
    </div>
  </div>
</template>

<style scoped></style>
