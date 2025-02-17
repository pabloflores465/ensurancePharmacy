export const darkMode = () =>
  useState("darkMode", () => {
    if (
      import.meta.client &&
      typeof window !== "undefined" &&
      window.localStorage
    ) {
      const stored = localStorage.getItem("darkMode");
      return stored ? JSON.parse(stored) : false;
    }
    return false;
  });

export const toggleDark = () => {
  const dark = darkMode();
  dark.value = !dark.value;
  if (
    import.meta.client &&
    typeof window !== "undefined" &&
    window.localStorage
  ) {
    localStorage.setItem("darkMode", JSON.stringify(dark.value));
  }
};
