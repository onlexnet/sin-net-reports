1. Run Liquibase commands from repo root:
   1. `mvn -pl initdb-migrate liquibase:help`
   2. `mvn -pl initdb-migrate liquibase:rollback -Dliquibase.rollbackCount=1`
   3. `mvn -pl initdb-migrate liquibase:update`
   4. `mvn -pl initdb-migrate liquibase:rollback -Dliquibase.rollbackTag=v0`
