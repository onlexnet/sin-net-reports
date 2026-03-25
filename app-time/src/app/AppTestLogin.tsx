import React, { useState } from "react";
import { Button } from "components/ui/button";
import { Input } from "components/ui/input";
import { getDisplayVersion } from './configuration/BuildInfo';

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
    <div className="mt-12 flex flex-col items-center gap-6">
      <h2 className="text-2xl font-semibold">Test Login</h2>
      <div className="w-[300px] flex flex-col gap-2">
        <label className="text-sm font-medium">Email Address</label>
        <Input
          type="email"
          placeholder="Enter your email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          onKeyDown={handleKeyPress}
          disabled={loading}
        />
        <Button
          onClick={handleLogin}
          disabled={loading}
          className="w-full"
        >
          {loading ? "Logowanie..." : "Login"}
        </Button>
      </div>
      <span className="text-sm text-muted-foreground">{getDisplayVersion()}</span>
    </div>
  );
};
