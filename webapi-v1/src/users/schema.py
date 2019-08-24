from django.contrib.auth import get_user_model
from graphene_django import DjangoObjectType
import graphene


class UserType(DjangoObjectType):
    class Meta:
        model = get_user_model()
        only_fields = ("id", "email", "username")


class Query(graphene.ObjectType):
    user = graphene.Field(UserType, id=graphene.Int(required=True))
    me = graphene.Field(UserType)

    def resolve_user(self, init, id):
        return get_user_model().objects.get(id=id)

    def resolve_me(self, info):
        user = info.context.user
        if user.is_anonymous:
            raise Exception("Not logged in")
        return user


class CreateUser(graphene.Mutation):
    user = graphene.Field(UserType)

    class Arguments:
        user_name = graphene.String()
        password = graphene.String()

    def mutate(self, into, user_name, password):
        newUser = get_user_model()(username=user_name)
        newUser.set_password(password)
        newUser.save()
        return CreateUser(user=newUser)


class Mutation(graphene.ObjectType):
    create_user = CreateUser.Field()
