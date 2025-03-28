#!/bin/bash

set -e
set -u

export POSTGRES_MULTIPLE_DATABASES=balance_db,transaction_db
export POSTGRES_USER=postgres

function create_user_and_database() {
    local database=$1
    echo "Creating user and database '$database'"
    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
        CREATE DATABASE $database;
        GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
EOSQL
}

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
    echo "Multiple database creation requested: $POSTGRES_MULTIPLE_DATABASES"
    for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
        create_user_and_database $db
    done
    echo "Multiple databases created"

    # Add Eventuate schema to each database
    for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
        echo "Initializing Eventuate schema in $db"
        psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -d "$db" -f /docker-entrypoint-initdb.d/eventuate-schema.sql
    done
    echo "Eventuate schema initialized in all databases"
fi