cwd=$(pwd)
export LOG_LEVEL=warn
export DATABASE_NAME=devlocaldb

export DAPR_PORT_HTTP=3500
export DAPR_PORT_GRPC=50001

# Base dev ports:
# http: 11010
# grpc: not supported
# debug: 11012
cd ../../uservice-webapi/main
dapr --app-id webapi \
--components-path ../../.components run \
--dapr-http-port $DAPR_PORT_HTTP --dapr-grpc-port $DAPR_PORT_GRPC \
--app-port 11010 \
-- \
mvn quarkus:dev -pl host &
P1=$!

# Base dev ports:
# http: 11020
# grpc: 11021
cd ../../uservice-timeentries/main

# rebuild dependant modules before starting spring boot
mvn -Dmaven.test.skip=true install -pl host -am

export APP_DB_HOST=localhost && export SPRING_PROFILES_ACTIVE=dev && \
dapr --app-id timeentries \
--components-path ../../.components run \
-- \
mvn spring-boot:run -pl host &
P2=$!


# Base dev ports:
# http: 11030
# grpc: 11031
cd ../../uservice-projects/main
export DATABASE_NAME=devlocaldb && export DATABASE_SCHEMA=uservice_projects && \
dapr --app-id projects \
--components-path ../../.components run \
--app-port 11031 --app-protocol grpc \
-- \
mvn quarkus:dev -pl host &
P3=$!


wait $P1 $P2 $P3
cd $cwd
