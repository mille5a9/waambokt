#!/bin/bash

git pull
./gradlew build
docker-compose -f 'docker-compose-prod.yml' down
docker-compose -f 'docker-compose-prod.yml' up -d --build