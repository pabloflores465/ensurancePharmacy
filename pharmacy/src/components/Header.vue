<!-- eslint-disable vue/multi-word-component-names -->

<template>
    <header class="bg-blue-600 p-4 text-white flex justify-between items-center">
        <h1 class="text-2xl font-bold">Farmacia Salud</h1>
        <nav>
            <router-link to="/" class="mx-3 hover:underline">Inicio</router-link>
            <router-link to="/medicamentos" class="mx-3 hover:underline">Medicamentos</router-link>
            <router-link to="/sucursales" class="mx-3 hover:underline">Sucursales</router-link>
            <router-link to="/contacto" class="mx-3 hover:underline">Contacto</router-link>
            <router-link to="/login" class="bg-white text-blue-600 px-3 py-1 rounded hover:bg-gray-100 transition">Iniciar Sesión</router-link>
        </nav>
    </header>
</template>


<style scoped>
/* Estilos generales */
body {
    font-family: 'Arial', sans-serif;
    background-color: #f8f9fa;
}

/* Header */
header {
    background-color: #2563eb; /* Azul fuerte */
    padding: 16px;
    color: white;
    font-weight: bold;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Navegación */
nav a {
    margin: 0 15px;
    color: white;
    text-decoration: none;
    font-size: 16px;
    font-weight: 500;
    transition: color 0.3s ease-in-out;
}

nav a:hover {
    color: #e2e8f0; /* Gris claro */
}

nav .login-btn {
    background-color: white;
    color: #2563eb;
    padding: 8px 16px;
    border-radius: 5px;
    font-weight: bold;
    transition: background-color 0.3s ease-in-out;
}

nav .login-btn:hover {
    background-color: #e2e8f0;
}

/* Hero/Banner */
.hero {
    background-color: #bfdbfe; /* Azul claro */
    padding: 60px 20px;
    text-align: center;
}

.hero h2 {
    font-size: 2.5rem;
    font-weight: bold;
    color: #111827;
}

.hero p {
    font-size: 1.2rem;
    color: #4b5563;
}

.hero input {
    width: 50%;
    padding: 12px;
    margin-top: 10px;
    border-radius: 8px;
    border: none;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    font-size: 16px;
}

/* Sección de Productos Destacados */
.products {
    background-color: #f1f5f9; /* Gris claro */
    padding: 50px 20px;
    text-align: center;
}

.products h2 {
    font-size: 2rem;
    font-weight: bold;
    margin-bottom: 20px;
}

.product-grid {
    display: flex;
    justify-content: center;
    gap: 20px;
}

.product-card {
    background: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    width: 280px;
    text-align: center;
}

.product-card img {
    width: 80px;
    height: 80px;
    object-fit: contain;
    margin-bottom: 10px;
}

.product-card h3 {
    font-size: 1.2rem;
    font-weight: bold;
}

.product-card p {
    font-size: 1rem;
    color: #16a34a; /* Verde */
    font-weight: bold;
}

/* Sección Beneficios */
.benefits {
    background-color: #e5e7eb; /* Gris más claro */
    padding: 40px 20px;
    text-align: center;
}

.benefits h2 {
    font-size: 2rem;
    font-weight: bold;
}

.benefits p {
    font-size: 1rem;
    color: #4b5563;
}

/* Mapa de Sucursales */
.map-section {
    background-color: #f1f5f9;
    padding: 50px 20px;
    text-align: center;
}

.map-placeholder {
    background-color: #9ca3af; /* Gris */
    height: 250px;
    border-radius: 10px;
}

/* Footer */
footer {
    background-color: #2563eb;
    color: white;
    padding: 20px;
    text-align: center;
    font-size: 14px;
    font-weight: bold;
}
</style>
