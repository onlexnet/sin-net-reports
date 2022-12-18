MVN_OPTIONS="-Dmaven.repo.local=/.m2/repository --quiet"
./mvnw $MVN_OPTIONS clean install -pl initdb-host -am -DskipTests
./mvnw $MVN_OPTIONS quarkus:dev -pl initdb-host
