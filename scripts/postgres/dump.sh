#!/bin/bash

# Please configure these informations :

# Postgres configuration
PPGHOST=localhost
PGDB=DB_name
PGUSER=DB_user
export PGPASSWORD=DB_password

# Files informations

tmpfile="/tmp/DUMP_MEVEO_TMP_`date +%Y_%m_%d_%H_%M_%S.sql`"
dumpfile="/var/lib/postgresql/DUMP_MEVEO_`date +%Y_%m_%d_%H_%M_%S`.sql"

# End of configuration

if [[ -z $(which pg_dump) ]]
then
echo "pg_dump must be installed"
exit 0
fi

echo "SET constraints all deferred;" > $tmpfile
pg_dump --table='cat_*' --data-only --disable-triggers --format=p --username=$PGUSER --host=$PGHOST $PGDB >> $tmpfile
cat $tmpfile | sed '/.*DISABLE TRIGGER ALL;$/{P;s/ALTER TABLE/DELETE FROM/;s/ DISABLE TRIGGER ALL//}' > $dumpfile
rm -fr $tmpfile
