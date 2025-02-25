<script setup lang="ts">
const profile: Ref<{
  name: string;
  cui: number;
  phone: string;
  email: string;
  address: string;
  birthdate: string;
  role: string;
  policy: string;
}> = ref({
  name: "John Doe",
  cui: 123456789,
  phone: "+1 (555) 123-4567",
  email: "johndoe@example.com",
  address: "123 Main Street, Springfield, USA",
  birthdate: "1990-01-01",
  role: "Administrator",
  policy: "Standard User Policy",
});
const appointments: Ref<
  {
    number: number;
    date: string;
    doctor: string;
    hospital: string;
  }[]
> = ref([
  {
    number: 1,
    date: "2025-03-01",
    doctor: "Dr. Ana López",
    hospital: "Hospital Central",
  },
  {
    number: 2,
    date: "2025-03-02",
    doctor: "Dr. Carlos Pérez",
    hospital: "Clínica del Sol",
  },
  {
    number: 3,
    date: "2025-03-03",
    doctor: "Dra. María Gómez",
    hospital: "Hospital Santa María",
  },
  {
    number: 4,
    date: "2025-03-04",
    doctor: "Dr. Juan Martínez",
    hospital: "Centro Médico",
  },
  {
    number: 5,
    date: "2025-03-05",
    doctor: "Dra. Elena Rodríguez",
    hospital: "Hospital San Juan",
  },
  {
    number: 6,
    date: "2025-03-06",
    doctor: "Dr. Luis Hernández",
    hospital: "Clínica Los Ángeles",
  },
  {
    number: 7,
    date: "2025-03-07",
    doctor: "Dra. Sofía García",
    hospital: "Hospital General",
  },
  {
    number: 8,
    date: "2025-03-08",
    doctor: "Dr. Pedro Ramírez",
    hospital: "Clínica del Norte",
  },
  {
    number: 9,
    date: "2025-03-09",
    doctor: "Dra. Marta Sánchez",
    hospital: "Hospital Regional",
  },
  {
    number: 10,
    date: "2025-03-10",
    doctor: "Dr. Jorge Castro",
    hospital: "Clínica del Este",
  },
  {
    number: 11,
    date: "2025-03-11",
    doctor: "Dra. Lucía Torres",
    hospital: "Hospital Central",
  },
  {
    number: 12,
    date: "2025-03-12",
    doctor: "Dr. Miguel Reyes",
    hospital: "Centro Médico",
  },
  {
    number: 13,
    date: "2025-03-13",
    doctor: "Dra. Laura Morales",
    hospital: "Hospital San José",
  },
  {
    number: 14,
    date: "2025-03-14",
    doctor: "Dr. Andrés Vega",
    hospital: "Clínica del Valle",
  },
  {
    number: 15,
    date: "2025-03-15",
    doctor: "Dra. Daniela Ruiz",
    hospital: "Hospital de la Luz",
  },
]);
const edit = useEdit();
</script>

