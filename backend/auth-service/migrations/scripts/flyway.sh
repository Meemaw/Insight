#!/usr/bin/env bash

DATABASE_HOST=${DATABASE_HOST:="localhost"}
DATABASE_PASSWORD=${DATABASE_PASSWORD:="postgres"}
DATABASE_USER=${DATABASE_USER:="postgres"}
DATABASE_NAME=${DATABASE_NAME:="postgres"}

JDBC_URL="jdbc:postgresql://${DATABASE_HOST}/${DATABASE_NAME}"

echo "Flyway connecting to ${JDBC_URL} via user ${DATABASE_USER}" 

/flyway/flyway -url=${JDBC_URL} -user=${DATABASE_USER} -password=${DATABASE_PASSWORD} -schemas=auth -locations=filesystem:sql -connectRetries=60 $@
