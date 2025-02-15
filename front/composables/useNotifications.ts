export const useNotifications = ()=> useState("notifications", (): {
  type?: "loading"|"success"|"error";
  title?: string;
  description?: string;
}[] =>([]))