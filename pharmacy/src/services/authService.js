// src/services/authService.js
const ip = process.env.VUE_APP_IP; // Asegúrate de tener definida esta variable
console.log(ip);
const API_URL = `http://${ip}:8000/api2`;

export const authService = {
  async login(credentials) {
    const response = await fetch(`${API_URL}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials)
    });

    if (!response.ok) {
      throw new Error('Credenciales inválidas');
    }

    const data = await response.json();

    // Si el backend retorna un token (o información del usuario), lo guardamos
    if (data.token) {
      localStorage.setItem('user', JSON.stringify(data));
    }

    return data;
  },

  logout() {
    localStorage.removeItem('user');
  },

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));
  }
};