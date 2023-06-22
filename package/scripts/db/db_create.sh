#!/bin/bash
#
###############################################################################
# HIRS DB creation
# Environment variables used:
# a. HIRS_MYSQL_ROOT_PWD: Set this variable if mysql root password is already set
# b. HIRS_DB_PWD: Set the pwd if default password to hirs_db user needs to be changed
# HIRS_MYSQL_ROOT_NEW_PWD wil be ignored if HIRS_MYSQL_ROOT_EXSITING_PWD is set.
################################################################################

# Capture location of the script to allow from invocation from any location
SCRIPT_DIR=$( dirname -- "$( readlink -f -- "$0"; )"; )

# Set up ihris_aca_install log
dt=$(date '+%m-%d-%y')
LOG_FILE=/var/log/hirs/hirs_aca_install_$dt.log

log () {
   echo $1
   echo $1 >> $LOG_FILE
}
log "HIRS ACA DB install start: $(date --date=@${DATE} '+%m-%d-%y:%H:%M')"

# Check if mysql is installed
type mysql >/dev/null 2>&1
if [ $? -ne 0 ]; then
   log "mariadb-server not installed, exiting HIRS ACA install"
   exit 1;
 else log "mariadb-server detected"
fi

# Set Mysql HIRS DB  password
if [ -z $HIRS_DB_PWD ]; then
   HIRS_DB_PWD="hirs_db"
fi
# Save hirs_db mysql user password to the properties file
mkdir -p /etc/hirs
mkdir -p /var/log/hirs

log "hibernate.connection.username="hirs_db"" > /etc/hirs/hibernate.properties
log "hibernate.connection.password=$HIRS_DB_PWD" >> /etc/hirs/hibernate.properties

# Check if we're in a Docker container
if [ -f /.dockerenv ]; then
    DOCKER_CONTAINER=true
else
    DOCKER_CONTAINER=false
fi

# Check if mysql is already running, if not initialize
if [[ $(pgrep -c -u mysql mysqld) -eq 0 ]]; then
# Check if running in a container
   if [ $DOCKER_CONTAINER  = true ]; then
   # if in Docker container, avoid services that invoke the D-Bus
       log "ACA is running in a container..."
       # Check if mariadb is setup
       if [ ! -d "/var/lib/mysql/mysql/" ]; then
           log "Installing mariadb"
           /usr/bin/mysql_install_db
           chown -R mysql:mysql /var/lib/mysql/
       fi
       log "Starting mysql...."
       chown -R mysql:mysql /var/log/mariadb
       /usr/bin/mysqld_safe &
   else
       systemctl enable mariadb
       systemctl start mariadb
   fi
fi

# Wait for mysql to start before continuing.
log "Checking mysqld status..."
while ! mysqladmin ping -h "$localhost" --silent; do
  sleep 1;
done
if [ -z ${HIRS_MYSQL_ROOT_PWD} ]; then
    log "HIRS_MYSQL_ROOT_PWD environment variable not set"
    mysql -fu root  -e 'quit'  &> /dev/null;
else
   log "Using $HIRS_MYSQL_ROOT_PWD as the mysql root password"
   $(mysql -u root -p$HIRS_MYSQL_ROOT_PWD -e 'quit'  &> /dev/null);
fi
if [ $? -eq 0 ]; then
 log "root password verified"
else
 log "MYSQL root password was not the default, not supplied,  or was incorrect"
 log "      please set the HIRS_MYSQL_ROOT_PWD system variable and retry."
 log "      ********** ACA Mysql setup aborted ********" ;
 exit 1;
fi

log "HIRS_DB_PWD is $HIRS_DB_PWD"
log "HIRS_MYSQL_ROOT_PWD is $HIRS_MYSQL_ROOT_PWD"

if [ -d /opt/hirs/scripts/db ]; then
   MYSQL_DIR="/opt/hirs/scripts/db"
else

   MYSQL_DIR="$SCRIPT_DIR/../db"
fi

log "MYSQL_DIR is $MYSQL_DIR"

# Check if hirs_db not created and create it if it wasn't
mysqlshow --user=root --password="$HIRS_MYSQL_ROOT_PWD" | grep "hirs_db" > /dev/null 2>&1
if [ $? -eq 0 ]; then
   log "hirs_db exists, skipping hirs_db create"
else
   mysql -u root --password=$HIRS_MYSQL_ROOT_PWD < $MYSQL_DIR/db_create.sql
   mysql -u root --password=$HIRS_MYSQL_ROOT_PWD < $MYSQL_DIR/secure_mysql.sql
   mysql -u root --password=$HIRS_MYSQL_ROOT_PWD -e "ALTER USER 'hirs_db'@'localhost' IDENTIFIED BY '"$HIRS_DB_PWD"'; FLUSH PRIVILEGES;";
fi
