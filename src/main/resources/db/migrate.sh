#!/usr/bin/env bash
cd /mnt/hdd/school-projects/fitness-system
set -a
source .env
set +a

./mvnw initialize flyway:migrate