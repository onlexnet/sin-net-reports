import { PublicClientApplication, SilentRequest, AuthenticationResult, Configuration, LogLevel, AccountInfo, InteractionRequiredAuthError, EndSessionRequest, RedirectRequest, PopupRequest } from "@azure/msal-browser";
import { store } from "../store/store";
import { initiateSession } from "../store/session/actions";
import { SignInFlow, InitiateSessionFinishedAction, INITIATE_SESSION_FINISHED } from "../store/session/types";
import { graphQlClient } from "../api";

/**
 * Configuration class for @azure/msal-browser: 
 * https://azuread.github.io/microsoft-authentication-library-for-js/ref/msal-browser/modules/_src_config_configuration_.html
 */
const MSAL_CONFIG: Configuration = {
    auth: {
        clientId: "36305176-2249-4ce5-8d59-a91dd7363610", // sinnetapp-prod
        authority: "https://sinnetapp.b2clogin.com/7c86200b-9308-4ebc-a462-fab0a67b91e6/B2C_1_sign-in-or-up",
        navigateToLoginRequestUrl: false,
        knownAuthorities: [
            "sinnetapp.b2clogin.com"
        ]
    },
    cache: {
        cacheLocation: "sessionStorage", // This configures where your cache will be stored
        storeAuthStateInCookie: false, // Set this to "true" if you are having issues on IE11 or Edge
    },
    system: {
        loggerOptions: {
            loggerCallback: (level, message, containsPii) => {
                if (containsPii) {
                    return;
                }
                switch (level) {
                    case LogLevel.Error:
                        console.error(message);
                        return;
                    case LogLevel.Info:
                        console.info(message);
                        return;
                    case LogLevel.Verbose:
                        console.debug(message);
                        return;
                    case LogLevel.Warning:
                        console.warn(message);
                        return;
                }
            }
        }
    }
};

/**
 * AuthModule for application - handles authentication in app.
 */
export class AuthModule {

    private msal: PublicClientApplication; // https://azuread.github.io/microsoft-authentication-library-for-js/ref/msal-browser/classes/_src_app_publicclientapplication_.publicclientapplication.html
    private account: AccountInfo | null; // https://azuread.github.io/microsoft-authentication-library-for-js/ref/msal-common/modules/_src_account_accountinfo_.html
    private loginRedirectRequest: RedirectRequest; // TODO: Publish ref docs for RedirectRequest
    private loginRequest: PopupRequest; // https://azuread.github.io/microsoft-authentication-library-for-js/ref/msal-common/modules/_src_request_authorizationurlrequest_.html
    private profileRedirectRequest: RedirectRequest;
    private profileRequest: PopupRequest;
    private mailRedirectRequest: RedirectRequest;
    private mailRequest: PopupRequest;
    private appRedirectRequest: RedirectRequest;
    private appRequest: PopupRequest;

    constructor() {

        this.msal = new PublicClientApplication(MSAL_CONFIG);
        this.account = null;

        this.loginRequest = {
            scopes: ["https://sinnetapp.onmicrosoft.com/psa/Actions.Read"]
        };

        this.loginRedirectRequest = {
            ...this.loginRequest,
            redirectStartPage: window.location.href
        };

        this.profileRequest = {
            scopes: ["User.Read"]
        };

        this.profileRedirectRequest = {
            ...this.profileRequest,
            redirectStartPage: window.location.href
        };

        // Add here scopes for access token to be used at MS Graph API endpoints.
        this.mailRequest = {
            scopes: ["Mail.Read"]
        };

        this.mailRedirectRequest = {
            ...this.mailRequest,
            redirectStartPage: window.location.href
        };

        this.appRequest = {
            scopes: ["Actions.Read"]
        };

        this.appRedirectRequest = {
            ...this.appRequest,
            redirectStartPage: window.location.href
        };
    }

    /**
     * Calls getAllAccounts and determines the correct account to sign into, currently defaults to first account found in cache.
     * TODO: Add account chooser code
     * 
     * https://github.com/AzureAD/microsoft-authentication-library-for-js/blob/dev/lib/msal-common/docs/Accounts.md
     */
    private getAccount(): AccountInfo | null {
        // need to call getAccount here?
        const currentAccounts = this.msal.getAllAccounts();
        if (currentAccounts === null) {
            console.log("No accounts detected");
            return null;
        }

        if (currentAccounts.length > 1) {
            // Add choose account code here
            console.log("Multiple accounts detected, need to add choose account code.");
            return currentAccounts[0];
        } else if (currentAccounts.length === 1) {
            return currentAccounts[0];
        }

        return null;
    }

    /**
     * Checks whether we are in the middle of a redirect and handles state accordingly. Only required for redirect flows.
     * 
     * https://github.com/AzureAD/microsoft-authentication-library-for-js/blob/dev/lib/msal-browser/docs/initialization.md#redirect-apis
     */
    loadAuthModule(): void {
        this.msal.handleRedirectPromise().then((resp: AuthenticationResult | null) => {
            this.handleResponseInternal(resp);
        }).catch(console.error);
    }

