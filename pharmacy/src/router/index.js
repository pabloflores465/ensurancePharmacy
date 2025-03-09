import { createRouter, createWebHistory } from 'vue-router';
import Home from '../pages/Home.vue'; // Ajustar rutas relativas
import Login from '../pages/Login.vue';
import Dashboard from '../pages/Dashboard.vue';
import Register from '../pages/Register.vue';
import Catalogo from '../pages/Catalogo.vue';
import Aseguradoras from '@/pages/Aseguradoras.vue';
import Ofertas from '@/pages/Ofertas.vue';
import ProductoDetalle from '@/pages/ProductoDetalle.vue'; // new import
import Receta from '@/components/Receta.vue';
import CreateProduct from '@/pages/CreateProduct.vue';
import Prescriptions from '@/pages/Prescriptions.vue';
import { authService } from '@/services/authService';

// Rutas para usuarios públicos y autenticados
const userRoutes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/catalogo', component: Catalogo },
  { path: '/aseguradoras', component: Aseguradoras },
  { path: '/ofertas', component: Ofertas },
  { path: '/producto/:id', name: 'ProductoDetalle', component: ProductoDetalle },
  { path: '/receta', component: Receta },
  { path: '/create-product', name: 'CreateProduct', component: CreateProduct },
  { path: '/prescriptions', name: 'Prescriptions', component: Prescriptions },
  { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } }
];

// Rutas exclusivas para administradores
const adminRoutes = [
  {
    path: '/admin/create-product',
    name: 'AdminCreateProduct',
    component: CreateProduct,
    meta: { admin: true }
  }
];

const routes = [
  ...userRoutes,
  ...adminRoutes
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const adminOnly = to.matched.some(record => record.meta.admin);
  const currentUser = authService.getCurrentUser();

  if (requiresAuth && !currentUser) {
    next('/login');
  } else if (adminOnly) {
    if (currentUser && currentUser.role === 'admin') {
      next();
    } else {
      next('/'); // Redirige o muestra una página de acceso denegado
    }
  } else {
    next();
  }
});

export default router;