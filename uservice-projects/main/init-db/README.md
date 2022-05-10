1. Start docker-compose up as usual to run just dockerized database server
2. Play individual commands like:
   1. **mvn -pl init-db liquibase:help**
   2. **mvn -pl init-db liquibase:rollback -Dliquibase.rollbackCount=1** do rollback one change
   3. **mvn -pl init-db liquibase:update** to aply all chanegs
   4. **mvn -pl init-db liquibase:rollback -Dliquibase.rollbackTag=v0** do rollback to empty tables
3. Or, test script manually **docker-compose -f host/compose-test-services.yaml up --build** to  run database and migrations
