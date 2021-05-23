#### Stage 1: Build Spring Boot
FROM maven:3.6.3-jdk-11 AS build

# Create an application directory
RUN mkdir -p nlp4re

# Set current working directory
WORKDIR nlp4re

# Copy pom
COPY pom.xml /nlp4re

# Copy src
COPY src /nlp4re/src

## Copy packages and install the dependencies
COPY src/main/frontend /nlp4re/src/main/frontend

# set enviroment variables
ENV DATABASE_HOST=nlp4re_mysql
RUN echo ${DATABASE_HOST}

# Build maven
RUN mvn -B clean package --file pom.xml

## Copy jar to production image from backend stage
FROM adoptopenjdk/openjdk11:alpine-slim
COPY --from=build /nlp4re/target/*.jar nlp4re.jar
EXPOSE 8080

# Run Web service on container image
CMD [ "sh", "-c", "java -Dserver.port=$PORT -Djava.security.egd=file:/dev/./urandom -jar nlp4re.jar" ]