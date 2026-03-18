import React from "react";
import { Button, Space, Typography } from "antd";
import { getDisplayVersion } from './configuration/BuildInfo';

const { Text } = Typography;

export const View: React.FC<{ login: () => void }> = ({ login }) => {
  return (
    <Space direction="vertical" align="center" size="middle">
      <Button type="primary" onClick={login}>Zaloguj się do aplikacji ...</Button>
      <Text>{getDisplayVersion()}</Text>
    </Space>
  );
};
