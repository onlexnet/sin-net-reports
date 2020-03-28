# Database configuration

The application is not yet in prod, so please run locally postgres instance:
docker run -p 5432:5432 --name yourContainerName -e POSTGRES_PASSWORD=yourPassword -d postgres