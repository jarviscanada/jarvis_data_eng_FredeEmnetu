#!/bin/sh

cmd=$1
db_username=$2
db_password=$3

sudo systemctl status docker > /dev/null || sudo systemctl start docker > /dev/null

docker container inspect jrvs-psql
container_status=$?

case $cmd in
  create)

    if [ $container_status -eq 0 ]; then
      echo "Container already exists"
      exit 1
    fi

    if [ $# -ne 3 ]; then
      echo 'create requires username and password'
      exit 1
    fi

    docker volume create pgdata
    docker run --name jrvs-psql -e PGUSER=$db_username -e PGPASSWORD=$db_password -d -p 5432:5432 -v pgdata:/var/lib/postgresql/data postgres:9.6-alpine
    exit $?
  ;;

  start|stop)
    if [ $container_status -eq 1 ] ; then
      echo 'Container does not exist'
      exit 1
    fi

    docker container $cmd jrvs-psql
    exit $?

  ;;
*)
  echo 'Illegal command'
  echo 'Commands: start|stop|create'
  exit 1
  ;;
esac








