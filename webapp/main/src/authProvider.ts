import { MsalAuthProvider, LoginType } from "react-aad-msal";
import { LogLevel, Logger } from "msal";

const logger = new Logger(
  (logLevel, message, containsPii) => {
    console.log("[MSAL]", message);
  },
  {
    level: LogLevel.Verbose,
    piiLoggingEnabled: false
  }
);

// The auth provider should be a singleton. Best practice is to only have it ever instantiated once.
// Avoid creating an instance inside the component it will be recreated on each render.
// If two providers are created on the same page it will cause authentication errors.
export const authProvider = new MsalAuthProvider(
  {
    auth: {
      // authority: "https://login.microsoftonline.com/common",
      authority: 'https://onlexnet.b2clogin.com/f5230e02-babc-4b9d-ab7f-e76af49d1e5d/B2C_1_sign-in-or-up',
      // sinnet-prod
      clientId: "9027d226-b538-414e-82ea-abfe306461ad",
      postLogoutRedirectUri: window.location.origin,
      // address is well-known because backend parts is based on Spring with proper MS Azure library
      // to handle redirected request.
      redirectUri: window.location.origin,
      validateAuthority: true,

      // After being redirected to the "redirectUri" page, should user
      // be redirected back to the Url where their login originated from?
      navigateToLoginRequestUrl: true,
      knownAuthorities: ["onlexnet.b2clogin.com"]
    },
    // Enable logging of MSAL events for easier troubleshooting.
    // This should be disabled in production builds.
    system: {
      logger: logger as any
    },
    cache: {
      cacheLocation: "sessionStorage",
      storeAuthStateInCookie: false
    }
  },
  {
    scopes: ["openid", "profile"]
  },
  {
    loginType: LoginType.Redirect,
    // When a token is refreshed it will be done by loading a page in an iframe.
    // Rather than reloading the same page, we can point to an empty html file which will prevent
    // site resources from being loaded twice.
    tokenRefreshUri: window.location.origin + "/auth.html"
  }
);
