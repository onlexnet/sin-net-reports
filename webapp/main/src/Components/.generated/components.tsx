import gql from 'graphql-tag';
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

export type Query = {
   __typename?: 'Query';
  getServices: Array<Maybe<ServiceModel>>;
};


export type QueryGetServicesArgs = {
  filter?: Maybe<ServicesFilter>;
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

export type ServicesFilter = {
  whenProvided?: Maybe<Scalars['String']>;
  forWhatCustomer?: Maybe<Scalars['String']>;
};

export type Subscription = {
   __typename?: 'Subscription';
  time?: Maybe<Scalars['Int']>;
};

export type Unnamed_1_QueryVariables = {};


export type Unnamed_1_Query = (
  { __typename?: 'Query' }
  & { getServices: Array<Maybe<(
    { __typename?: 'ServiceModel' }
    & Pick<ServiceModel, 'whenProvided' | 'forWhatCustomer'>
  )>> }
);

