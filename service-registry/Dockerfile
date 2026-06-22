FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY service-registry/pom.xml service-registry/pom.xml
RUN mvn dependency:go-offline -B -f service-registry/pom.xml

COPY service-registry/src service-registry/src
RUN mvn package -DskipTests -f service-registry/pom.xml

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/service-registry/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
