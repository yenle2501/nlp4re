#### Stage 1: Build react application
FROM node as reactFrontend

## Set current working directory
WORKDIR /reactFrontend

## Copy packages and install the dependencies
COPY reactFrontend .
RUN npm ci
RUN npm run-script build

#### Stage 2: Build Spring Boot
FROM maven:3.6.3-jdk-11 as backend

# Set current working directory
WORKDIR /backend

# Copy backend
COPY backend .

# Package application
RUN mkdir -p src/main/resources/static

# Copy build in static
COPY --from=reactFrontend /reactFrontend/build src/main/resources/static

# Build maven
RUN mvn clean package verify

## Copy jar to production image from backend stage
FROM openjdk:14-jdk-alpine
COPY --from=backend /backend/target/nlp4re-*.jar ./nlp4re-0.0.1-SNAPSHOT.jar
EXPOSE 8080

# Run Web service on container image
CMD [ "sh", "-c", "java -Dserver.port=$PORT -Djava.security.egd=file:/dev/./urandom -jar /nlp4re-0.0.1-SNAPSHOT.jar" ]