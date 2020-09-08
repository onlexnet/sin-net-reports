import React from "react";
import { useUserContextQuery } from '../Components/.generated/components'


interface Props {
}

export const Debug: React.FC<Props> = () => {
  // const client = apolloClientFactory(accountInfo.jwtIdToken);
  // const { data, loading, error } = useUserContextQuery({ client });
  const { data, loading, error } = useUserContextQuery();

  if (data) return (<>
    {data.getPrincipal.name}
  </>)

  return (
    <>
      <p>data: {String(data)}</p>
      <p>loading: {String(loading)}</p>
      <p>error: {String(error)}</p>

      <p>DEBUG</p>
    </>
  );
};
