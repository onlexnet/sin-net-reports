1. Start the local k3d stack from `smoke-test`:
   - `./setup-k3d.sh up`
2. If you need a clean environment, recreate the cluster:
   - `./setup-k3d.sh down`
   - `./setup-k3d.sh up`
3. Run Liquibase commands from repo root:
   1. `mvn -pl initdb-migrate liquibase:help`
   2. `mvn -pl initdb-migrate liquibase:rollback -Dliquibase.rollbackCount=1`
   3. `mvn -pl initdb-migrate liquibase:update`
   4. `mvn -pl initdb-migrate liquibase:rollback -Dliquibase.rollbackTag=v0`
