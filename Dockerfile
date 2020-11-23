
# Use the official maven/Java 8 image to create a build artifact.
FROM maven:3.6-jdk-11 as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn clean package -DskipTests

# Use AdoptOpenJDK for base image.
FROM adoptopenjdk/openjdk11:alpine-slim

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/target/nlp4re-*.jar /nlp4re-0.0.1-SNAPSHOT.jar

# Run the web service on container startup.
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/nlp4re-0.0.1-SNAPSHOT.jar"]