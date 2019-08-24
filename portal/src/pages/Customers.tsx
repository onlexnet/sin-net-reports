import React from "react";
import { Button, Card, Elevation } from "@blueprintjs/core";
import gql from "graphql-tag";
import { Query, QueryResult } from "react-apollo";

const GET_CUSTOMERS_QUERY = gql`
  query {
    customers {
      id
    }
  }
`;

interface IData {
  customers: [{ id: string; __typename: string }];
}

export const Customers: React.FC = props => {
  return (
    <div>
      <Card interactive={true} elevation={Elevation.TWO}>
        <p>Card content</p>
        <Button>Submit</Button>
      </Card>
      <Query query={GET_CUSTOMERS_QUERY}>
        {(result: QueryResult<IData>) => {
          const { data, loading, error } = result;

          if (loading) return <div>loading ...</div>;
          if (error) return <div>Error</div>;

          return <div>{JSON.stringify(data)}</div>;
        }}
      </Query>
    </div>
  );
};
