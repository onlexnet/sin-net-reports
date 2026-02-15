import React, { useState } from "react";
import { Button, Input, Space, Typography, Form } from "antd";
import { getDisplayVersion } from './configuration/BuildInfo';

const { Text } = Typography;

export const View: React.FC<{ login: (email: string) => void }> = ({ login }) => {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = () => {
    if (!email || !email.includes("@")) {
      alert("Please enter a valid email address");
      return;
    }
    setLoading(true);
    try {
      login(email);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleLogin();
    }
  };

  return (
    <Space direction="vertical" align="center" size="large" style={{ marginTop: "50px" }}>
      <Typography.Title level={2}>Test Login</Typography.Title>
      <Form layout="vertical" style={{ width: "300px" }}>
        <Form.Item label="Email Address" required>
          <Input
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onKeyPress={handleKeyPress}
            disabled={loading}
          />
        </Form.Item>
        <Form.Item>
          <Button 
            type="primary" 
            onClick={handleLogin}
            loading={loading}
            block
          >
            Login
          </Button>
        </Form.Item>
      </Form>
      <Text type="secondary">{getDisplayVersion()}</Text>
    </Space>
  );
};
