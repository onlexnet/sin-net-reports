import { applyThemeMode, getStoredThemeMode, setStoredThemeMode } from "./ThemeMode";

describe("ThemeMode", () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.removeAttribute("data-theme");
  });

  it("persists the selected theme mode and applies it to the document", () => {
    setStoredThemeMode("dark");

    expect(getStoredThemeMode()).toBe("dark");

    applyThemeMode("dark");
    expect(document.documentElement.dataset.theme).toBe("dark");
  });

  it("falls back to system mode for invalid stored values", () => {
    localStorage.setItem("app-theme-mode", "unexpected-value");

    expect(getStoredThemeMode()).toBe("system");
  });
});
