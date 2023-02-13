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
  whenProvided?: Maybe<Scalars['String']>;
  forWhatCustomer?: Maybe<Scalars['ID']>;
};

export type ServiceModel = {
  __typename?: 'ServiceModel';
  whenProvided?: Maybe<Scalars['String']>;
  forWhatCustomer?: Maybe<Scalars['String']>;
};

export type Services = {
  __typename?: 'Services';
  search: ServicesSearchResult;
  add: ServiceModel;
};


export type ServicesSearchArgs = {
  filter?: Maybe<ServicesFilter>;
};


export type ServicesAddArgs = {
  entry?: Maybe<ServiceEntry>;
};

export type ServicesFilter = {
  whenProvided?: Maybe<Scalars['String']>;
  forWhatCustomer?: Maybe<Scalars['String']>;
  onlyMine?: Maybe<Scalars['Boolean']>;
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


export const FetchServicesDocument = gql`
    query FetchServices($period: String!, $customerName: String!) {
  Services {
    search(filter: {whenProvided: $period, forWhatCustomer: $customerName, onlyMine: false}) {
      items {
        whenProvided
        forWhatCustomer
      }
    }
  }
}
    `;

export type SdkFunctionWrapper = <T>(action: () => Promise<T>) => Promise<T>;


const defaultWrapper: SdkFunctionWrapper = sdkFunction => sdkFunction();
export function getSdk(client: GraphQLClient, withWrapper: SdkFunctionWrapper = defaultWrapper) {
  return {
    FetchServices(variables: FetchServicesQueryVariables): Promise<FetchServicesQuery> {
      return withWrapper(() => client.request<FetchServicesQuery>(print(FetchServicesDocument), variables));
    }
  };
}
export type Sdk = ReturnType<typeof getSdk>;