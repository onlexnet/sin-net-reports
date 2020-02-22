import React from "react";
import { Query } from "react-apollo";
import gql from "graphql-tag";
import { Redirect } from "react-router";

export const HOME_QUERY = gql`
  query {
    isLoggedIn @client
  }
`;

interface IData {
  isLoggedIn: boolean;
}

const Home: React.FC = props => {
  return (
    <Query<IData, {}> query={HOME_QUERY}>
      {({ data, loading, error }) => {
        if (loading) return;
        if (error) return;

        if (data!.isLoggedIn) return <Redirect to="/main" />;
        return <Redirect to="/auth" />;
      }}
    </Query>
  );
};

export default Home;
