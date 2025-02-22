<script setup lang="ts">
const medicines: Ref<
  {
    name: string;
    code: string;
    activePrinciple: string;
    presentation: string;
    units: number;
    recipeRequired: boolean;
    brand: string;
    categories: string[];
  }[]
> = ref([
  {
    name: "Paracetamol",
    code: "PARA001",
    activePrinciple: "Paracetamol",
    presentation: "500mg Tablets",
    units: 20,
    recipeRequired: false,
    brand: "Tylenol",
    categories: ["Analgésico", "Antipirético"],
  },
  {
    name: "Ibuprofen",
    code: "IBU002",
    activePrinciple: "Ibuprofen",
    presentation: "200mg Tablets",
    units: 30,
    recipeRequired: false,
    brand: "Advil",
    categories: [
      "AINE",
      "Antiinflamatorio",
      "Analgésico",
      "Analgésico",
      "Analgésico",
      "Analgésico",
    ],
  },
  {
    name: "Amoxicillin",
    code: "AMOX003",
    activePrinciple: "Amoxicillin",
    presentation: "250mg Capsules",
    units: 15,
    recipeRequired: true,
    brand: "Amoxil",
    categories: ["Antibiótico"],
  },
  {
    name: "Omeprazole",
    code: "OMEP004",
    activePrinciple: "Omeprazole",
    presentation: "20mg Capsules",
    units: 14,
    recipeRequired: true,
    brand: "Losec",
    categories: ["Inhibidor de la bomba de protones", "Gastrointestinal"],
  },
  {
    name: "Cetirizine",
    code: "CETI005",
    activePrinciple: "Cetirizine Hydrochloride",
    presentation: "10mg Tablets",
    units: 10,
    recipeRequired: false,
    brand: "Zyrtec",
    categories: ["Antihistamínico", "Alergias"],
  },
  {
    name: "Metformin",
    code: "META006",
    activePrinciple: "Metformin Hydrochloride",
    presentation: "500mg Tablets",
    units: 60,
    recipeRequired: true,
    brand: "Glucophage",
    categories: ["Antidiabético", "Biguanida"],
  },
  {
    name: "Amlodipine",
    code: "AMLO007",
    activePrinciple: "Amlodipine Besylate",
    presentation: "5mg Tablets",
    units: 28,
    recipeRequired: true,
    brand: "Norvasc",
    categories: ["Antihipertensivo", "Bloqueador de los canales de calcio"],
  },
  {
    name: "Lisinopril",
    code: "LISI008",
    activePrinciple: "Lisinopril",
    presentation: "10mg Tablets",
    units: 30,
    recipeRequired: true,
    brand: "Zestril",
    categories: ["Antihipertensivo", "Inhibidor de la ECA"],
  },
  {
    name: "Simvastatin",
    code: "SIMV009",
    activePrinciple: "Simvastatin",
    presentation: "20mg Tablets",
    units: 30,
    recipeRequired: true,
    brand: "Zocor",
    categories: ["Reductor de lípidos", "Estatina"],
  },
  {
    name: "Azithromycin",
    code: "AZIT010",
    activePrinciple: "Azithromycin",
    presentation: "250mg Tablets",
    units: 6,
    recipeRequired: true,
    brand: "Zithromax",
    categories: ["Antibiótico", "Macrólido"],
  },
  {
    name: "Albuterol",
    code: "ALBU011",
    activePrinciple: "Albuterol Sulfate",
    presentation: "90mcg Inhaler",
    units: 1,
    recipeRequired: true,
    brand: "Ventolin",
    categories: ["Broncodilatador", "Respiratorio"],
  },
  {
    name: "Prednisone",
    code: "PRED012",
    activePrinciple: "Prednisone",
    presentation: "5mg Tablets",
    units: 20,
    recipeRequired: true,
    brand: "Deltasone",
    categories: ["Corticosteroide", "Antiinflamatorio"],
  },
  {
    name: "Levothyroxine",
    code: "LEVO013",
    activePrinciple: "Levothyroxine Sodium",
    presentation: "50mcg Tablets",
    units: 90,
    recipeRequired: true,
    brand: "Synthroid",
    categories: ["Hormona tiroidea"],
  },
  {
    name: "Warfarin",
    code: "WARF014",
    activePrinciple: "Warfarin Sodium",
    presentation: "5mg Tablets",
    units: 28,
    recipeRequired: true,
    brand: "Coumadin",
    categories: ["Anticoagulante", "Antitrombótico"],
  },
  {
    name: "Dexamethasone",
    code: "DEXA015",
    activePrinciple: "Dexamethasone",
    presentation: "4mg Tablets",
    units: 30,
    recipeRequired: true,
    brand: "Decadron",
    categories: ["Corticosteroide", "Antiinflamatorio"],
  },
]);
const edit = useEdit();
</script>

