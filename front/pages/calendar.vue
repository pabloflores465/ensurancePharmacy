<script setup>
const currentWeekStart = ref(getMonday(new Date()));

const horasDelDia = Array.from({ length: 24 }, (_, i) => i);

function getMonday(date) {
  const d = new Date(date);
  const day = d.getDay(); // domingo=0, lunes=1, etc.
  const diff = d.getDate() - day + (day === 0 ? -6 : 1);
  d.setDate(diff);
  d.setHours(0, 0, 0, 0);
  return d;
}

const diasSemana = computed(() => {
  const dias = [];
  for (let i = 0; i < 7; i++) {
    const d = new Date(currentWeekStart.value);
    d.setDate(d.getDate() + i);
    dias.push(d);
  }
  return dias;
});

const rangoFechas = computed(() => {
  if (!diasSemana.value.length) return "";
  const inicio = diasSemana.value[0];
  const fin = diasSemana.value[6];
  return (
    `${inicio.getDate()}/${inicio.getMonth() + 1}/${inicio.getFullYear()} - ` +
    `${fin.getDate()}/${fin.getMonth() + 1}/${fin.getFullYear()}`
  );
});

function formatearFecha(date) {
  const dia = date.getDate();
  const mes = date.toLocaleString("es-ES", { month: "short" });
  return `${dia} ${mes}`;
}

function nombreCortoDia(date) {
  return date
    .toLocaleDateString("es-ES", { weekday: "short" })
    .replace(".", "")
    .toLowerCase();
}

function prevWeek() {
  currentWeekStart.value.setDate(currentWeekStart.value.getDate() - 7);
  currentWeekStart.value = new Date(currentWeekStart.value);
}

function nextWeek() {
  currentWeekStart.value.setDate(currentWeekStart.value.getDate() + 7);
  currentWeekStart.value = new Date(currentWeekStart.value);
}
</script>

<template>
  <div
    class="bg-image-[url('https://cdn.pixabay.com/photo/2023/11/06/09/52/mountain-8369262_1280.jpg')] mx-auto flex w-full flex-col p-4"
  >
    <div class="mb-4 flex items-center justify-between">
      <button class="btn flex" @click="prevWeek">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path d="M400-80 0-480l400-400 71 71-329 329 329 329-71 71Z" />
        </svg>
      </button>

      <div class="text-xl font-semibold">
        {{ rangoFechas }}
      </div>

      <button class="btn flex" @click="nextWeek">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path d="m321-80-71-71 329-329-329-329 71-71 400 400L321-80Z" />
        </svg>
      </button>
    </div>

    <div
      class="border-primary overflow-auto rounded-3xl border-2 backdrop-blur-md"
    >
      <table class="w-full table-fixed border-collapse">
        <thead>
          <tr class="bg-background text-primary">
            <th class="w-16"></th>
            <th
              v-for="(dia, index) in diasSemana"
              :key="index"
              class="border-primary border-b text-center"
            >
              <div class="font-semibold">{{ nombreCortoDia(dia) }}</div>
              <div>{{ formatearFecha(dia) }}</div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(hora, idx) in horasDelDia"
            :key="idx"
            class="border-primary border-t"
          >
            <td
              class="border-primary bg-background hover:bg-h-background text-primary border-top border-e p-2 text-center font-semibold"
            >
              {{ hora }}:00
            </td>

            <td
              v-for="(dia, dayIndex) in diasSemana"
              :key="dayIndex"
              class="bg-background/80 border-primary hover:bg-h-background/80 border-top h-16 border-e"
            >
              <button></button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
