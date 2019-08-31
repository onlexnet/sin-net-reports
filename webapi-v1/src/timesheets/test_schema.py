import collections
from graphene.test import Client, Schema
from django.contrib.auth import get_user_model
from django.test import TestCase
from ..app.schema import schema
import uuid


class Context(object):
    pass


class ApiTest(TestCase):
    @classmethod
    def setUpClass(cls):
        pass

    @classmethod
    def tearDownClass(cls):
        pass

    def setUp(self):
        self.user = create_user()

    def test_create_timesheet(self):
        client = Client(schema)

        context = Context()
        context.user = self.user

        r = client.execute(
            'mutation { createCustomer(name: "a") { customer { id } } }',
            context=context,
        )

        r = client.execute(
            """
mutation {
  createTimesheet(customerId:1 description: \"a\" distanceInKms: 2 timeInMins: 3 when:\"2001-02-03\") {
    timesheet {
      id
    }
  }
}        
""",
            context=context,
        )

        assert r["data"]["createTimesheet"]["timesheet"]["id"] is not None


def create_user():
    model = get_user_model()
    new_user = model(username=str(uuid.uuid4))
    new_user.save()
    return new_user
