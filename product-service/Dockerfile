FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY product-service/pom.xml product-service/pom.xml
RUN mvn dependency:go-offline -B -f product-service/pom.xml

COPY product-service/src product-service/src
RUN mvn package -DskipTests -f product-service/pom.xml

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/product-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
