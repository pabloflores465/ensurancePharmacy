<template>
  <div class="bg-[url('/sign.jpg')] bg-cover bg-center w-full flex justify-center items-center">
    <div
      class="bg-black/30 backdrop-blur-md rounded-lg shadow-xl p-8 max-w-md w-full"
    >
      <h2 class="text-2xl font-bold text-white mb-6 text-center">
        Sign Up
      </h2>
      <form>
        <div class="mb-4">
          <label for="nombre" class="block text-sm font-medium text-white"
            >Name</label
          >
          <input
            type="text"
            id="nombre"
            placeholder="Name"
            class="mt-1 block w-full outline-0 border-b-2 border-yellow-300 text-white hover:border-yellow-400 p-2"
          />
        </div>
        <div class="mb-4">
          <label for="email" class="block text-sm font-medium text-white"
            >E-Mail</label
          >
          <input
            type="email"
            id="email"
            placeholder="email@example.com"
            class="mt-1 block w-full text-white outline-0 border-b-2 border-yellow-300  hover:border-yellow-400 p-2"
          />
        </div>
        <div class="mb-6">
          <label for="password" class="block text-sm font-medium text-white"
            >Password</label
          >
          <input
            type="password"
            id="password"
            placeholder="********"
            class="mt-1 block w-full border-b-2 outline-0 text-white border-yellow-400 hover:border-yellow-400  p-2"
          />
        </div>
        <Button
          type="submit"
          styles="flex w-full justify-center text-[#121827]"
        >
          Registrarse
        </Button>
      </form>
    </div>
  </div>
</template>
