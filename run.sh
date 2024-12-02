#!/bin/sh

rm -f *.jar
./mvnw clean install -DskipTests
cp target/*.jar runnable.jar

echo "App Compiled Successfully"

docker-compose down
docker-compose build
docker-compose up -d

echo "Application running successfully..."