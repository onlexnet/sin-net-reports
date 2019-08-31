from django.apps import AppConfig

class TimesheetsConfig(AppConfig):
    name = 'timesheets'

    def ready(self):
        import timesheets.signals
