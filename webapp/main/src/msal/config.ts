import * as Msal from '@azure/msal-browser'

export const isIE = window.navigator.userAgent.indexOf('MSIE ') > -1 || window.navigator.userAgent.indexOf('Trident/') > -1;

export const b2cPolicies = {
    names: {
        signUpSignIn: "B2C_1_sign-in-or-up",
        resetPassword: "b2c_1_reset"
    },
    authorities: {
        signUpSignIn: {
            authority: "https://onlexnet.b2clogin.com/f5230e02-babc-4b9d-ab7f-e76af49d1e5d/B2C_1_sign-in-or-up"
        },
        resetPassword: {
            authority: "https://xx.b2clogin.com/xx.onmicrosoft.com/b2c_1_reset"
        }
    }
}

export const apiConfig: { b2cScopes: string[], webApi: string } = {
    b2cScopes: ["https://xx.onmicrosoft.com/xx/read", "https://xx.onmicrosoft.com/xx/write"],
    webApi: 'https://xx.azurewebsites.net'
};


export const msalConfig: Msal.Configuration = {
    auth: {
        clientId: "9027d226-b538-414e-82ea-abfe306461ad",
        authority: b2cPolicies.authorities.signUpSignIn.authority,
        redirectUri: "http://localhost:3000/",
        navigateToLoginRequestUrl: false,
        knownAuthorities: [
            "onlexnet.b2clogin.com"
        ]
    },
    cache: {
        cacheLocation: "sessionStorage",
        storeAuthStateInCookie: isIE,
    },
    system: {
        loggerOptions: {
            loggerCallback: (arg1, arg2, arg3) => {
                console.log(arg2);
            },
            logLevel: Msal.LogLevel.Verbose,
            piiLoggingEnabled: true
        }
    }
}

export const loginRequest: { scopes: string[] } = {
    scopes: ['openid', 'profile', 'offline_access']
};


export const tokenRequest: { scopes: string[] } = {
    scopes: apiConfig.b2cScopes // i.e. [https://fabrikamb2c.onmicrosoft.com/helloapi/demo.read]
};




export const msalconfig2 = {
    policies: b2cPolicies,
    config: msalConfig,
    api: apiConfig,
    apiScopes: apiConfig.b2cScopes,
    loginScopes: loginRequest.scopes
}