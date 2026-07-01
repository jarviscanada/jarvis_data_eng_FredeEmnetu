#!/bin/bash
# host info script
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

lscpu_out="$(lscpu)"

hostname=$(hostname -f)

cpu_number=$(echo "$lscpu_out"  | egrep "^CPU\(s\)" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out" | egrep  -i  "^Architecture:" | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out"| egrep -i "^Model name:" | awk -F ':' '{print $2}' | xargs)
cpu_mhz=$(echo "$lscpu_out" | egrep -i "Model name:" | awk '{print $7*1000}' | xargs)
l2_cache=$(echo "$lscpu_out" | egrep -i "L2 Cache:" | awk '{print $3}' | xargs)
total_mem=$(cat /proc/meminfo | egrep  "^MemTotal:" | awk '{print $2}' | xargs)
timestamp=$(timedatectl | egrep -i "Universal time:" | awk '{print $4, $5}' | xargs)

insert_stmt="INSERT INTO host_info(hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, timestamp, total_mem) \
VALUES ('$hostname', $cpu_number, '$cpu_architecture', '$cpu_model', $cpu_mhz, $l2_cache, '$timestamp', $total_mem)"


#set up env var for pql cmd
export PGPASSWORD=$psql_password

psql -h $psql_host -p $psql_port -d $db_name -U $psql_user   -c "$insert_stmt"
exit $?