import React from "react";
import { PrimaryButton, Stack } from "office-ui-fabric-react";

export const View: React.FC<{ login: () => void }> = ({ login }) => {
  return (
    <Stack>
      <Stack.Item align="center">
        <PrimaryButton onClick={login}>Zaloguj się do aplikacji ...</PrimaryButton>
      </Stack.Item>
    </Stack>
  );
};
