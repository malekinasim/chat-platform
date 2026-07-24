# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Maven Wrapper
COPY .mvn/ .mvn/
COPY mvnw ./

# Parent and chat client  POMs
COPY pom.xml ./
# Maven needs every module POM because they are listed in the root pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY chat-client/pom.xml chat-client/pom.xml
COPY chat-server/pom.xml chat-server/pom.xml
COPY security-common/pom.xml security-common/pom.xml

RUN chmod +x mvnw

# Download dependencies separately for Docker layer caching
RUN ./mvnw -B -pl chat-server -am dependency:go-offline

# Copy only the Auth Service source code
COPY chat-client/src chat-client/src
# Sources needed by chat-server
COPY security-common/src security-common/src


RUN ./mvnw -B -pl chat-client -am clean package -DskipTests


# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build \
    /app/chat-client/target/chat-client.jar \
    app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]