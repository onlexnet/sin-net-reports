import { AuthModule } from "./AuthModule";

export const authModule: AuthModule = new AuthModule();

// Load auth module when browser window loads. Only required for redirect flows.
window.addEventListener("load", async () => {
  authModule.loadAuthModule();
});

