import React from "react";



interface Props {
}

export const Debug: React.FC<Props> = () => {
  // const client = apolloClientFactory(accountInfo.jwtIdToken);
  //const { data, loading, error } = useGetServicesQuery({ client });
  return (
    // <ApolloProvider client={client}>
    //   <p>data: {String(data)}</p>
    //   <p>loading: {String(loading)}</p>
    //   <p>error: {String(error)}</p>

    // </ApolloProvider>
    <p>DEBUG</p>
  );
};
