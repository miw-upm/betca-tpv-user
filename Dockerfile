# ====== ETAPA 1: Construcción del JAR con Maven ======
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ====== ETAPA 2: Configuración de PostgreSQL ======
FROM postgres:15.10 AS database
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV POSTGRES_DB=tpv
EXPOSE 5432
COPY init.sql /docker-entrypoint-initdb.d/

# ====== ETAPA 3: Configuración de la aplicación Java ======
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
CMD [ "java","-jar","app.jar" ]