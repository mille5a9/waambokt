#!/bin/bash

git pull
./gradlew build
docker-compose -f 'docker-compose-dev.yml' down
docker-compose -f 'docker-compose-dev.yml' up -d --build