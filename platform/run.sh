#!/usr/bin/env bash
docker-machine create -d virtualbox --virtualbox-cpu-count 4 --virtualbox-memory 7096 default
eval $(docker-machine env default)
gradle buildDocker
docker volume create --name data-mongo
docker volume create --name data-prometheus
docker volume create --name data-grafana
docker volume create --name data-logging
docker volume create --name data-elasticsearch
docker-compose -f docker-infra.yml up -d
bash -c "while ! curl -s $(docker-machine ip):8761 > /dev/null; do echo waiting for start infra..; sleep 10; done; docker-compose -f docker-platform.yml up -d"

#docker-compose create
#docker-compose start

