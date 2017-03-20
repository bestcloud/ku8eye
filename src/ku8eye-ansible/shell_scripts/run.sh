#!/bin/sh

# start mysqld service
/root/shell_scripts/run_mysqld.sh
# wait for mysqld to startup completely
sleep 5
echo "======`date +"[%Y-%m-%d %H:%M:%S]"` mysqld_safe start done. ======"

# run ku8eye-web initsql.sql to create user and tables
mysql < /root/db_scripts/initsql.sql
echo "======`date +"[%Y-%m-%d %H:%M:%S]"` create mysql tables for ku8eye-web done. ======"
sleep 1

# start ku8eye-web app
/root/shell_scripts/run_tomcat.sh
echo "======`date +"[%Y-%m-%d %H:%M:%S]"` start ku8eye-web done. ======" 
