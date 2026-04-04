import { useIsAuthenticated, useMsal } from "@azure/msal-react";
import React from "react";
import { Button } from "components/ui/button";
import { getDisplayVersion, getBuildInfo } from '../app/configuration/BuildInfo';
import { clearAuthenticatedUser } from '../app/configuration/ApplicationInsights';
import { applyThemeMode, getStoredThemeMode, setStoredThemeMode, ThemeMode } from '../app/configuration/ThemeMode';

interface Props {
}

export const Debug: React.FC<Props> = () => {
  const a = useMsal();
  const isAuthenticated = useIsAuthenticated();
  const buildInfo = getBuildInfo();
  const [themeMode, setThemeModeState] = React.useState<ThemeMode>(() => getStoredThemeMode());

  const handleLogout = () => {
    // Clear authenticated user context in Application Insights
    clearAuthenticatedUser();
    a.instance.logout();
  };

  const handleThemeChange = (mode: ThemeMode) => {
    setStoredThemeMode(mode);
    applyThemeMode(mode);
    setThemeModeState(mode);
  };

  return (
    <div className="space-y-4">
      <p className="text-muted-foreground">You are running this application in <b>{process.env.NODE_ENV}</b> mode!.</p>

      <div className="space-y-2">
        <p className="font-medium">Motyw aplikacji</p>
        <div className="flex flex-wrap gap-2">
          <Button type="button" variant={themeMode === "light" ? "default" : "outline"} onClick={() => handleThemeChange("light")}>Light</Button>
          <Button type="button" variant={themeMode === "dark" ? "default" : "outline"} onClick={() => handleThemeChange("dark")}>Dark</Button>
          <Button type="button" variant={themeMode === "system" ? "secondary" : "outline"} onClick={() => handleThemeChange("system")}>System</Button>
        </div>
      </div>

      {isAuthenticated && <Button onClick={handleLogout}>Logout</Button>}
      <p>version: {getDisplayVersion()}
        {buildInfo.buildVersion && <span className="text-muted-foreground"> (package: {buildInfo.version})</span>}
      </p>
    </div>
  );
};





