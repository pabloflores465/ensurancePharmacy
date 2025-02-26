<script setup>
import Modal from '~/components/modal.vue';

const currentWeekStart = ref(getMonday(new Date()));

const horasDelDia = Array.from({ length: 24 }, (_, i) => i);

// Estado para controlar el modal
const showModal = ref(false);
const selectedDate = ref(null);
const selectedHour = ref(null);

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

// Función para abrir el modal - modificada con depuración
function openAppointmentModal(date, hour) {
  console.log('Abriendo modal para:', date, hour);
  selectedDate.value = new Date(date);
  selectedHour.value = hour;
  showModal.value = true;
  console.log('Estado del modal:', showModal.value);
}

// Función para cerrar el modal
function closeModal() {
  showModal.value = false;
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
              class="bg-background/80 border-primary hover:bg-h-background/80 border-top h-16 border-e p-0"
            >
              <button 
                type="button"
                @click="openAppointmentModal(dia, hora)"
                class="w-full h-full p-2 text-left hover:bg-primary/10 transition-colors duration-200 flex items-start justify-start"
              >
                <span class="text-xs">{{ hora }}:00 - Disponible</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal con estructura simplificada -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center">
      <div class="fixed inset-0 bg-black/50" @click="closeModal"></div>
      <div class="relative bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
        <div class="p-4 border-b">
          <h2 class="text-xl font-semibold">
            Configurar cita: {{ selectedDate ? `${selectedDate.toLocaleDateString('es-ES')} a las ${selectedHour}:00` : '' }}
          </h2>
        </div>
        <div class="p-4">
          <div class="mb-4">
            <label class="block text-sm font-medium mb-1" for="nombrePaciente">Nombre del paciente</label>
            <input id="nombrePaciente" type="text" class="w-full p-2 border border-primary rounded-md">
          </div>
          <div class="mb-4">
            <label class="block text-sm font-medium mb-1" for="duracion">Duración (minutos)</label>
            <select id="duracion" class="w-full p-2 border border-primary rounded-md">
              <option value="15">15 minutos</option>
              <option value="30" selected>30 minutos</option>
              <option value="45">45 minutos</option>
              <option value="60">1 hora</option>
            </select>
          </div>
          <div class="mb-4">
            <label class="block text-sm font-medium mb-1" for="notas">Notas</label>
            <textarea id="notas" class="w-full p-2 border border-primary rounded-md" rows="3"></textarea>
          </div>
        </div>
        <div class="p-4 border-t flex justify-end space-x-2">
          <button class="btn btn-outline" @click="closeModal">Cancelar</button>
          <button class="btn btn-primary">Guardar cita</button>
        </div>
      </div>
    </div>
  </div>
</template>