    /**
     * Handles the response from a popup or redirect. If response is null, will check if we have any accounts and attempt to sign in.
     * @param response 
     */
    private handleResponseInternal(resp: AuthenticationResult | null) {
        const msg = 'handleResponseInternal: ' + JSON.stringify(resp);
        console.log(msg);

        if (resp) {
            const action: InitiateSessionFinishedAction = {
                type: INITIATE_SESSION_FINISHED,
                jwtToken: resp.accessToken ?? "undefined1",
                email: resp.account.username
            }
            store.dispatch(action);
            graphQlClient.setHeader("Authorization", `Bearer ${resp.accessToken}`);
        }

        if (resp?.account) {
            this.account = resp.account;
        } else {
            this.account = this.getAccount();
        }

        if (this.account) {
            // alert('test1');
            // this.getAppTokenRedirect()
            // .then(it => {
            //     alert(2);
            // })
            // .catch(ex => {
            //     alert(3);
            //     console.log(ex);
            // })
        } else {
            // store.dispatch(updateSession({
            //     loggedIn: false
            // }));
        }
    }

    login = () => {
        this.msal.loginRedirect(this.loginRedirectRequest);
    }

    /**
     * Logs out of current account.
     */
    logout(): void {
        if (!this.account) return;

        const logOutRequest: EndSessionRequest = {
            account: this.account
        };

        this.msal.logout(logOutRequest);
    }

    /**
     * Gets the token to read user profile data from MS Graph silently, or falls back to interactive redirect.
     */
    async getProfileTokenRedirect(): Promise<string> {
        if (this.account === null) return Promise.reject();

        var silentProfileRequest = {
            scopes: ["openid", "profile", "User.Read"],
            account: this.account,
            forceRefresh: false
        };

        return this.getTokenRedirect(silentProfileRequest, this.profileRedirectRequest);
    }

    async getAppTokenRedirect(): Promise<string> {
        if (this.account === null) return Promise.reject();

        const silentRequest = {
            scopes: ["openid", "profile", "https://onlexnet.onmicrosoft.com/posesor/Actions.Read"],
            account: this.account,
            forceRefresh: true
        };

        return this.getTokenRedirect(silentRequest, this.appRedirectRequest);
    }

    /**
     * Gets the token to read user profile data from MS Graph silently, or falls back to interactive popup.
     */
    async getProfileTokenPopup(): Promise<string> {
        if (this.account === null) return Promise.reject();

        var silentProfileRequest = {
            scopes: ["openid", "profile", "User.Read"],
            account: this.account,
            forceRefresh: false
        };

        return this.getTokenPopup(silentProfileRequest, this.profileRequest);
    }

    /**
     * Gets the token to read mail data from MS Graph silently, or falls back to interactive redirect.
     */
    async getMailTokenRedirect(): Promise<string> {
        if (this.account === null) return Promise.reject();

        const silentMailRequest = {
            scopes: ["openid", "profile", "Mail.Read"],
            account: this.account,
            forceRefresh: false
        };
        return this.getTokenRedirect(silentMailRequest, this.mailRedirectRequest);
    }

    /**
     * Gets the token to read mail data from MS Graph silently, or falls back to interactive popup.
     */
    async getMailTokenPopup(): Promise<string> {
        if (this.account === null) return Promise.reject();

        const silentMailRequest = {
            scopes: ["openid", "profile", "Mail.Read"],
            account: this.account,
            forceRefresh: false
        };
        return this.getTokenPopup(silentMailRequest, this.mailRequest);
    }

    /**
     * Gets a token silently, or falls back to interactive popup.
     */
    private async getTokenPopup(silentRequest: SilentRequest, interactiveRequest: PopupRequest): Promise<string> {
        try {
            const response: AuthenticationResult = await this.msal.acquireTokenSilent(silentRequest);
            return response.accessToken;
        } catch (e) {
            console.log("silent token acquisition fails.");
            if (e instanceof InteractionRequiredAuthError) {
                console.log("acquiring token using redirect");
                return this.msal.acquireTokenPopup(interactiveRequest).then((resp) => {
                    return resp.accessToken;
                }).catch((err) => {
                    console.error(err);
                    return Promise.reject();
                });
            } else {
                console.error(e);
                return Promise.reject();
            }
        }
    }

    /**
     * Gets a token silently, or falls back to interactive redirect.
     */
    private async getTokenRedirect(silentRequest: SilentRequest, interactiveRequest: RedirectRequest): Promise<string> {
        try {
            const response = await this.msal.acquireTokenSilent(silentRequest);
            return response.accessToken;
        } catch (e) {
            console.log("silent token acquisition fails.");
            if (e instanceof InteractionRequiredAuthError) {
                console.log("acquiring token using redirect");
                this.msal.acquireTokenRedirect(interactiveRequest).catch(console.error);
            } else {
                console.error(e);
            }
            return Promise.reject();
        }
    }
}