<template>
  <div
    class="from-s-background to-sh-background h-full w-full bg-gradient-to-br py-8"
  >
    <div class="align-middle md:flex">
      <div class="h-full align-middle md:my-auto md:ms-8 md:me-6 md:flex-col">
        <img
          src="https://images.pexels.com/photos/1704488/pexels-photo-1704488.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
          alt="Profile Picture"
          class="mx-auto mb-6 flex h-30 w-30 items-center justify-center rounded-full object-cover"
        />
        <h1 v-if="!edit" class="title mb-6">Hi, {{ profile.name }}</h1>
      </div>
      <div v-if="!edit" class="card mb-6">
        <h2 class="title mb-6 text-lg">General Info</h2>
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
              d="M160-160q-33 0-56.5-23.5T80-240v-480q0-33 23.5-56.5T160-800h640q33 0 56.5 23.5T880-720v480q0 33-23.5 56.5T800-160H160Zm320-280L160-640v400h640v-400L480-440Zm0-80 320-200H160l320 200ZM160-640v-80 480-400Z"
            />
          </svg>
          <p class="me-2 font-semibold">E-Mail:</p>
          {{ profile.email }}
        </div>
        <div class="text-primary mb-6 flex">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="currentColor"
          >
            <path
              d="M798-120q-125 0-247-54.5T329-329Q229-429 174.5-551T120-798q0-18 12-30t30-12h162q14 0 25 9.5t13 22.5l26 140q2 16-1 27t-11 19l-97 98q20 37 47.5 71.5T387-386q31 31 65 57.5t72 48.5l94-94q9-9 23.5-13.5T670-390l138 28q14 4 23 14.5t9 23.5v162q0 18-12 30t-30 12ZM241-600l66-66-17-94h-89q5 41 14 81t26 79Zm358 358q39 17 79.5 27t81.5 13v-88l-94-19-67 67ZM241-600Zm358 358Z"
            />
          </svg>
          <p class="me-2 font-semibold">Phone:</p>
          {{ profile.phone }}
        </div>
        <div class="text-secondary mb-4 flex text-sm">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="20px"
            viewBox="0 -960 960 960"
            width="20px"
            fill="currentColor"
          >
            <path
              d="M240-200h120v-240h240v240h120v-360L480-740 240-560v360Zm-80 80v-480l320-240 320 240v480H520v-240h-80v240H160Zm320-350Z"
            />
          </svg>
          <p class="me-2 font-semibold">Address:</p>
          {{ profile.address }}
        </div>
        <div class="text-secondary mb-4 flex text-sm">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="20px"
            viewBox="0 -960 960 960"
            width="20px"
            fill="currentColor"
          >
            <path
              d="M160-80q-17 0-28.5-11.5T120-120v-200q0-33 23.5-56.5T200-400v-160q0-33 23.5-56.5T280-640h160v-58q-18-12-29-29t-11-41q0-15 6-29.5t18-26.5l56-56 56 56q12 12 18 26.5t6 29.5q0 24-11 41t-29 29v58h160q33 0 56.5 23.5T760-560v160q33 0 56.5 23.5T840-320v200q0 17-11.5 28.5T800-80H160Zm120-320h400v-160H280v160Zm-80 240h560v-160H200v160Zm80-240h400-400Zm-80 240h560-560Zm560-240H200h560Z"
            />
          </svg>
          <p class="me-2 font-semibold">Birthday:</p>
          {{ profile.birthdate }}
        </div>
        <div class="text-secondary mb-6 flex text-sm">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="20px"
            viewBox="0 -960 960 960"
            width="20px"
            fill="currentColor"
          >
            <path
              d="M160-80q-33 0-56.5-23.5T80-160v-440q0-33 23.5-56.5T160-680h200v-120q0-33 23.5-56.5T440-880h80q33 0 56.5 23.5T600-800v120h200q33 0 56.5 23.5T880-600v440q0 33-23.5 56.5T800-80H160Zm0-80h640v-440H600q0 33-23.5 56.5T520-520h-80q-33 0-56.5-23.5T360-600H160v440Zm80-80h240v-18q0-17-9.5-31.5T444-312q-20-9-40.5-13.5T360-330q-23 0-43.5 4.5T276-312q-17 8-26.5 22.5T240-258v18Zm320-60h160v-60H560v60Zm-200-60q25 0 42.5-17.5T420-420q0-25-17.5-42.5T360-480q-25 0-42.5 17.5T300-420q0 25 17.5 42.5T360-360Zm200-60h160v-60H560v60ZM440-600h80v-200h-80v200Zm40 220Z"
            />
          </svg>
          <p class="me-2 font-semibold">CUI:</p>
          {{ profile.cui }}
        </div>
        <div class="text-success mb-6 flex">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="currentColor"
          >
            <path
              d="M280-280h280v-80H280v80Zm0-160h400v-80H280v80Zm0-160h400v-80H280v80Zm-80 480q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H200Zm0-80h560v-560H200v560Zm0-560v560-560Z"
            />
          </svg>

          <p class="me-2 font-semibold">Policy:</p>
          {{ profile.policy }}
        </div>
      </div>
      <div></div>
    </div>
    <div class="card">
      <div class="title mb-6">Appointments</div>
      <div class="responsive-grid">
        <div v-for="appointment in appointments">
          <div
            class="bg-s-background hover:bg-h-secondary mb-6 rounded-lg px-4 py-2"
          >
            <div class="text-primary mb-6 flex">
              <svg
                class="text-accent me-2"
                xmlns="http://www.w3.org/2000/svg"
                height="24px"
                viewBox="0 -960 960 960"
                width="24px"
                fill="currentColor"
              >
                <path
                  d="m240-160 40-160H120l20-80h160l40-160H180l20-80h160l40-160h80l-40 160h160l40-160h80l-40 160h160l-20 80H660l-40 160h160l-20 80H600l-40 160h-80l40-160H360l-40 160h-80Zm140-240h160l40-160H420l-40 160Z"
                />
              </svg>
              <div class="title me-2 text-lg font-semibold">
                Number: {{ appointment.number }}
              </div>
            </div>
            <div class="text-primary mb-4 flex">
              <svg
                class="me-2"
                xmlns="http://www.w3.org/2000/svg"
                height="22px"
                viewBox="0 -960 960 960"
                width="22px"
                fill="currentColor"
              >
                <path
                  d="M580-240q-42 0-71-29t-29-71q0-42 29-71t71-29q42 0 71 29t29 71q0 42-29 71t-71 29ZM200-80q-33 0-56.5-23.5T120-160v-560q0-33 23.5-56.5T200-800h40v-80h80v80h320v-80h80v80h40q33 0 56.5 23.5T840-720v560q0 33-23.5 56.5T760-80H200Zm0-80h560v-400H200v400Zm0-480h560v-80H200v80Zm0 0v-80 80Z"
                />
              </svg>
              <div class="me-2 font-semibold">Date:</div>
              {{ appointment.date }}
            </div>
            <div class="text-secondary mb-4 flex text-sm">
              <svg
                class="me-2"
                xmlns="http://www.w3.org/2000/svg"
                height="20px"
                viewBox="0 -960 960 960"
                width="20px"
                fill="currentColor"
              >
                <path
                  d="M694-120 552-262l57-56 85 85 170-170 56 57-226 226ZM540-80q-108 0-184-76t-76-184v-23q-86-14-143-80.5T80-600v-240h120v-40h80v160h-80v-40h-40v160q0 66 47 113t113 47q66 0 113-47t47-113v-160h-40v40h-80v-160h80v40h120v240q0 90-57 156.5T360-363v23q0 75 52.5 127.5T540-160v80Z"
                />
              </svg>
              <div class="me-2 font-semibold">Doctor:</div>
              {{ appointment.doctor }}
            </div>
            <div class="text-secondary flex text-sm">
              <svg
                class="me-2"
                xmlns="http://www.w3.org/2000/svg"
                height="20px"
                viewBox="0 -960 960 960"
                width="20px"
                fill="currentColor"
              >
                <path
                  d="M420-280h120v-140h140v-120H540v-140H420v140H280v120h140v140ZM200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H200Zm0-80h560v-560H200v560Zm0-560v560-560Z"
                />
              </svg>
              <div class="me-2 font-semibold">hospital:</div>
              {{ appointment.doctor }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="edit" class="card mb-6">
      <p class="title mb-6">Edit Profile</p>
      <div class="mb-8">
        <span class="text-primary font-semibold">Name</span>
        <input type="text" class="field" />
      </div>
      <div class="mb-8">
        <span class="text-primary font-semibold">Email</span>
        <input type="email" class="field" />
      </div>
      <div class="mb-8">
        <span class="text-primary font-semibold">Phone</span>
        <input type="number" class="field" />
      </div>
      <div class="mb-8">
        <span class="text-primary font-semibold">Address</span>
        <input type="text" class="field" />
      </div>
      <div class="mb-8">
        <span class="text-primary font-semibold">BirthDay</span>
        <input type="text" class="field" />
      </div>
      <div class="mb-8">
        <span class="text-primary font-semibold">CUI</span>
        <input type="number" class="field" />
      </div>
      <button class="btn mx-auto flex">
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
          />
        </svg>
        Save
      </button>
    </div>
  </div>
</template>
