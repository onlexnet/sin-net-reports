import { useIsAuthenticated, useMsal } from "@azure/msal-react";
import { Button, Typography, Layout } from "antd";
import React from "react";
import { getDisplayVersion, getBuildInfo } from '../app/configuration/BuildInfo';
import { clearAuthenticatedUser } from '../app/configuration/ApplicationInsights';

const { Text } = Typography;
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
      <Text type="secondary">You are running this application in <b>{process.env.NODE_ENV}</b> mode!.</Text>
      {isAuthenticated && <Button onClick={handleLogout}>Logout</Button>}
      <Text>version: {getDisplayVersion()}</Text>
      {buildInfo.buildVersion && <Text type="secondary"> (package: {buildInfo.version})</Text>}
    </Content>
  );
};





