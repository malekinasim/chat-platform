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
RUN ./mvnw -B -pl chat-client -am dependency:go-offline

# Sources required to build chat-client
COPY chat-client/src chat-client/src
COPY security-common/src security-common/src

# Build chat-client and its required reactor modules
RUN ./mvnw -B -pl chat-client -am clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build \
    /app/chat-client/target/chat-client.jar \
    app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]