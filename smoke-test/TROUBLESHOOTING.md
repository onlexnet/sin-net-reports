# Troubleshooting Guide for Smoke Tests

## Port Conflicts

### Problem: "Bind for :::1433 failed: port is already allocated"

This error occurs when the required ports are already in use on your machine.

### Solution 1: Use Custom Ports (Recommended)

Create a `.env` file from the template:

```bash
cd smoke-test
cp .env.example .env
```

Edit `.env` and change the conflicting ports:

```bash
# If SQL Server port 1433 is in use
DB_PORT=14330

# If port 8080 is in use
TIMEENTRIES_PORT=8090

# If port 8081 is in use
WEBAPI_PORT=8091

# If port 3000 is in use
WEBAPP_PORT=3030
```

Then run the tests:

```bash
make test
# or
docker-compose --profile test up --abort-on-container-exit test-runner
```

### Solution 2: Stop Conflicting Services

Find and stop the service using the port:

```bash
# On Linux/Mac
sudo lsof -i :1433
sudo kill <PID>

# On Windows
netstat -ano | findstr :1433
taskkill /PID <PID> /F
```

### Solution 3: Use Different Port in Command Line

Set environment variables inline:

```bash
DB_PORT=14330 TIMEENTRIES_PORT=8090 make test
```

Or with docker-compose:

```bash
DB_PORT=14330 docker-compose --profile test up --abort-on-container-exit test-runner
```

## Service Not Starting

### Check if containers are running

```bash
docker-compose ps
```

### View service logs

```bash
# All services
make logs

# Specific service
make logs-db
make logs-timeentries
make logs-webapi
make logs-webapp
```

### Check for health issues

```bash
docker inspect smoke-test-sqlserver | grep -A 5 Health
docker inspect smoke-test-timeentries | grep -A 5 Health
docker inspect smoke-test-webapi | grep -A 5 Health
```

## Images Not Found

### Problem: "Error: No such image: uservice-timeentries:latest"

The Docker images need to be built before running tests.

### Solution

Build the images:

```bash
make build
```

Or manually:

```bash
cd uservice-timeentries
mvn spring-boot:build-image -ntp -pl host -DskipTests -Dspring-boot.build-image.imageName=uservice-timeentries

cd ../uservice-webapi
mvn spring-boot:build-image -ntp -pl host -DskipTests -Dspring-boot.build-image.imageName=uservice-webapi

cd ../smoke-test
docker-compose build test-runner
```

## Tests Failing

### Check test runner logs

```bash
docker-compose logs test-runner
```

### Run tests with verbose output

```bash
docker-compose --profile test up test-runner
```

### Manually test services

```bash
# Start services without tests
make up

# Test manually
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:3000
```

## Database Connection Issues

### Check SQL Server is running and healthy

```bash
docker exec smoke-test-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P "YourStrong@Passw0rd" -Q "SELECT @@VERSION"
```

### Check database logs

```bash
make logs-db
```

### Verify database environment variables

```bash
docker inspect smoke-test-sqlserver | grep -A 10 Env
```

## Network Issues

### Check if containers are on the same network

```bash
docker network inspect smoke-test_smoke-test-network
```

### Recreate the network

```bash
make clean
make test
```

## Cleanup Issues

### Remove all containers, volumes, and networks

```bash
make clean
```

### Force cleanup if stuck

```bash
docker-compose --profile test down -v --remove-orphans
docker system prune -f
```

### Remove specific container

```bash
docker rm -f smoke-test-sqlserver
docker rm -f smoke-test-timeentries
docker rm -f smoke-test-webapi
docker rm -f smoke-test-webapp
docker rm -f smoke-test-runner
```

## Common Error Messages

### "driver failed programming external connectivity"

**Cause**: Port already in use  
**Solution**: Use custom ports (see Port Conflicts section above)

### "Error response from daemon: Conflict. The container name ... is already in use"

**Cause**: Previous containers not cleaned up  
**Solution**: 
```bash
make clean
# or
docker rm -f smoke-test-sqlserver smoke-test-timeentries smoke-test-webapi smoke-test-webapp smoke-test-runner
```

### "container ... is unhealthy"

**Cause**: Service failed to start or health check failed  
**Solution**: Check logs with `make logs-<service>` and verify environment configuration

### "no such image"

**Cause**: Docker images not built  
**Solution**: Run `make build` before `make test`

## Getting Help

If issues persist:

1. Check all service logs: `make logs`
2. Verify your environment: `make status`
3. Try a clean restart:
   ```bash
   make clean
   make build
   make test
   ```
4. Check Docker version: `docker --version` (should be 20.10+)
5. Check Docker Compose version: `docker-compose --version` (should be 1.29+ or 2.x)

## Performance Issues

### Tests taking too long

- Increase health check intervals in `docker-compose.yml`
- Allocate more resources to Docker (Settings → Resources)
- Use SSD for Docker storage

### Services timing out

- Increase `start_period` in health checks
- Check system resources (CPU, memory, disk)
- Reduce concurrent containers if running multiple test suites
