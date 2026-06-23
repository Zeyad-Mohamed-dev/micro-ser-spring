FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY user-service/pom.xml user-service/pom.xml
RUN mvn dependency:go-offline -B -f user-service/pom.xml

COPY user-service/src user-service/src
RUN mvn package -DskipTests -f user-service/pom.xml

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/user-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
