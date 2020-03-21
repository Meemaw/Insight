#!/usr/bin/env bash

DATABASE_HOST=${DATABASE_HOST:="localhost"}
DATABASE_PASSWORD=${DATABASE_PASSWORD:="postgres"}
DATABASE_USER=${DATABASE_USER:="postgres"}

JDBC_URL="jdbc:postgresql://${DATABASE_HOST}/${DATABASE_NAME}"

echo "Starting flyway ${JDBC_URL}" 

/flyway/flyway -url=${JDBC_URL} -user=${DATABASE_USER} -password=${DATABASE_PASSWORD} -schemas=auth -locations=filesystem:sql -connectRetries=60 $@
