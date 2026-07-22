# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Maven Wrapper
COPY .mvn/ .mvn/
COPY mvnw ./

# Parent and Auth Service POMs
COPY pom.xml ./
COPY chat-client/pom.xml chat-client/pom.xml

RUN chmod +x mvnw

# Download dependencies separately for Docker layer caching
RUN ./mvnw -f chat-client/pom.xml dependency:go-offline

# Copy only the Auth Service source code
COPY chat-client/src chat-client/src

RUN ./mvnw -f chat-client/pom.xml clean package -DskipTests


# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build \
    /app/chat-client/target/chat-client.jar \
    app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]