"""
TBD
"""
from __future__ import absolute_import

# Mutation to support JWT.
# more info https://github.com/flavors/django-graphql-jwt/
import graphene
import graphql_jwt

from timesheets.schema import Query as TimesheetsQuery, Mutation as TimesheetsMutation
from users.schema import Query as UsersQuery, Mutation as UsersMutation


class Query(TimesheetsQuery, UsersQuery):
    pass


class JwtMutation(graphene.ObjectType):
    token_auth = graphql_jwt.ObtainJSONWebToken.Field()
    verify_token = graphql_jwt.Verify.Field()
    refresh_token = graphql_jwt.Refresh.Field()


class Mutation(TimesheetsMutation, UsersMutation, JwtMutation):
    pass


schema = graphene.Schema(query=Query, mutation=Mutation)
