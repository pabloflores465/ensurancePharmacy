<script setup lang="ts">
import Hospital from "~/pages/hospital.vue";

const prescriptions: Ref<
  {
    id: number;
    hospital: string;
    doctor: string;
    patient: string;
    pharmacy: string;
    date: string;
    total: number;
    copay: number;
    comments: string;
    medicines: string[];
    date_created: string;
    secured: boolean;
    minimun: number;
    auth_no: string;
    show: boolean;
  }[]
> = ref([
  {
    id: 1,
    hospital: "City Hospital",
    doctor: "Dr. Smith",
    patient: "John Doe",
    pharmacy: "Pharmacy One",
    date: "2023-04-01",
    total: 150.0,
    copay: 15.0,
    comments: "Take twice daily",
    medicines: ["Aspirin", "Ibuprofen"],
    date_created: "2023-04-01T09:00:00Z",
    secured: true,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 2,
    hospital: "General Hospital",
    doctor: "Dr. Brown",
    patient: "Jane Doe",
    pharmacy: "Pharmacy Two",
    date: "2023-04-02",
    total: 200.0,
    copay: 20.0,
    comments: "Take with food",
    medicines: ["Paracetamol"],
    date_created: "2023-04-02T10:00:00Z",
    secured: false,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 3,
    hospital: "Mercy Hospital",
    doctor: "Dr. Lee",
    patient: "Alice Johnson",
    pharmacy: "Pharmacy Three",
    date: "2023-04-03",
    total: 175.0,
    copay: 17.5,
    comments: "Apply cream to affected area",
    medicines: ["Hydrocortisone"],
    date_created: "2023-04-03T11:00:00Z",
    secured: true,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 4,
    hospital: "Saint Mary's",
    doctor: "Dr. Patel",
    patient: "Bob Smith",
    pharmacy: "Pharmacy One",
    date: "2023-04-04",
    total: 220.0,
    copay: 22.0,
    comments: "Use as directed",
    medicines: ["Amoxicillin", "Clavulanate"],
    date_created: "2023-04-04T12:00:00Z",
    secured: false,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 5,
    hospital: "Central Hospital",
    doctor: "Dr. Garcia",
    patient: "Carol Davis",
    pharmacy: "Pharmacy Two",
    date: "2023-04-05",
    total: 130.0,
    copay: 13.0,
    comments: "Monitor blood pressure",
    medicines: ["Lisinopril"],
    date_created: "2023-04-05T08:30:00Z",
    secured: true,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 6,
    hospital: "City Hospital",
    doctor: "Dr. Nguyen",
    patient: "David Wilson",
    pharmacy: "Pharmacy Three",
    date: "2023-04-06",
    total: 180.0,
    copay: 18.0,
    comments: "Take after meals",
    medicines: ["Metformin"],
    date_created: "2023-04-06T09:45:00Z",
    secured: false,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 7,
    hospital: "General Hospital",
    doctor: "Dr. Kim",
    patient: "Emma Martinez",
    pharmacy: "Pharmacy One",
    date: "2023-04-07",
    total: 210.0,
    copay: 21.0,
    comments: "Evening dose only",
    medicines: ["Atorvastatin"],
    date_created: "2023-04-07T10:15:00Z",
    secured: true,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 8,
    hospital: "Mercy Hospital",
    doctor: "Dr. Robinson",
    patient: "Frank Clark",
    pharmacy: "Pharmacy Two",
    date: "2023-04-08",
    total: 160.0,
    copay: 16.0,
    comments: "Take with water",
    medicines: ["Omeprazole"],
    date_created: "2023-04-08T11:30:00Z",
    secured: false,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 9,
    hospital: "Saint Mary's",
    doctor: "Dr. Davis",
    patient: "Grace Lewis",
    pharmacy: "Pharmacy Three",
    date: "2023-04-09",
    total: 190.0,
    copay: 19.0,
    comments: "Keep refrigerated",
    medicines: ["Insulin"],
    date_created: "2023-04-09T07:50:00Z",
    secured: true,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 10,
    hospital: "Central Hospital",
    doctor: "Dr. Clark",
    patient: "Henry Young",
    pharmacy: "Pharmacy One",
    date: "2023-04-10",
    total: 205.0,
    copay: 20.5,
    comments: "Do not operate heavy machinery",
    medicines: ["Diazepam"],
    date_created: "2023-04-10T12:20:00Z",
    secured: false,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 11,
    hospital: "City Hospital",
    doctor: "Dr. Lopez",
    patient: "Isabella Hernandez",
    pharmacy: "Pharmacy Two",
    date: "2023-04-11",
    total: 145.0,
    copay: 14.5,
    comments: "Take on an empty stomach",
    medicines: ["Prednisone"],
    date_created: "2023-04-11T09:10:00Z",
    secured: true,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 12,
    hospital: "General Hospital",
    doctor: "Dr. Gonzalez",
    patient: "Jack Robinson",
    pharmacy: "Pharmacy Three",
    date: "2023-04-12",
    total: 230.0,
    copay: 23.0,
    comments: "Administer intravenously",
    medicines: ["Ceftriaxone"],
    date_created: "2023-04-12T10:40:00Z",
    secured: false,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 13,
    hospital: "Mercy Hospital",
    doctor: "Dr. Wilson",
    patient: "Karen Walker",
    pharmacy: "Pharmacy One",
    date: "2023-04-13",
    total: 170.0,
    copay: 17.0,
    comments: "Monitor for side effects",
    medicines: ["Levothyroxine"],
    date_created: "2023-04-13T11:55:00Z",
    secured: true,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 14,
    hospital: "Saint Mary's",
    doctor: "Dr. Evans",
    patient: "Leo King",
    pharmacy: "Pharmacy Two",
    date: "2023-04-14",
    total: 195.0,
    copay: 19.5,
    comments: "Take in the morning",
    medicines: ["Amlodipine"],
    date_created: "2023-04-14T08:05:00Z",
    secured: false,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
  {
    id: 15,
    hospital: "Central Hospital",
    doctor: "Dr. Turner",
    patient: "Mia Scott",
    pharmacy: "Pharmacy Three",
    date: "2023-04-15",
    total: 185.0,
    copay: 18.5,
    comments: "Use as prescribed",
    medicines: ["Simvastatin"],
    date_created: "2023-04-15T10:30:00Z",
    secured: true,
    minimun: 1,
    auth_no: "123456789",
    show: false,
  },
]);
</script>

<template>
  <main
    class="bg-image-[url('https://cdn.prod.website-files.com/6466101d017ab9d60c8d0137/668ce90cd1d21a2f4e8a8536_Repeat%20Prescriptions.jpg')] max-sm:flex-col max-sm:items-center max-sm:justify-center max-sm:gap-4 max-sm:px-2 max-sm:py-8 md:grid md:grid-cols-2 md:gap-4 md:px-8 md:py-16 lg:grid-cols-4 lg:gap-2 xl:grid-cols-4"
  >
    <div
      v-for="prescription in prescriptions"
      class="card lg:align-center gap-4 transition duration-300 hover:scale-105 max-sm:mx-2 max-sm:flex-col md:flex-row lg:align-middle"
    >
      <p class="text-title title mb-6">Number: {{ prescription.id }}</p>
      <p class="text-primary mb-3 flex text-lg">
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
        <strong class="me-2">Hospital:</strong> {{ prescription.hospital }}
      </p>
      <div class="text-md text-primary mb-4 flex">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M480-480q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM160-160v-112q0-34 17.5-62.5T224-378q62-31 126-46.5T480-440q66 0 130 15.5T736-378q29 15 46.5 43.5T800-272v112H160Zm80-80h480v-32q0-11-5.5-20T700-306q-54-27-109-40.5T480-360q-56 0-111 13.5T260-306q-9 5-14.5 14t-5.5 20v32Zm240-320q33 0 56.5-23.5T560-640q0-33-23.5-56.5T480-720q-33 0-56.5 23.5T400-640q0 33 23.5 56.5T480-560Zm0-80Zm0 400Z"
          />
        </svg>
        <p class="me-2 font-semibold">Usuario:</p>

        {{ prescription.patient }}
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
            d="M680-320q-50 0-85-35t-35-85q0-50 35-85t85-35q50 0 85 35t35 85q0 50-35 85t-85 35Zm0-80q17 0 28.5-11.5T720-440q0-17-11.5-28.5T680-480q-17 0-28.5 11.5T640-440q0 17 11.5 28.5T680-400ZM440-40v-116q0-21 10-39.5t28-29.5q32-19 67.5-31.5T618-275l62 75 62-75q37 6 72 18.5t67 31.5q18 11 28.5 29.5T920-156v116H440Zm79-80h123l-54-66q-18 5-35 13t-34 17v36Zm199 0h122v-36q-16-10-33-17.5T772-186l-54 66Zm-76 0Zm76 0Zm-518 0q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v200q-16-20-35-38t-45-24v-138H200v560h166q-3 11-4.5 22t-1.5 22v36H200Zm80-480h280q26-20 57-30t63-10v-40H280v80Zm0 160h200q0-21 4.5-41t12.5-39H280v80Zm0 160h138q11-9 23.5-16t25.5-13v-51H280v80Zm-80 80v-560 137-17 440Zm480-240Z"
          />
        </svg>
        <p class="text-tertiary me-2 font-semibold">Doctor:</p>
        {{ prescription.doctor }}
      </div>
      <button
        @click="() => (prescription.show = !prescription.show)"
        class="btn flex justify-center"
      >
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M440-280h80v-240h-80v240Zm40-320q17 0 28.5-11.5T520-640q0-17-11.5-28.5T480-680q-17 0-28.5 11.5T440-640q0 17 11.5 28.5T480-600Zm0 520q-83 0-156-31.5T197-197q-54-54-85.5-127T80-480q0-83 31.5-156T197-763q54-54 127-85.5T480-880q83 0 156 31.5T763-763q54 54 85.5 127T880-480q0 83-31.5 156T763-197q-54 54-127 85.5T480-80Zm0-80q134 0 227-93t93-227q0-134-93-227t-227-93q-134 0-227 93t-93 227q0 134 93 227t227 93Zm0-320Z"
          />
        </svg>
        See More Details
      </button>
    </div>
    <Modal
      v-for="prescription in prescriptions"
      v-model:show="prescription.show"
    >
      <p class="title mb-6">Number: {{ prescription.id }}</p>
      <p class="text-terciary mb-2 font-semibold">People</p>
      <div class="text-md text-primary mb-4 flex">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M480-480q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM160-160v-112q0-34 17.5-62.5T224-378q62-31 126-46.5T480-440q66 0 130 15.5T736-378q29 15 46.5 43.5T800-272v112H160Zm80-80h480v-32q0-11-5.5-20T700-306q-54-27-109-40.5T480-360q-56 0-111 13.5T260-306q-9 5-14.5 14t-5.5 20v32Zm240-320q33 0 56.5-23.5T560-640q0-33-23.5-56.5T480-720q-33 0-56.5 23.5T400-640q0 33 23.5 56.5T480-560Zm0-80Zm0 400Z"
          />
        </svg>
        <p class="me-2 font-semibold">Usuario:</p>

        {{ prescription.patient }}
      </div>
      <div class="text-secondary text-md mb-6 flex">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="20px"
          viewBox="0 -960 960 960"
          width="20px"
          fill="currentColor"
        >
          <path
            d="M680-320q-50 0-85-35t-35-85q0-50 35-85t85-35q50 0 85 35t35 85q0 50-35 85t-85 35Zm0-80q17 0 28.5-11.5T720-440q0-17-11.5-28.5T680-480q-17 0-28.5 11.5T640-440q0 17 11.5 28.5T680-400ZM440-40v-116q0-21 10-39.5t28-29.5q32-19 67.5-31.5T618-275l62 75 62-75q37 6 72 18.5t67 31.5q18 11 28.5 29.5T920-156v116H440Zm79-80h123l-54-66q-18 5-35 13t-34 17v36Zm199 0h122v-36q-16-10-33-17.5T772-186l-54 66Zm-76 0Zm76 0Zm-518 0q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v200q-16-20-35-38t-45-24v-138H200v560h166q-3 11-4.5 22t-1.5 22v36H200Zm80-480h280q26-20 57-30t63-10v-40H280v80Zm0 160h200q0-21 4.5-41t12.5-39H280v80Zm0 160h138q11-9 23.5-16t25.5-13v-51H280v80Zm-80 80v-560 137-17 440Zm480-240Z"
          />
        </svg>
        <p class="me-2 font-semibold">Doctor:</p>
        {{ prescription.doctor }}
      </div>
      <div class="text-terciary text-md mb-3 font-semibold">Institutions</div>
      <div class="text-secondary text-md mb-4 flex">
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
        {{ prescription.hospital }}
      </div>
      <div class="text-secondary text-md mb-6 flex">
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="M420-260h120v-100h100v-120H540v-100H420v100H320v120h100v100ZM280-120q-33 0-56.5-23.5T200-200v-440q0-33 23.5-56.5T280-720h400q33 0 56.5 23.5T760-640v440q0 33-23.5 56.5T680-120H280Zm0-80h400v-440H280v440Zm-40-560v-80h480v80H240Zm40 120v440-440Z"
          />
        </svg>
        <p class="me-2 font-semibold">Pharmacy:</p>
        {{ prescription.pharmacy }}
      </div>
      <div class="text-terciary text-md mb-3 font-semibold">Pay</div>
      <div class="text-success text-md mb-4 flex">
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
        <p class="me-2 font-semibold">Total to Pay:</p>
        {{ prescription.total }}
      </div>
      <div class="text-error text-md mb-4 flex">
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
        <p class="me-2 font-semibold">Copay:</p>
        {{ prescription.copay }}
      </div>
      <div class="text-accent text-md mb-4 flex">
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
        <p class="me-2 font-semibold">Auth:</p>
        {{ prescription.auth_no }}
      </div>
      <Switch class="mb-6" label="Secured" disabled checked></Switch>
      <p class="text-terciary text-md mb-3 font-semibold">Medicines</p>
      <div
        v-for="medicine in prescription.medicines"
        class="text-md text-primary mb-4 flex"
      >
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="currentColor"
        >
          <path
            d="m320-60-80-60v-160h-40q-33 0-56.5-23.5T120-360v-300q-17 0-28.5-11.5T80-700q0-17 11.5-28.5T120-740h120v-60h-20q-17 0-28.5-11.5T180-840q0-17 11.5-28.5T220-880h120q17 0 28.5 11.5T380-840q0 17-11.5 28.5T340-800h-20v60h120q17 0 28.5 11.5T480-700q0 17-11.5 28.5T440-660v300q0 33-23.5 56.5T360-280h-40v220ZM200-360h160v-60h-70q-12 0-21-9t-9-21q0-12 9-21t21-9h70v-60h-70q-12 0-21-9t-9-21q0-12 9-21t21-9h70v-60H200v300ZM600-80q-33 0-56.5-23.5T520-160v-260q0-29 10-48t21-33q11-14 20-22.5t9-16.5v-20q-17 0-28.5-11.5T540-600q0-17 11.5-28.5T580-640h200q17 0 28.5 11.5T820-600q0 17-11.5 28.5T780-560v20q0 8 10 18t22 24q11 14 19.5 33t8.5 45v260q0 33-23.5 56.5T760-80H600Zm0-320h160v-20q0-15-9-26t-20-24q-11-13-21-29t-10-41v-20h-40v20q0 24-9.5 40T630-471q-11 13-20.5 24.5T600-420v20Zm0 120h160v-60H600v60Zm0 120h160v-60H600v60Zm0-120h160-160Z"
          />
        </svg>
        {{ medicine }}
      </div>
      <p class="text-terciary text-md mt-6 mb-3 font-semibold">Comment</p>
      <textarea class="text-secondary">{{ prescription.comments }}</textarea>
    </Modal>
  </main>
</template>

<style scoped></style>
