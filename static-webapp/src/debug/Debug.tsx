import { useIsAuthenticated, useMsal } from "@azure/msal-react";
import { Button, Typography, Layout } from "antd";
import React from "react";
import { getDisplayVersion, getBuildInfo } from '../app/configuration/BuildInfo';

const { Text } = Typography;
const { Content } = Layout;

interface Props {
}

export const Debug: React.FC<Props> = () => {
  const a = useMsal();
  const isAuthenticated = useIsAuthenticated();
  const buildInfo = getBuildInfo();

  return (
    <Content>
      <Text type="secondary">You are running this application in <b>{process.env.NODE_ENV}</b> mode.</Text>
      {isAuthenticated && <Button onClick={() => a.instance.logout()}>Logout</Button>}
      <Text>version: {getDisplayVersion()}</Text>
      {buildInfo.buildVersion && <Text type="secondary"> (package: {buildInfo.version})</Text>}
    </Content>
  );
};





