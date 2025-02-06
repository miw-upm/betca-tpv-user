# [Máster en Ingeniería Web por la Universidad Politécnica de Madrid (miw-upm)](http://miw.etsisi.upm.es)
## Back-end con Tecnologías de Código Abierto (BETCA).
> Aplicación TPV. Pretende ser un ejemplo práctico y real de todos los conocimientos vistos

## Estado del código
[![Spring User - Tests](https://github.com/miw-upm/betca-tpv-user/actions/workflows/ci.yml/badge.svg)](https://github.com/miw-upm/betca-tpv-user/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?branch=develop&project=es.upm.miw%3Abetca-tpv-user&metric=alert_status)](https://sonarcloud.io/dashboard?id=es.upm.miw%3Abetca-tpv-user&branch=develop)

## Tecnologías necesarias
`Java` `Maven` `GitHub` `Spring-boot` `Sonarcloud` `Docker` `PostgreSQL` `Render` 

## :gear: Ejecución en local
1. Arrancar Docker Desktop
1. Ejecutar en consola: `docker compose up --build -d`

* Cliente Web (OpenAPI): `http://localhost:8081/swagger-ui.html`
* Ver los logs (con -f se queda escuchando, Ctrl+C para salir): `docker logs [-f] user-api`
* Para parar: `docker compose stop`
* Arrancar la consola de PostgreSQL sobre la BD: `docker exec -it postgres-db psql -U postgres -d tpv`

## :book: Documentación del proyecto
[betca-tpv: User](https://github.com/miw-upm/betca-tpv#back-end-user).


