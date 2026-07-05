export type ThemeMode = "system" | "light" | "dark";

const THEME_STORAGE_KEY = "app-theme-mode";

const isThemeMode = (value: string | null): value is ThemeMode => {
  return value === "system" || value === "light" || value === "dark";
};

const resolveThemeMode = (mode: ThemeMode): "light" | "dark" => {
  if (mode === "dark" || mode === "light") {
    return mode;
  }

  if (typeof window !== "undefined" && window.matchMedia?.("(prefers-color-scheme: dark)").matches) {
    return "dark";
  }

  return "light";
};

export const getStoredThemeMode = (): ThemeMode => {
  if (typeof window === "undefined") {
    return "system";
  }

  const storedValue = window.localStorage.getItem(THEME_STORAGE_KEY);
  return isThemeMode(storedValue) ? storedValue : "system";
};

export const setStoredThemeMode = (mode: ThemeMode): void => {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(THEME_STORAGE_KEY, mode);
};

export const applyThemeMode = (mode: ThemeMode): "light" | "dark" => {
  const resolvedTheme = resolveThemeMode(mode);

  if (typeof document !== "undefined") {
    if (mode === "system") {
      document.documentElement.removeAttribute("data-theme");
    } else {
      document.documentElement.dataset.theme = mode;
    }

    document.documentElement.style.colorScheme = resolvedTheme;
  }

  return resolvedTheme;
};
