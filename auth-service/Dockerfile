# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Maven Wrapper
COPY .mvn/ .mvn/
COPY mvnw ./

# Parent and Auth Service POMs
COPY pom.xml ./
COPY auth-service/pom.xml auth-service/pom.xml
COPY chat-client/pom.xml chat-client/pom.xml
COPY chat-server/pom.xml chat-server/pom.xml
COPY security-common/pom.xml security-common/pom.xml

RUN chmod +x mvnw

# Download dependencies for Docker layer caching
RUN ./mvnw -B -pl auth-service -am dependency:go-offline

# Copy only the Auth Service source code
COPY auth-service/src auth-service/src
COPY security-common/src security-common/src

RUN ./mvnw -B -pl auth-service -am clean package -DskipTests


# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build \
    /app/auth-service/target/auth-service.jar \
    app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]