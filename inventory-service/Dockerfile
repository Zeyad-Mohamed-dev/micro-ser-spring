FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY inventory-service/pom.xml inventory-service/pom.xml
RUN mvn dependency:go-offline -B -f inventory-service/pom.xml

COPY inventory-service/src inventory-service/src
RUN mvn package -DskipTests -f inventory-service/pom.xml

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/inventory-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
