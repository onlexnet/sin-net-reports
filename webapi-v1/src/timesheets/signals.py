from django.db.models.signals import pre_save
from django.dispatch import receiver
from .models import ServiceNote
import logging


@receiver(pre_save, sender=ServiceNote)
def mymodel_save_handler(sender, **kwargs):
    logging.info("======================================")
