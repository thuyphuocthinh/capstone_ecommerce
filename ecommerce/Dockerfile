## BUILD STAGE ##
FROM maven:3.8.3-openjdk-17 as build
WORKDIR /app
COPY . .
RUN mvn install -DskipTests=true

## RUN STAGE ##
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/target/ecommerce-0.0.1-SNAPSHOT.jar /run/target/ecommerce-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/run/target/ecommerce-0.0.1-SNAPSHOT.jar"]