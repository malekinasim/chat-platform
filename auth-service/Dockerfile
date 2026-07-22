# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Maven Wrapper
COPY .mvn/ .mvn/
COPY mvnw ./

# Parent and Auth Service POMs
COPY pom.xml ./
COPY auth-service/pom.xml auth-service/pom.xml

RUN chmod +x mvnw

# Download dependencies separately for Docker layer caching
RUN ./mvnw -f auth-service/pom.xml dependency:go-offline

# Copy only the Auth Service source code
COPY auth-service/src auth-service/src

RUN ./mvnw -f auth-service/pom.xml clean package -DskipTests


# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build \
    /app/auth-service/target/auth-service.jar \
    app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]