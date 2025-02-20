<script setup lang="ts">
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
    show: false,
  },
]);
</script>

<template>
  <main
    class="bg-image-[url('https://cdn.prod.website-files.com/6466101d017ab9d60c8d0137/668ce90cd1d21a2f4e8a8536_Repeat%20Prescriptions.jpg')]"
  >
    <div
      v-for="prescription in prescriptions"
      class="card gap-4 max-sm:mx-2 max-sm:flex-col md:grid md:grid-flow-row md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4"
    >
      <p class="text-title title mb-4">Hospital: {{ prescription.hospital }}</p>
      <div class="text-md text-primary mb-4 flex">
        <p class="me-2 font-semibold">Usuario:</p>
        {{ prescription.patient }}
      </div>
      <div class="text-secondary mb-6 flex text-sm">
        <p class="me-2 font-semibold">Doctor:</p>
        {{ prescription.doctor }}
      </div>
      <button
        @click="() => (prescription.show = !prescription.show)"
        class="btn flex"
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
      <Modal :show="prescription.show">
        <div class="title">
          dsfsghjfgdfgsdafghjfhdfsfadgfhgjhhdgsfaddgfhgjhfhdgsfadgfhgjhdghgjhola
          mundo
        </div>
      </Modal>
    </div>
  </main>
</template>

<style scoped></style>