<template>
  <div
    class="bg-image-[url(https://cdn.pixabay.com/photo/2020/10/02/09/01/tablets-5620566_1280.jpg)]"
  >
    <div class="responsive-grid">
      <div v-if="!edit" v-for="medicine in medicines" class="card">
        <p class="title mb-6">{{ medicine.name }}</p>
        <div class="label text-md mb-4 flex">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="currentColor"
          >
            <path
              d="M520-120v-80h80v80h-80Zm-80-80v-200h80v200h-80Zm320-120v-160h80v160h-80Zm-80-160v-80h80v80h-80Zm-480 80v-80h80v80h-80Zm-80-80v-80h80v80h-80Zm360-280v-80h80v80h-80ZM180-660h120v-120H180v120Zm-60 60v-240h240v240H120Zm60 420h120v-120H180v120Zm-60 60v-240h240v240H120Zm540-540h120v-120H660v120Zm-60 60v-240h240v240H600Zm80 480v-120h-80v-80h160v120h80v80H680ZM520-400v-80h160v80H520Zm-160 0v-80h-80v-80h240v80h-80v80h-80Zm40-200v-160h80v80h80v80H400Zm-190-90v-60h60v60h-60Zm0 480v-60h60v60h-60Zm480-480v-60h60v60h-60Z"
            />
          </svg>
          <p class="me-2 font-semibold">Code:</p>
          {{ medicine.code }}
        </div>
        <div class="label text-md mb-4 flex">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="currentColor"
          >
            <path
              d="m260-520 220-360 220 360H260ZM700-80q-75 0-127.5-52.5T520-260q0-75 52.5-127.5T700-440q75 0 127.5 52.5T880-260q0 75-52.5 127.5T700-80Zm-580-20v-320h320v320H120Zm580-60q42 0 71-29t29-71q0-42-29-71t-71-29q-42 0-71 29t-29 71q0 42 29 71t71 29Zm-500-20h160v-160H200v160Zm202-420h156l-78-126-78 126Zm78 0ZM360-340Zm340 80Z"
            />
          </svg>
          <p class="me-2 font-semibold">Active Principle:</p>
          {{ medicine.activePrinciple }}
        </div>
        <div class="text-secondary mb-4 flex text-sm">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="22px"
            viewBox="0 -960 960 960"
            width="22px"
            fill="currentColor"
          >
            <path
              d="M120-760v-80h480v80H120Zm180 500h120v-100h100v-120H420v-100H300v100H200v120h100v100ZM160-120q-33 0-56.5-23.5T80-200v-440q0-33 23.5-56.5T160-720h400q33 0 56.5 23.5T640-640v440q0 33-23.5 56.5T560-120H160Zm0-80h400v-440H160v440Zm600-210q-35-17-57.5-56.5T680-560q0-68 34.5-114t85.5-46q51 0 85.5 46T920-560q0 54-22.5 93.5T840-410v250q0 17-11.5 28.5T800-120q-17 0-28.5-11.5T760-160v-250Zm40-70q12 0 26-21.5t14-58.5q0-37-14-58.5T800-640q-12 0-26 21.5T760-560q0 37 14 58.5t26 21.5Zm-440 60Zm440-140Z"
            />
          </svg>
          <p class="me-2 font-semibold">Presentation:</p>
          {{ medicine.presentation }}
        </div>
        <div class="text-secondary mb-4 flex text-sm">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="22px"
            viewBox="0 -960 960 960"
            width="22px"
            fill="currentColor"
          >
            <path
              d="M620-163 450-333l56-56 114 114 226-226 56 56-282 282Zm220-397h-80v-200h-80v120H280v-120h-80v560h240v80H200q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h167q11-35 43-57.5t70-22.5q40 0 71.5 22.5T594-840h166q33 0 56.5 23.5T840-760v200ZM480-760q17 0 28.5-11.5T520-800q0-17-11.5-28.5T480-840q-17 0-28.5 11.5T440-800q0 17 11.5 28.5T480-760Z"
            />
          </svg>
          <p class="me-2 font-semibold">Units:</p>
          {{ medicine.units }}
        </div>
        <Switch class="mb-4" label="Recipe Required" />
        <div class="text-secondary mb-4 flex text-sm">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="22px"
            viewBox="0 -960 960 960"
            width="22px"
            fill="currentColor"
          >
            <path
              d="M120-120v-560h160v-160h400v320h160v400H520v-160h-80v160H120Zm80-80h80v-80h-80v80Zm0-160h80v-80h-80v80Zm0-160h80v-80h-80v80Zm160 160h80v-80h-80v80Zm0-160h80v-80h-80v80Zm0-160h80v-80h-80v80Zm160 320h80v-80h-80v80Zm0-160h80v-80h-80v80Zm0-160h80v-80h-80v80Zm160 480h80v-80h-80v80Zm0-160h80v-80h-80v80Z"
            />
          </svg>
          <p class="me-2 font-semibold">Brand:</p>
          {{ medicine.brand }}
        </div>
        <div
          class="text-background grid grid-flow-row grid-cols-2 rounded-md px-2 py-1 text-sm"
        >
          <div
            v-for="category in medicine.categories"
            class="bg-accent m-1 rounded-lg px-2 py-1 text-sm"
          >
            {{ category }}
          </div>
        </div>
      </div>
    </div>
    <div class="responsive-grid">
      <div v-if="edit" v-for="medicine in medicines" class="card">
        <span class="text-primary font-semibold">Name</span>
        <input type="text" class="field mb-8" />
        <span class="text-primary font-semibold">Code</span>
        <input type="text" class="field mb-8" />
        <span class="text-primary font-semibold">Active Principle</span>
        <input type="text" class="field mb-8" />
        <span class="text-primary font-semibold">Presentation</span>
        <input type="text" class="field mb-8" />
        <span class="text-primary font-semibold">Units</span>
        <input type="number" class="field mb-8" />
        <div><Switch class="mb-4" label="Recipe Required" /></div>
        <span class="text-primary font-semibold">Brand</span>
        <input type="text" class="field mb-8" />
        <Dropdown />
      </div>
    </div>
  </div>
</template>

<style scoped></style>
