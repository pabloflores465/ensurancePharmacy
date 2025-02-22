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
const edit = useEdit();
</script>

<template>
  <div
    class="from-s-background to-sh-background h-full w-full bg-gradient-to-br py-8"
  >
    <img
      src="https://images.pexels.com/photos/1704488/pexels-photo-1704488.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
      alt="Profile Picture"
      class="mx-auto mb-6 flex h-30 w-30 items-center justify-center rounded-full object-cover"
    />
    <h1 v-if="!edit" class="title mb-6">Hi, {{ profile.name }}</h1>

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
            d="M160-80q-33 0-56.5-23.5T80-160v-440q0-33 23.5-56.5T160-680h200v-120q0-33 23.5-56.5T440-880h80q33 0 56.5 23.5T600-800v120h200q33 0 56.5 23.5T880-600v440q0 33-23.5 56.5T800-80H160Zm0-80h640v-440H600q0 33-23.5 56.5T520-520h-80q-33 0-56.5-23.5T360-600H160v440Zm80-80h240v-18q0-17-9.5-31.5T444-312q-20-9-40.5-13.5T360-330q-23 0-43.5 4.5T276-312q-17 8-26.5 22.5T240-258v18Zm320-60h160v-60H560v60Zm-200-60q25 0 42.5-17.5T420-420q0-25-17.5-42.5T360-480q-25 0-42.5 17.5T300-420q0 25 17.5 42.5T360-360Zm200-60h160v-60H560v60ZM440-600h80v-200h-80v200Zm40 220Z"
          />
        </svg>
        <p class="me-2 font-semibold">CUI:</p>
        {{ profile.cui }}
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

<style scoped></style>
