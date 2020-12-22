FROM alpine:3.7
FROM postgres:10-alpine
COPY bvs.pgdump /tmp/
COPY 01-init.sql /docker-entrypoint-initdb.d/
COPY 93-finalize.sql /docker-entrypoint-initdb.d/

# this is necessary so that the DB will be stored on the container itself
ENV PGDATA=/data

# init db
RUN grep -v 'exec "$@"' /usr/local/bin/docker-entrypoint.sh > /tnp/docker-entrypoint-without-serverstart.sh ; \
    chmod a+x /tmp/docker-entrypoint-without-serverstart.sh ; \
    POSTGRES_PASSWORD=mysecretpassword /tmp/docker-entrypoint-without-serverstart.sh postgres ; \
    rm -rf /docker-entrypoint-initdb.d/* /tmp/docker-entrypoint-without-serverstart.sh