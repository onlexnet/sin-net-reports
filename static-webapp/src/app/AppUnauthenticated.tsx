import React from "react";
import packageJson from '../../package.json';
import { Button, Space, Typography } from "antd";

const { Text } = Typography;

export const View: React.FC<{ login: () => void }> = ({ login }) => {
  return (
    <Space direction="vertical" align="center" size="middle">
      <Button type="primary" onClick={login}>Zaloguj się do aplikacji ...</Button>
      <Text>{packageJson.version}</Text>
    </Space>
  );
};
