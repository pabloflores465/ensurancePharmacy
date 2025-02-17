interface notification {
  id?: number;
  type?: "loading" | "success" | "error";
  title?: string;
  description: string;
  timeout?: number;
}
export const useNotifications = () =>
  useState("notifications", (): notification[] => []);

let notId: number = 0;

export const notify = (
  not: notification = {
    type: "loading",
    title: "",
    description: "",
    timeout: 2000,
  },
): void => {
  if (!not.id) {
    notId = notId + 1;
    not.id = notId;
  }
  const notifications = useNotifications();
  notifications.value.push(not);
  setTimeout(() => {
    notifications.value = notifications.filter((element) =>
      shallowEqual(element, not),
    );
  }, not.timeout);
};

function shallowEqual(obj1, obj2) {
  if (obj1 === obj2) return true;

  if (
    typeof obj1 !== "object" ||
    obj1 === null ||
    typeof obj2 !== "object" ||
    obj2 === null
  ) {
    return false;
  }

  const keys1 = Object.keys(obj1);
  const keys2 = Object.keys(obj2);
  if (keys1.length !== keys2.length) return false;

  for (let key of keys1) {
    if (!obj2.hasOwnProperty(key) || obj1[key] !== obj2[key]) {
      return false;
    }
  }

  return true;
}
