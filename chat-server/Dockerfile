# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Maven Wrapper
COPY .mvn/ .mvn/
COPY mvnw ./

# Parent and module POMs
COPY pom.xml ./
COPY auth-service/pom.xml auth-service/pom.xml
COPY chat-client/pom.xml chat-client/pom.xml
COPY chat-server/pom.xml chat-server/pom.xml
COPY security-common/pom.xml security-common/pom.xml

RUN chmod +x mvnw

# Download dependencies for Docker layer caching
RUN ./mvnw -B -pl chat-server -am dependency:go-offline

# Sources required to build chat-server
COPY chat-server/src chat-server/src
COPY security-common/src security-common/src

# Build chat-server and its required reactor modules
RUN ./mvnw -B -pl chat-server -am clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build \
    /app/chat-server/target/chat-server.jar \
    app.jar

EXPOSE 8080 8081

ENTRYPOINT ["java", "-jar", "app.jar"]