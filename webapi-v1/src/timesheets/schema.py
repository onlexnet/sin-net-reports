"""
GraphQL queries and mutations.
"""
from __future__ import absolute_import

import graphene
from graphene_django import DjangoObjectType
from graphql import GraphQLError

from .models import ServiceNote
from .models import Customer


class CustomerType(DjangoObjectType):
    """
    Custom data
    """

    class Meta:
        """
        Class metadata.
        """

        model = Customer


class TimesheetType(DjangoObjectType):
    """
    Some descriptive info
    """

    class Meta:
        model = ServiceNote


class Query(graphene.ObjectType):
    timesheets = graphene.List(TimesheetType)
    customers = graphene.List(CustomerType)

    def resolve_timesheets(self, info):
        return ServiceNote.objects.all()

    def resolve_customers(self, info):
        return Customer.objects.all()


class CreateCustomer(graphene.Mutation):
    """
    Defines GraphQL mutation to create a new Customer.
    """

    customer = graphene.Field(CustomerType)

    class Arguments:
        """
        Input arguments for Customer.
        """

        name = graphene.String()

    def mutate(self, info, name):
        """
        Handler for new GraphQL requests to create a new Customer.
        """
        user = info.context.user
        if user.is_anonymous:
            raise GraphQLError("Not logged in")

        new_item = Customer()
        new_item.name = name
        new_item.created_by = user
        Customer.save(new_item)
        return CreateCustomer(new_item)


class CreateTimesheet(graphene.Mutation):
    timesheet = graphene.Field(TimesheetType)

    class Arguments:
        customer_id = graphene.Int()
        description = graphene.String()
        time_in_mins = graphene.Int()
        distance_in_kms = graphene.Int()
        when = graphene.Date()

    def mutate(
        self, info, customer_id, description, time_in_mins, distance_in_kms, when
    ):
        user = info.context.user
        if getattr(user, "is_anonymous", True):
            raise Exception("Log in to update Timesheet")

        timesheet = ServiceNote(
            who=user,
            whom_id=customer_id,
            description=description,
            time_in_mins=time_in_mins,
            distance_in_kms=distance_in_kms,
            when=when,
            created_by=user,
        )
        timesheet.save()

        return CreateTimesheet(timesheet)


class UpdateTimesheet(graphene.Mutation):
    updated = graphene.Field(TimesheetType)

    class Arguments:
        id = graphene.Int()
        description = graphene.String()

    def mutate(self, info, id, description):
        user = info.context.user
        if user.is_anonymous:
            raise Exception("Log in to update Timesheet")
        actual = ServiceNote.objects.get(id=id)
        actual.description = description
        actual.save()
        return UpdateTimesheet(actual)


class Mutation(graphene.ObjectType):
    """
    Single class to keep all package mutations in one class definition.
    """

    create_timesheet = CreateTimesheet.Field()
    update_timesheet = UpdateTimesheet.Field()
    create_customer = CreateCustomer.Field()
