"""
Contains database models
"""
from __future__ import absolute_import
from django.db import models
from django.contrib.auth import get_user_model


class Customer(models.Model):
    """
    Represent external client for whom you provide services.
    """

    name = models.TextField(blank=False, unique=True)
    created_at = models.DateTimeField(auto_now_add=True)
    created_by = models.ForeignKey(
        get_user_model(), on_delete=models.CASCADE, null=False
    )


class ServiceNote(models.Model):
    """
    Single description of a time-bounded service.
    """

    description = models.TextField(blank=True)
    time_in_mins = models.IntegerField()
    distance_in_kms = models.IntegerField()
    when = models.DateField()

    created_at = models.DateTimeField(auto_now_add=True)
    created_by = models.ForeignKey(
        get_user_model(), on_delete=models.CASCADE, related_name="created_by"
    )


class DailyReport(models.Model):
    """
    Connects who, whom and what
    """

    whom = models.ForeignKey(Customer, on_delete=models.CASCADE)
    what = models.ForeignKey(ServiceNote, on_delete=models.CASCADE)
    who = models.ForeignKey(get_user_model(), on_delete=models.CASCADE)
