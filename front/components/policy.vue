<script setup lang="ts">
withDefaults(
  defineProps<{
    name?: string;
    code?: string;
    percentage?: string;
    description?: string;
    creationDate?: string;
    expireDate?: string;
    price?: number;
  }>(),
  {
    name: "Poliza 1",
    code: "3A4A",
    percentage: "80%",
    description:
      "Esta póliza de seguro ha sido diseñada para brindarte una protección integral ante cualquier imprevisto. Con una cobertura adaptable a tus necesidades, garantiza respaldo financiero y tranquilidad en momentos críticos. Disfruta de beneficios exclusivos y de un atractivo descuento del 80%, lo que la convierte en una opción eficiente y económica. Con vigencia desde el 1 de enero de 2024 hasta el 1 de enero de 2025, esta póliza es la elección ideal para quienes buscan seguridad, respaldo y confianza en cada paso de su vida.",
    creationDate: "01-01-2024",
    expireDate: "01-01-2025",
    price: 2300.66,
  },
);
</script>

<template>
  <section
    class="card m-4 w-[50%] transform px-6 py-4 transition-transform duration-300 hover:scale-105 max-sm:w-full"
  >
    <div class="title mb-6">
      {{ name }}
    </div>

    <div class="text-md text-accent hover:text-h-accent mb-2 flex">
      <div class="flex pe-2 font-semibold">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M40-120v-200h80v120h120v80H40Zm680 0v-80h120v-120h80v200H720ZM160-240v-480h80v480h-80Zm120 0v-480h40v480h-40Zm120 0v-480h80v480h-80Zm120 0v-480h120v480H520Zm160 0v-480h40v480h-40Zm80 0v-480h40v480h-40ZM40-640v-200h200v80H120v120H40Zm800 0v-120H720v-80h200v200h-80Z"
          />
        </svg>
        Policy Code:
      </div>
      {{ code }}
    </div>
    <div class="text-md text-success hover:text-h-success mb-2 flex">
      <div class="flex pe-2 font-semibold">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M300-520q-58 0-99-41t-41-99q0-58 41-99t99-41q58 0 99 41t41 99q0 58-41 99t-99 41Zm0-80q25 0 42.5-17.5T360-660q0-25-17.5-42.5T300-720q-25 0-42.5 17.5T240-660q0 25 17.5 42.5T300-600Zm360 440q-58 0-99-41t-41-99q0-58 41-99t99-41q58 0 99 41t41 99q0 58-41 99t-99 41Zm0-80q25 0 42.5-17.5T720-300q0-25-17.5-42.5T660-360q-25 0-42.5 17.5T600-300q0 25 17.5 42.5T660-240Zm-444 80-56-56 584-584 56 56-584 584Z"
          />
        </svg>
        Discount Percentage:
      </div>
      {{ percentage }}
    </div>
    <div class="text-md text-error hover:text-h-error mb-6 flex">
      <div class="flex pe-2 font-semibold">
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
        Anual Price:
      </div>
      Q.{{ price }}
    </div>
    <div class="text-md text-primary hover:text-h-primary mb-2 flex">
      <div class="flex pe-2 font-semibold">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M360-300q-42 0-71-29t-29-71q0-42 29-71t71-29q42 0 71 29t29 71q0 42-29 71t-71 29ZM200-80q-33 0-56.5-23.5T120-160v-560q0-33 23.5-56.5T200-800h40v-80h80v80h320v-80h80v80h40q33 0 56.5 23.5T840-720v560q0 33-23.5 56.5T760-80H200Zm0-80h560v-400H200v400Zm0-480h560v-80H200v80Zm0 0v-80 80Z"
          />
        </svg>
        Creation Date:
      </div>
      {{ creationDate }}
    </div>
    <div class="text-md text-primary hover:text-h-primary mb-6 flex">
      <div class="flex pe-2 font-semibold">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M580-240q-42 0-71-29t-29-71q0-42 29-71t71-29q42 0 71 29t29 71q0 42-29 71t-71 29ZM200-80q-33 0-56.5-23.5T120-160v-560q0-33 23.5-56.5T200-800h40v-80h80v80h320v-80h80v80h40q33 0 56.5 23.5T840-720v560q0 33-23.5 56.5T760-80H200Zm0-80h560v-400H200v400Zm0-480h560v-80H200v80Zm0 0v-80 80Z"
          />
        </svg>
        Expire Date:
      </div>
      {{ expireDate }}
    </div>
    <div class="text-secondary mb-4 text-justify text-sm">
      {{ description }}
    </div>
  </section>
</template>
