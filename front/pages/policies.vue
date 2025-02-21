<script setup lang="ts">
const search = useSearch();
const policies: Ref<
  {
    name: string;
    code: string;
    percentage: string;
    description: string;
    creationDate: string;
    expireDate: string;
    price: number;
  }[]
> = ref([
  {
    name: "Poliza 1",
    code: "3A4A",
    percentage: "80%",
    description:
      "Esta póliza de seguro ha sido diseñada para brindarte una protección integral ante cualquier imprevisto.",
    creationDate: "01-01-2024",
    expireDate: "01-01-2025",
    price: 2300.66,
  },
  {
    name: "Poliza 2",
    code: "4B5B",
    percentage: "75%",
    description:
      "Cobertura adaptada a tus necesidades con respaldo financiero y beneficios exclusivos.",
    creationDate: "05-01-2024",
    expireDate: "05-01-2025",
    price: 2100.5,
  },
  {
    name: "Poliza 3",
    code: "5C6C",
    percentage: "85%",
    description:
      "Ofrece una protección amplia y personalizada, garantizando tranquilidad en momentos críticos.",
    creationDate: "10-01-2024",
    expireDate: "10-01-2025",
    price: 2500.0,
  },
  {
    name: "Poliza 4",
    code: "6D7D",
    percentage: "70%",
    description:
      "Diseñada para adaptarse a diferentes necesidades y ofrecer una excelente relación calidad-precio.",
    creationDate: "15-01-2024",
    expireDate: "15-01-2025",
    price: 1950.75,
  },
  {
    name: "Poliza 5",
    code: "7E8E",
    percentage: "90%",
    description:
      "Con beneficios exclusivos y una cobertura integral que respalda tu tranquilidad financiera.",
    creationDate: "20-01-2024",
    expireDate: "20-01-2025",
    price: 2700.2,
  },
  {
    name: "Poliza 6",
    code: "8F9F",
    percentage: "65%",
    description:
      "Ideal para quienes buscan un equilibrio entre precio y protección en situaciones imprevistas.",
    creationDate: "25-01-2024",
    expireDate: "25-01-2025",
    price: 1850.4,
  },
  {
    name: "Poliza 7",
    code: "9G0G",
    percentage: "80%",
    description:
      "Póliza con cobertura completa y descuentos atractivos para una mejor protección.",
    creationDate: "30-01-2024",
    expireDate: "30-01-2025",
    price: 2300.66,
  },
  {
    name: "Poliza 8",
    code: "0H1H",
    percentage: "78%",
    description:
      "Ofrece respaldo financiero y asistencia inmediata en caso de imprevistos.",
    creationDate: "05-02-2024",
    expireDate: "05-02-2025",
    price: 2200.0,
  },
  {
    name: "Poliza 9",
    code: "1I2I",
    percentage: "82%",
    description:
      "Diseñada para brindar seguridad y confianza, con una amplia gama de beneficios.",
    creationDate: "10-02-2024",
    expireDate: "10-02-2025",
    price: 2400.55,
  },
  {
    name: "Poliza 10",
    code: "2J3J",
    percentage: "77%",
    description:
      "Cobertura integral con descuentos significativos, ideal para toda la familia.",
    creationDate: "15-02-2024",
    expireDate: "15-02-2025",
    price: 2250.8,
  },
  {
    name: "Poliza 11",
    code: "3K4K",
    percentage: "83%",
    description:
      "Garantiza respaldo financiero y una respuesta rápida ante cualquier situación.",
    creationDate: "20-02-2024",
    expireDate: "20-02-2025",
    price: 2350.3,
  },
  {
    name: "Poliza 12",
    code: "4L5L",
    percentage: "79%",
    description:
      "Una opción equilibrada que combina protección, beneficios y un precio competitivo.",
    creationDate: "25-02-2024",
    expireDate: "25-02-2025",
    price: 2280.9,
  },
  {
    name: "Poliza 13",
    code: "5M6M",
    percentage: "81%",
    description:
      "Diseñada para ofrecerte tranquilidad con una amplia cobertura y soporte permanente.",
    creationDate: "01-03-2024",
    expireDate: "01-03-2025",
    price: 2400.0,
  },
  {
    name: "Poliza 14",
    code: "6N7N",
    percentage: "76%",
    description:
      "Con una excelente relación costo-beneficio, brindándote protección ante cualquier eventualidad.",
    creationDate: "05-03-2024",
    expireDate: "05-03-2025",
    price: 2200.75,
  },
  {
    name: "Poliza 15",
    code: "7O8O",
    percentage: "84%",
    description:
      "Póliza integral que garantiza asistencia inmediata y respaldo financiero en situaciones críticas.",
    creationDate: "10-03-2024",
    expireDate: "10-03-2025",
    price: 2450.5,
  },
]);
const edit = useEdit();
</script>

<template>
  <main
    class="bg-image-[url(https://cdn.pixabay.com/photo/2020/11/03/15/32/man-5710164_1280.jpg)]"
  >
    <Search v-if="search" />
    <div class="responsive-grid">
      <div v-if="!edit" class="card m-4" v-for="policy in policies">
        <div class="title mb-6">
          {{ policy.name }}
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
          {{ policy.code }}
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
          {{ policy.percentage }}
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
          Q.{{ policy.price }}
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
          {{ policy.creationDate }}
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
          {{ policy.expireDate }}
        </div>
        <div class="text-secondary mb-4 text-justify text-sm">
          {{ policy.description }}
        </div>
      </div>
      <div v-if="edit" class="card m-4" v-for="policy in policies">
        <span class="text-primary font-semibold">Policy</span>
        <input type="text" class="field mb-8" />
        <span class="text-primary font-semibold">Policy Code</span>
        <input type="text" class="field mb-8" />
        <span class="text-primary font-semibold">Discount Percentage</span>
        <input type="number" class="field mb-8" />
        <span class="text-primary font-semibold">Anual Price</span>
        <input type="number" class="field mb-8" />
        <span class="text-primary font-semibold">Creation Date</span>
        <input type="date" class="field mb-8" />
        <span class="text-primary font-semibold">Expire Date</span>
        <input type="date" class="field mb-8" />
        <span class="text-primary font-semibold">Description</span>
        <input type="text" class="field mb-8" />
        <button class="btn mx-auto flex justify-center">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="currentColor"
          >
            <path
              d="M840-680v480q0 33-23.5 56.5T760-120H200q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h480l160 160Zm-80 34L646-760H200v560h560v-446ZM480-240q50 0 85-35t35-85q0-50-35-85t-85-35q-50 0-85 35t-35 85q0 50 35 85t85 35ZM240-560h360v-160H240v160Zm-40-86v446-560 114Z"
            /></svg
          >Save
        </button>
      </div>
    </div>
  </main>
</template>
