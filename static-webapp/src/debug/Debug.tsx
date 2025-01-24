import { useIsAuthenticated, useMsal } from "@azure/msal-react";
import { Button, Typography, Layout } from "antd";
import React from "react";
import packageJson from '../../package.json';

const { Text } = Typography;
const { Content } = Layout;

interface Props {
}

export const Debug: React.FC<Props> = () => {
  const a = useMsal();
  const isAuthenticated = useIsAuthenticated();

  return (
    <Content>
      <Text type="secondary">You are running this application in <b>{process.env.NODE_ENV}</b> mode.</Text>
      {isAuthenticated && <Button onClick={() => a.instance.logout()}>Logout</Button>}
      <Text>version: {packageJson.version}</Text>
    </Content>
  );
};





