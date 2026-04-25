FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY o-pai-o/pom.xml .
RUN mvn -B -q dependency:go-offline

COPY o-pai-o/src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]