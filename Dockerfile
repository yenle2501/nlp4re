#### Stage 1: Build react application
FROM node as frontend

## Set current working directory
WORKDIR /frontend

## Copy packages and install the dependencies
COPY src/main/frontend .
RUN npm ci
RUN npm run-script build

# Package application
RUN mkdir -p src/main/resources/static

# Copy frontend in static
COPY --from=frontend /frontend/build src/main/resources/static

#### Stage 2: Build maven
FROM maven:3.6.3-jdk-11 as backend

# Set current working directory
WORKDIR /backend

# Copy the pom.xml file
COPY pom.xml .

# Build all the dependencies in preparation to go offline. 
# This is a separate step so the dependencies will be cached unless 
# the pom.xml file has changed.
RUN mvn -B -f pom.xml dependency:go-offline

# Copy src
COPY src .

# Build maven
RUN mvn clean package verify

## Copy jar to production image from backend stage
FROM adoptopenjdk/openjdk11:alpine-slim
COPY --from=backend /target/nlp4re-*.jar ./nlp4re-0.0.1-SNAPSHOT.jar
EXPOSE 8080

# Run Web service on container image
CMD [ "sh", "-c", "java -Dserver.port=$PORT -Djava.security.egd=file:/dev/./urandom -jar /nlp4re-0.0.1-SNAPSHOT.jar" ]