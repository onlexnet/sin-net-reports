cwd=$(pwd)
# trap 'kill $(jobs -p)' SIGINT SIGTERM SIGKILL # kills remainig tasks after ctrl+C
LOG_LEVEL="warn"
# export JAVA_TOOL_OPTIONS=-Dfork=false
# export JDK_JAVA_OPTIONS=-Dfork=false
# export _JAVA_OPTIONS=-Dfork=false
# export MAVEN_OPTS=-Dfork=false

cd ../../uservice-webapi/main
dapr --app-id uservice-webapi --components-path .components run -- mvn quarkus:dev -pl host &
P1=$!

cd ../../uservice-timeentries/main
export APP_DB_HOST=localhost && export APP_DB_NAME=postgres && export SPRING_PROFILES_ACTIVE=dev && \
dapr --app-id uservice-timeentries --components-path .components run -- \
mvn spring-boot:run -Dspring-boot.run.fork=false -pl host &
P2=$!

cd ../../uservice-projects/main
dapr --app-id uservice-projects-dbinit run -- \
mvn quarkus:dev -pl init-db &
P3=$!

cd ../../uservice-projects/main
dapr --components-path .components run -- mvn quarkus:dev -pl host &
P4=$!

wait $P1 $P2 $P3 $P4
cd $cwd
