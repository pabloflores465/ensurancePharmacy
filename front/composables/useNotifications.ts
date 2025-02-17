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
  if (!id) {
    notId = notId + 1;
    not.id = notId;
  }
  const notifications = useNotifications();
  setTimeout(() => {}, not.timeout);
};
