#!/bin/bash
# host usage
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5


# Check # of args
if [ "$#" -ne 5 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

vmstat_mb=$(vmstat --unit M)
hostname=$(hostname -f)

# usage info
memory_free=$(echo "$vmstat_mb"| tail -1 | awk -v col="4" '{print $col}' |xargs)
cpu_idle=$(echo "$vmstat_mb" | tail -1 | awk -v col="15" '{print $col}' | xargs)
cpu_kernel=$(echo "$vmstat_mb"| tail -n 1 |  awk '{print $14 }' | xargs)
disk_io=$(echo "$vmstat_mb" | tail -1 | awk '{print $9 + $10}' |xargs)
disk_available=$(df -BM / | tail -n 1 |  awk '{print substr($4, 1, length($4)-1) }' | xargs)

timestamp=$(echo "$(timedatectl)" | egrep -i "Universal time:" | awk '{print $4, $5}' | xargs)

host_id="(SELECT id FROM host_info WHERE hostname='$hostname')";

insert_stmt="INSERT INTO
host_usage(timestamp,host_id,memory_free,cpu_idle,cpu_kernel,disk_io,disk_available)
VALUES('$timestamp',$host_id,$memory_free,$cpu_idle,$cpu_kernel,$disk_io,$disk_available)"

#set up env var for pql cmd
export PGPASSWORD=$psql_password
#Insert date into a database
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"
exit $?