#!/bin/bash

# Please configure these informations :

# Postgres configuration
PGHOST=localhost
PGDB=DB_name
PGUSER=DB_user
export PGPASSWORD=DB_password

logfile="/tmp/restore_log_`date +%Y_%m_%d_%H_%M_%S`.log"

# End of configuration


if [ ${#} -ne 1 ]; then
echo "You need to give an existing dump file in argument : restore.sh /path/to/dump.sql"
    exit 0
fi

dumpfile=$1
if [ ! -f $dumpfile ]; then
    echo "File not found!"
    echo "You need to give an existing dump file in argument : restore.sh /path/to/dump.sql"
    exit 0
fi

if [[ -z $(which psql) ]]
then
echo "psql must be installed"
exit 0
fi

psql --dbname=$PGDB --username=$PGUSER --host=$PGHOST --file $dumpfile > $logfile
