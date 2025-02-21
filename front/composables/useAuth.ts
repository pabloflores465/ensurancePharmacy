interface permission {
  rol: "admin" | "user" | "guest" | "employee" | "extern";
  action: "create" | "read" | "update" | "delete";
  resource: string;
  additional?: () => boolean[];
}

const permissionSystem: Ref<permission[]> = ref([
  {
    rol: "guest",
    action: "read",
    resource: "signup",
  },
  {
    rol: "guest",
    action: "read",
    resource: "login",
  },
  {
    rol: "admin",
    action: "create",
    resource: "user",
  },
]);

const userRole = ref("guest");

export const setUser = (rol: string) => {
  userRole.value = rol;
  if (import.meta.client) {
    localStorage.setItem("rol", rol);
  }
};

export const getUser = () => {
  if (import.meta.client) {
    const storedRol = localStorage.getItem("rol");
    if (storedRol) {
      userRole.value = storedRol;
    }
  }
  return userRole;
};

export const useAuth = (
  action: "create" | "read" | "update" | "delete",
  resource: string,
) =>
  computed(() => {
    let found = false;
    for (const currentPermission of permissionSystem.value) {
      if (
        currentPermission.action === action &&
        currentPermission.resource === resource
      ) {
        found = true;
        if (currentPermission.rol === getUser().value) {
          return true;
        }
      }
    }
    if (!found) {
      console.log("not found");
      return false;
    }
    console.log("no permission");
    return false;
  });
