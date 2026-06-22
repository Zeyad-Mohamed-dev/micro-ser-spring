FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY order-service/pom.xml order-service/pom.xml
RUN mvn dependency:go-offline -B -f order-service/pom.xml

COPY order-service/src order-service/src
RUN mvn package -DskipTests -f order-service/pom.xml

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/order-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
