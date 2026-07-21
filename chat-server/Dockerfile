

# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY .mvn .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/chat-server.jar  app.jar

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]