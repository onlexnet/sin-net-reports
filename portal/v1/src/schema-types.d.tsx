import gql from "graphql-tag";
export type Maybe<T> = T | null;
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
  /** The `Date` scalar type represents a Date
   * value as specified by
   * [iso8601](https://en.wikipedia.org/wiki/ISO_8601).
   */
  Date: any;
  /** The `DateTime` scalar type represents a DateTime
   * value as specified by
   * [iso8601](https://en.wikipedia.org/wiki/ISO_8601).
   */
  DateTime: any;
  /** The `GenericScalar` scalar type represents a generic
   * GraphQL scalar value that could be:
   * String, Boolean, Int, Float, List or Object.
   */
  GenericScalar: any;
};

/** Defines GraphQL mutation to create a new Customer. */
export type CreateCustomer = {
  __typename?: "CreateCustomer";
  customer?: Maybe<CustomerType>;
};

export type CreateTimesheet = {
  __typename?: "CreateTimesheet";
  timesheet?: Maybe<TimesheetType>;
};

export type CreateUser = {
  __typename?: "CreateUser";
  user?: Maybe<UserType>;
};

/** Custom data */
export type CustomerType = {
  __typename?: "CustomerType";
  id: Scalars["ID"];
  name: Scalars["String"];
  createdAt: Scalars["DateTime"];
  createdBy: UserType;
};

export type Mutation = {
  __typename?: "Mutation";
  /** Obtain JSON Web Token mutation */
  tokenAuth?: Maybe<ObtainJsonWebToken>;
  verifyToken?: Maybe<Verify>;
  refreshToken?: Maybe<Refresh>;
  createUser?: Maybe<CreateUser>;
  createTimesheet?: Maybe<CreateTimesheet>;
  updateTimesheet?: Maybe<UpdateTimesheet>;
  /** Defines GraphQL mutation to create a new Customer. */
  createCustomer?: Maybe<CreateCustomer>;
};

export type MutationTokenAuthArgs = {
  username: Scalars["String"];
  password: Scalars["String"];
};

export type MutationVerifyTokenArgs = {
  token: Scalars["String"];
};

export type MutationRefreshTokenArgs = {
  token: Scalars["String"];
};

export type MutationCreateUserArgs = {
  password?: Maybe<Scalars["String"]>;
  userName?: Maybe<Scalars["String"]>;
};

export type MutationCreateTimesheetArgs = {
  customerId?: Maybe<Scalars["Int"]>;
  description?: Maybe<Scalars["String"]>;
  distanceInKms?: Maybe<Scalars["Int"]>;
  timeInMins?: Maybe<Scalars["Int"]>;
  when?: Maybe<Scalars["Date"]>;
};

export type MutationUpdateTimesheetArgs = {
  description?: Maybe<Scalars["String"]>;
  id?: Maybe<Scalars["Int"]>;
};

export type MutationCreateCustomerArgs = {
  name?: Maybe<Scalars["String"]>;
};

/** Obtain JSON Web Token mutation */
export type ObtainJsonWebToken = {
  __typename?: "ObtainJSONWebToken";
  token?: Maybe<Scalars["String"]>;
};

export type Query = {
  __typename?: "Query";
  user?: Maybe<UserType>;
  me?: Maybe<UserType>;
  timesheets?: Maybe<Array<Maybe<TimesheetType>>>;
  customers?: Maybe<Array<Maybe<CustomerType>>>;
};

export type QueryUserArgs = {
  id: Scalars["Int"];
};

export type Refresh = {
  __typename?: "Refresh";
  token?: Maybe<Scalars["String"]>;
  payload?: Maybe<Scalars["GenericScalar"]>;
};

/** Some descriptive info */
export type TimesheetType = {
  __typename?: "TimesheetType";
  id: Scalars["ID"];
  description: Scalars["String"];
  timeInMins: Scalars["Int"];
  distanceInKms: Scalars["Int"];
  when: Scalars["Date"];
  createdAt: Scalars["DateTime"];
  createdBy: UserType;
};

export type UpdateTimesheet = {
  __typename?: "UpdateTimesheet";
  updated?: Maybe<TimesheetType>;
};

export type UserType = {
  __typename?: "UserType";
  id: Scalars["ID"];
  /** Required. 150 characters or fewer. Letters, digits and @/./+/-/_ only. */
  username: Scalars["String"];
  email: Scalars["String"];
};

export type Verify = {
  __typename?: "Verify";
  payload?: Maybe<Scalars["GenericScalar"]>;
};
export type Unnamed_1_MutationVariables = {
  username: Scalars["String"];
  password: Scalars["String"];
};

export type Unnamed_1_Mutation = { __typename?: "Mutation" } & {
  tokenAuth: Maybe<
    { __typename?: "ObtainJSONWebToken" } & Pick<ObtainJsonWebToken, "token">
  >;
};

export type Unnamed_2_MutationVariables = {
  username: Scalars["String"];
  password?: Maybe<Scalars["String"]>;
};

export type Unnamed_2_Mutation = { __typename?: "Mutation" } & {
  createUser: Maybe<
    { __typename?: "CreateUser" } & {
      user: Maybe<{ __typename?: "UserType" } & Pick<UserType, "id">>;
    }
  >;
};

export type Unnamed_3_QueryVariables = {};

export type Unnamed_3_Query = { __typename?: "Query" } & {
  customers: Maybe<
    Array<Maybe<{ __typename?: "CustomerType" } & Pick<CustomerType, "id">>>
  >;
};
