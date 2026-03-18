import { useIsAuthenticated, useMsal } from "@azure/msal-react";
import { Layout } from "antd";
import React from "react";
import { Button } from "components/ui/button";
import { getDisplayVersion, getBuildInfo } from '../app/configuration/BuildInfo';
import { clearAuthenticatedUser } from '../app/configuration/ApplicationInsights';
const { Content } = Layout;

interface Props {
}

export const Debug: React.FC<Props> = () => {
  const a = useMsal();
  const isAuthenticated = useIsAuthenticated();
  const buildInfo = getBuildInfo();

  const handleLogout = () => {
    // Clear authenticated user context in Application Insights
    clearAuthenticatedUser();
    a.instance.logout();
  };

  return (
    <Content>
      <p className="text-muted-foreground">You are running this application in <b>{process.env.NODE_ENV}</b> mode!.</p>
      {isAuthenticated && <Button onClick={handleLogout}>Logout</Button>}
      <p>version: {getDisplayVersion()}
        {buildInfo.buildVersion && <span className="text-muted-foreground"> (package: {buildInfo.version})</span>}
      </p>
    </Content>
  );
};





