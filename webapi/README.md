# How the environment was initiated

- use VSCode, powershell terminal
- py -3 -m venv .env (to create sep dev environment)
- add just created .env folder to .gitignore
- .\.env\Scripts\Activate.ps1 (to use local isolated env)
- pip install django
- django-admin startproject app
- install poetry
- run poetry init next to manage.py

### Useful links used in the code

[Add authorisation header to graphql calls](http://docs.graphene-python.org/en/latest/testing/#execute-parameters)
[Add pylint-django](https://stackoverflow.com/questions/45135263/class-has-no-objects-member)

### Some interesting links

https://breadcrumbscollector.tech/implementing-event-sourcing-in-python-part-1-aggregates/

# How to run

- go to src folder:
- python manage.py runserver
- open http://127.0.0.1:8000/graphql/
- download and run [insomnia](https://insomnia.rest/)
- create a new user

```
mutation {
  createUser(userName: "user", password: "password") {
    user {
      id
    }
  }
}
```

get token for authentication

```
mutation {
  tokenAuth(username: "user", password: "password") {
    token
  }
}
```

copy the token as Authorization header, add 'JWT ' as the prefix to token value and validate

```
{
  me {
    username
  }
}
```

Now, you can run queries and mutations and heving session with authorized user.

### Database

- Application uses PostgreSQL database. To run your own instance for local development, please:
- docker run --rm -e POSTGRES_PASSWORD=docker -e POSTGRES_USER=docker -e POSTGRES_DB=myservices -d -p 5432:5432 postgres
- connect (if required) to instance using user/password/database = **docker/docker/myservices**
- Optionally: read some related article on [Digital Ocean](https://www.digitalocean.com/community/tutorials/how-to-use-postgresql-with-your-django-application-on-ubuntu-14-04)
- optionally: if you would like to use a different database, then open _settings.py_ and update connection info in existing database definition
