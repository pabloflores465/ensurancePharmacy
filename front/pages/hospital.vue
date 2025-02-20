<script setup lang="ts">
const hospitals: Ref<
  {
    id_hos: number;
    name: string;
    address: string;
    phone: string;
    email: string;
    enabled: boolean;
  }[]
> = ref([
  {
    id_hos: 1,
    name: "Hospital Central",
    address: "Av. Principal 123",
    phone: "555-1234",
    email: "contacto@hospitalcentral.com",
    enabled: true,
  },
  {
    id_hos: 2,
    name: "Clínica del Norte",
    address: "Calle Secundaria 456",
    phone: "555-5678",
    email: "info@clinicadelnorte.org",
    enabled: false,
  },
  {
    id_hos: 3,
    name: "Sanatorio del Este",
    address: "Boulevard de la Salud 789",
    phone: "555-9012",
    email: "contact@sanatoriodeleste.com",
    enabled: true,
  },
  {
    id_hos: 1,
    name: "Hospital Central",
    address: "Av. Principal 123",
    phone: "555-1234",
    email: "contacto@hospitalcentral.com",
    enabled: true,
  },
  {
    id_hos: 2,
    name: "Clínica del Norte",
    address: "Calle Secundaria 456",
    phone: "555-5678",
    email: "info@clinicadelnorte.org",
    enabled: false,
  },
  {
    id_hos: 3,
    name: "Sanatorio del Este",
    address: "Boulevard de la Salud 789",
    phone: "555-9012",
    email: "contact@sanatoriodeleste.com",
    enabled: true,
  },
  {
    id_hos: 1,
    name: "Hospital Central",
    address: "Av. Principal 123",
    phone: "555-1234",
    email: "contacto@hospitalcentral.com",
    enabled: true,
  },
  {
    id_hos: 2,
    name: "Clínica del Norte",
    address: "Calle Secundaria 456",
    phone: "555-5678",
    email: "info@clinicadelnorte.org",
    enabled: false,
  },
  {
    id_hos: 3,
    name: "Sanatorio del Este",
    address: "Boulevard de la Salud 789",
    phone: "555-9012",
    email: "contact@sanatoriodeleste.com",
    enabled: true,
  },
]);
</script>

<template>
  <div
    class="bg-image-[url('/medicine.jpg')] h-full w-full grid-flow-row items-center justify-center gap-1 md:grid md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4"
  >
    <div v-for="hospital in hospitals" class="card">
      <h2 class="title mb-6">Hospital #{{ hospital.id_hos }}</h2>
      <p class="text-primary mb-4 flex">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M160-80q-33 0-56.5-23.5T80-160v-440q0-33 23.5-56.5T160-680h200v-120q0-33 23.5-56.5T440-880h80q33 0 56.5 23.5T600-800v120h200q33 0 56.5 23.5T880-600v440q0 33-23.5 56.5T800-80H160Zm0-80h640v-440H600q0 33-23.5 56.5T520-520h-80q-33 0-56.5-23.5T360-600H160v440Zm80-80h240v-18q0-17-9.5-31.5T444-312q-20-9-40.5-13.5T360-330q-23 0-43.5 4.5T276-312q-17 8-26.5 22.5T240-258v18Zm320-60h160v-60H560v60Zm-200-60q25 0 42.5-17.5T420-420q0-25-17.5-42.5T360-480q-25 0-42.5 17.5T300-420q0 25 17.5 42.5T360-360Zm200-60h160v-60H560v60ZM440-600h80v-200h-80v200Zm40 220Z"
          /></svg
        ><span class="me-2 font-semibold">Nombre:</span> {{ hospital.name }}
      </p>
      <p class="text-secondary mb-2 flex text-sm">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="20px"
          viewBox="0 -960 960 960"
          width="20px"
          fill="currentColor"
        >
          <path
            d="M480-301q99-80 149.5-154T680-594q0-90-56-148t-144-58q-88 0-144 58t-56 148q0 65 50.5 139T480-301Zm0 101Q339-304 269.5-402T200-594q0-125 78-205.5T480-880q124 0 202 80.5T760-594q0 94-69.5 192T480-200Zm0-320q33 0 56.5-23.5T560-600q0-33-23.5-56.5T480-680q-33 0-56.5 23.5T400-600q0 33 23.5 56.5T480-520ZM200-80v-80h560v80H200Zm280-520Z"
          /></svg
        ><span class="font-semibold">Dirección:</span> {{ hospital.address }}
      </p>
      <p class="text-secondary mb-2 flex text-sm">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="20px"
          viewBox="0 -960 960 960"
          width="20px"
          fill="currentColor"
        >
          <path
            d="M798-120q-125 0-247-54.5T329-329Q229-429 174.5-551T120-798q0-18 12-30t30-12h162q14 0 25 9.5t13 22.5l26 140q2 16-1 27t-11 19l-97 98q20 37 47.5 71.5T387-386q31 31 65 57.5t72 48.5l94-94q9-9 23.5-13.5T670-390l138 28q14 4 23 14.5t9 23.5v162q0 18-12 30t-30 12ZM241-600l66-66-17-94h-89q5 41 14 81t26 79Zm358 358q39 17 79.5 27t81.5 13v-88l-94-19-67 67ZM241-600Zm358 358Z"
          /></svg
        ><span class="font-semibold">Teléfono:</span> {{ hospital.phone }}
      </p>
      <p class="text-secondary mb-4 flex text-sm">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="20px"
          viewBox="0 -960 960 960"
          width="20px"
          fill="currentColor"
        >
          <path
            d="M160-160q-33 0-56.5-23.5T80-240v-480q0-33 23.5-56.5T160-800h640q33 0 56.5 23.5T880-720v480q0 33-23.5 56.5T800-160H160Zm320-280L160-640v400h640v-400L480-440Zm0-80 320-200H160l320 200ZM160-640v-80 480-400Z"
          />
        </svg>
        <span class="font-semibold">Email:</span> {{ hospital.email }}
      </p>
      <Switch
        class="text-sm"
        v-model="hospital.enabled"
        label="Enabled"
      ></Switch>
    </div>
  </div>
</template>
