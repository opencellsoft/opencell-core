#!/bin/bash
set -e


# CREATE REPLICATION USER ON MASTER IF NEEDED
if [[ -n $PG_REP_USER && -n $PG_REP_PASSWORD && $PG_MODE = "master" ]]; then
echo "activate master replication"

echo "host replication all 0.0.0.0/0 md5" >> "$PGDATA/pg_hba.conf"

set -e
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER $PG_REP_USER REPLICATION LOGIN CONNECTION LIMIT 100 ENCRYPTED PASSWORD '$PG_REP_PASSWORD';
EOSQL

cat >> ${PGDATA}/postgresql.conf <<EOF
wal_level = hot_standby
archive_mode = on
archive_command = 'cd .'
max_wal_senders = 8
wal_keep_segments = 8
hot_standby = on
EOF
fi

# CONFIGURE REPLICATION IF NEEDED
if [[ -n $PG_REP_USER && -n $PG_REP_PASSWORD && -n $PG_MASTER_HOST && -n $PG_MASTER_PORT && $PG_MODE = "slave" ]]; then
echo "activate slave"

echo "*:*:*:$PG_REP_USER:$PG_REP_PASSWORD" > ~/.pgpass
chmod 0600 ~/.pgpass

pg_ctl -D ${PGDATA} stop -m fast

rm -Rf ${PGDATA}/*

until PGPASSWORD=$PG_REP_PASSWORD pg_basebackup -h $PG_MASTER_HOST -p $PG_MASTER_PORT -D ${PGDATA} -U ${PG_REP_USER} -vP -W
    do
        echo "Waiting for master to connect..."
        sleep 5s
done

echo "host replication all 0.0.0.0/0 md5" >> "$PGDATA/pg_hba.conf"

cat > ${PGDATA}/recovery.conf <<EOF
standby_mode = on
primary_conninfo = 'host=$PG_MASTER_HOST port=$PG_MASTER_PORT user=$PG_REP_USER password=$PG_REP_PASSWORD'
trigger_file = '/tmp/touch_me_to_promote_to_me_master'
EOF

sed -i 's/wal_level = hot_standby/wal_level = replica/g' ${PGDATA}/postgresql.conf

pg_ctl -D ${PGDATA} start

else

# CREATE KEYCLOAK DB AND USER
psql -v ON_ERROR_STOP=1 -U ${POSTGRES_USER} ${POSTGRES_DB} <<-EOSQL
    CREATE USER ${KEYCLOAK_DB_USER:-keycloak} WITH PASSWORD '${KEYCLOAK_DB_PASSWORD:-keycloak}';
    CREATE DATABASE ${KEYCLOAK_DB:-keycloak};
    GRANT ALL PRIVILEGES ON DATABASE ${KEYCLOAK_DB:-keycloak} TO ${KEYCLOAK_DB_USER:-keycloak};

EOSQL


fi
