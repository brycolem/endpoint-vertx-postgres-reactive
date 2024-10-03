FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
EXPOSE 8001
COPY --from=build /app/target/api-1.0.1-fat.jar /app/api-1.0.1-fat.jar
CMD ["java", "-jar", "api-1.0.1-fat.jar"]
