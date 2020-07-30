import gql from 'graphql-tag';
import * as ApolloReactCommon from '@apollo/react-common';
import * as ApolloReactHooks from '@apollo/react-hooks';
export type Maybe<T> = T | null;
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
};

export type Mutation = {
   __typename?: 'Mutation';
  addService: ServiceModel;
};


export type MutationAddServiceArgs = {
  entry?: Maybe<ServiceEntry>;
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
};


export type ServicesSearchArgs = {
  filter?: Maybe<ServicesFilter>;
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

export type GetServicesQueryVariables = {};


export type GetServicesQuery = (
  { __typename?: 'Query' }
  & { Services: (
    { __typename?: 'Services' }
    & { search: (
      { __typename?: 'ServicesSearchResult' }
      & Pick<ServicesSearchResult, 'totalDistance'>
      & { items: Array<(
        { __typename?: 'ServiceModel' }
        & Pick<ServiceModel, 'whenProvided' | 'forWhatCustomer'>
      )> }
    ) }
  ) }
);


export const GetServicesDocument = gql`
    query getServices {
  Services {
    search {
      items {
        whenProvided
        forWhatCustomer
      }
      totalDistance
    }
  }
}
    `;

/**
 * __useGetServicesQuery__
 *
 * To run a query within a React component, call `useGetServicesQuery` and pass it any options that fit your needs.
 * When your component renders, `useGetServicesQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useGetServicesQuery({
 *   variables: {
 *   },
 * });
 */
export function useGetServicesQuery(baseOptions?: ApolloReactHooks.QueryHookOptions<GetServicesQuery, GetServicesQueryVariables>) {
        return ApolloReactHooks.useQuery<GetServicesQuery, GetServicesQueryVariables>(GetServicesDocument, baseOptions);
      }
export function useGetServicesLazyQuery(baseOptions?: ApolloReactHooks.LazyQueryHookOptions<GetServicesQuery, GetServicesQueryVariables>) {
          return ApolloReactHooks.useLazyQuery<GetServicesQuery, GetServicesQueryVariables>(GetServicesDocument, baseOptions);
        }
export type GetServicesQueryHookResult = ReturnType<typeof useGetServicesQuery>;
export type GetServicesLazyQueryHookResult = ReturnType<typeof useGetServicesLazyQuery>;
export type GetServicesQueryResult = ApolloReactCommon.QueryResult<GetServicesQuery, GetServicesQueryVariables>;