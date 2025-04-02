import { createMemoryHistory, createRouter } from "vue-router";
import type { RouteLocationNormalized, NavigationGuardNext } from "vue-router";
import Login from "./pages/login.vue";
import Home from "./pages/home.vue";
import Register from "./pages/register.vue";
import AdminUsers from "./pages/admin/users.vue";
import ProfileCompletion from "./pages/profile-completion.vue";
import InactiveAccount from "./pages/inactive-account.vue";
import { checkMissingRequiredFields } from "./utils/profile-utils";

// Middleware para proteger rutas
const requireAuth = (
  to: RouteLocationNormalized, 
  from: RouteLocationNormalized, 
  next: NavigationGuardNext
) => {
  const profile = JSON.parse(localStorage.getItem("user") || "null");
  
  if (!profile || profile === "null") {
    // No autenticado, redirigir a login
    next('/login');
  } else if (profile.enabled !== 1) {
    // Usuario no activado
    next('/inactive-account');
  } else {
    // Verificar si el usuario necesita completar su perfil
    const missingFields = checkMissingRequiredFields(profile);
    if (missingFields && to.path !== '/profile-completion') {
      next('/profile-completion');
    } else {
      next(); // Usuario autenticado y con perfil completo, continuar
    }
  }
};

// Middleware para rutas de administrador
const requireAdmin = (
  to: RouteLocationNormalized, 
  from: RouteLocationNormalized, 
  next: NavigationGuardNext
) => {
  const profile = JSON.parse(localStorage.getItem("user") || "null");
  
  if (!profile || profile === "null") {
    // No autenticado, redirigir a login
    next('/login');
  } else if (profile.role !== "ADMIN") {
    // No es administrador, redirigir a home
    next('/home');
  } else {
    next(); // Es administrador, continuar
  }
};

// Middleware para comprobar si el usuario tiene los datos completos
const requireCompleteProfile = (
  to: RouteLocationNormalized, 
  from: RouteLocationNormalized, 
  next: NavigationGuardNext
) => {
  const profile = JSON.parse(localStorage.getItem("user") || "null");
  
  if (!profile || profile === "null") {
    next('/login');
    return;
  }
  
  const missingFields = checkMissingRequiredFields(profile);
  
  if (missingFields) {
    // Solo permitir acceso a la ruta de completar perfil
    if (to.path !== '/profile-completion') {
      next('/profile-completion');
    } else {
      next();
    }
  } else {
    next(); // Perfil completo, continuar
  }
};

// Middleware para usuarios inactivos
const inactiveUserOnly = (
  to: RouteLocationNormalized, 
  from: RouteLocationNormalized, 
  next: NavigationGuardNext
) => {
  const profile = JSON.parse(localStorage.getItem("user") || "null");
  
  if (!profile || profile === "null") {
    next('/login');
  } else if (profile.enabled === 1) {
    next('/home');
  } else {
    next(); // Usuario inactivo, puede ver la página
  }
};

const routes = [
  { path: "/", redirect: "/login" },
  { path: "/login", component: Login },
  { path: "/register", component: Register },
  { 
    path: "/home", 
    component: Home, 
    beforeEnter: requireAuth 
  },
  {
    path: "/admin/users",
    component: AdminUsers,
    beforeEnter: requireAdmin
  },
  {
    path: "/profile-completion",
    component: ProfileCompletion,
    beforeEnter: requireCompleteProfile
  },
  {
    path: "/inactive-account",
    component: InactiveAccount,
    beforeEnter: inactiveUserOnly
  }
];

const router = createRouter({
  history: createMemoryHistory(),
  routes,
});

export default router;
