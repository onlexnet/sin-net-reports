cd ../../uservice-webapi/main && dapr --components-path .components run -- mvn quarkus:dev -pl host & 
P1=$!

cd ../../webapi/main && APP_DB_HOST=localhost APP_DB_NAME=postgres SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run -pl host
P2=$!

wait $P1 $P2
