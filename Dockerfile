# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-26 AS build
WORKDIR /code

# Cache dependencies first
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Build the application
COPY src ./src
RUN mvn -B package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:25-jre
LABEL maintainer="hendisantika@yahoo.co.id"
WORKDIR /app
COPY --from=build /code/target/online-banking-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
