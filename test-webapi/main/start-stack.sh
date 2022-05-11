cwd=$(pwd)
export LOG_LEVEL=warn
export DATABASE_NAME=devlocaldb

cd ../../uservice-webapi/main
dapr --app-id webapi \
--components-path ../../.components run \
--app-port 8080 -- mvn quarkus:dev -pl host &
P1=$!

cd ../../uservice-timeentries/main
export APP_DB_HOST=localhost && export SPRING_PROFILES_ACTIVE=dev && \
dapr --app-id uservice-timeentries --components-path ../../.components run -- \
mvn spring-boot:run -Dspring-boot.run.fork=false -pl host &
P2=$!

cd ../../uservice-projects/main
# init-db does need dapr, but dap deletes properly process of init-db on termination
dapr --app-id uservice-projects-dbinit run -- \
mvn quarkus:dev -pl init-db &
P3=$!

# cd ../../uservice-projects/main
# dapr --components-path ../../.components run -- \
# mvn quarkus:dev -pl host &
# P4=$!

wait $P1 $P2 $P3 $P4
cd $cwd
