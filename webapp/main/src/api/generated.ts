import { GraphQLClient } from 'graphql-request';
import { print } from 'graphql';
import gql from 'graphql-tag';
export type Maybe<T> = T | null;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
  Date: any;
};


export type Mutation = {
  __typename?: 'Mutation';
  Services: ServicesOperations;
};

export type PrincipalModel = {
  __typename?: 'PrincipalModel';
  name?: Maybe<Scalars['String']>;
};

export type Query = {
  __typename?: 'Query';
  getPrincipal: PrincipalModel;
  Services: Services;
};

export type ServiceEntry = {
  whenProvided: Scalars['Date'];
  forWhatCustomer: Scalars['ID'];
  description: Scalars['String'];
};

export type ServiceModel = {
  __typename?: 'ServiceModel';
  servicemanName: Scalars['String'];
  whenProvided: Scalars['Date'];
  forWhatCustomer: Scalars['String'];
};

export type Services = {
  __typename?: 'Services';
  search: ServicesSearchResult;
};


export type ServicesSearchArgs = {
  filter?: Maybe<ServicesFilter>;
};

export type ServicesFilter = {
  from?: Maybe<Scalars['Date']>;
  to?: Maybe<Scalars['Date']>;
};

export type ServicesOperations = {
  __typename?: 'ServicesOperations';
  addNew: Scalars['Boolean'];
};


export type ServicesOperationsAddNewArgs = {
  entry?: Maybe<ServiceEntry>;
};

export type ServicesSearchResult = {
  __typename?: 'ServicesSearchResult';
  items: Array<ServiceModel>;
  totalDistance: Scalars['Int'];
};

export type Subscription = {
  __typename?: 'Subscription';
  time?: Maybe<Scalars['Int']>;
};

export type FetchServicesQueryVariables = Exact<{
  from: Scalars['Date'];
  to: Scalars['Date'];
}>;


export type FetchServicesQuery = (
  { __typename?: 'Query' }
  & { Services: (
    { __typename?: 'Services' }
    & { search: (
      { __typename?: 'ServicesSearchResult' }
      & { items: Array<(
        { __typename?: 'ServiceModel' }
        & Pick<ServiceModel, 'whenProvided' | 'forWhatCustomer'>
      )> }
    ) }
  ) }
);

export type NewServiceActionMutationVariables = Exact<{
  when: Scalars['Date'];
  what: Scalars['String'];
}>;


export type NewServiceActionMutation = (
  { __typename?: 'Mutation' }
  & { Services: (
    { __typename?: 'ServicesOperations' }
    & Pick<ServicesOperations, 'addNew'>
  ) }
);


export const FetchServicesDocument = gql`
    query FetchServices($from: Date!, $to: Date!) {
  Services {
    search(filter: {from: $from, to: $to}) {
      items {
        whenProvided
        forWhatCustomer
      }
    }
  }
}
    `;
export const NewServiceActionDocument = gql`
    mutation newServiceAction($when: Date!, $what: String!) {
  Services {
    addNew(entry: {whenProvided: $when, forWhatCustomer: "GFT", description: $what})
  }
}
    `;

export type SdkFunctionWrapper = <T>(action: () => Promise<T>) => Promise<T>;


const defaultWrapper: SdkFunctionWrapper = sdkFunction => sdkFunction();
export function getSdk(client: GraphQLClient, withWrapper: SdkFunctionWrapper = defaultWrapper) {
  return {
    FetchServices(variables: FetchServicesQueryVariables): Promise<FetchServicesQuery> {
      return withWrapper(() => client.request<FetchServicesQuery>(print(FetchServicesDocument), variables));
    },
    newServiceAction(variables: NewServiceActionMutationVariables): Promise<NewServiceActionMutation> {
      return withWrapper(() => client.request<NewServiceActionMutation>(print(NewServiceActionDocument), variables));
    }
  };
}
export type Sdk = ReturnType<typeof getSdk>;