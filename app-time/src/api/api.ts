import { ApolloClient, ApolloLink, createHttpLink, DefaultOptions, InMemoryCache } from "@apollo/client";
import { GraphQLClient } from 'graphql-request';
import { addressProvider } from "../addressProvider";

// Lazy getter for GraphQL URL (computed when first accessed, not at module load time)
const getGraphqlUrl = () => `${addressProvider().host}/graphql`;

export const apolloClientFactory = (jwtToken: string, email?: string) => {
  // configuration below is focused on Authentication
  // https://www.apollographql.com/docs/react/networking/authentication/

  const httpLink = createHttpLink({
    uri: getGraphqlUrl(),

    // Cookie
    // If your app is browser based and you are using cookies for login and session management with a backend,
    // it's very easy to tell your network interface to send the cookie along with every request.
    // You just need to pass the credentials option. e.g. credentials: 'same-origin' as shown below,
    // if your backend server is the same domain or else credentials: 'include' if your backend is a different domain.
    // credentials: "",
  });

  const middlewareAuthLink = new ApolloLink((operation, forward) => {
    const headers: any = {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Credentials": true,
      "Access-Control-Allow-Methods": "OPTIONS, GET, POST, PUT, PATCH, DELETE",
      Authorization: `Bearer ${jwtToken}`,
    };
    
    // For test login, add email as X-MS-CLIENT-PRINCIPAL-NAME header
    // This is used by the backend's CustomAuthenticationFilter
    if (email) {
      headers["X-MS-CLIENT-PRINCIPAL-NAME"] = email;
      headers["X-MS-CLIENT-PRINCIPAL-ID"] = email; // Use email as ID as well for testing
    }
    
    operation.setContext({
      headers
    });
    return forward(operation);
  });

  const defaultOptions: DefaultOptions = {
    watchQuery: {
      fetchPolicy: 'no-cache',
      errorPolicy: 'ignore',
    },
    query: {
      fetchPolicy: 'network-only',
      errorPolicy: 'all',
    },
  }
  return new ApolloClient({
    cache: new InMemoryCache({}),
    link: middlewareAuthLink.concat(httpLink),
    connectToDevTools: true,
    defaultOptions
  });
};

// Lazy singleton for GraphQL client
let _graphQlClient: GraphQLClient | null = null;
export const getGraphQlClient = (): GraphQLClient => {
  if (!_graphQlClient) {
    _graphQlClient = new GraphQLClient(getGraphqlUrl());
  }
  return _graphQlClient;
};
