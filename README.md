[![NLP4RE](https://github.com/yenle2501/nlp4re/actions/workflows/nlp4re_build_test.yml/badge.svg)](https://github.com/yenle2501/nlp4re/actions/workflows/nlp4re_build_test.yml)
[![codecov](https://codecov.io/gh/yenle2501/nlp4re/branch/master/graph/badge.svg?token=449EECWL2U)](https://codecov.io/gh/yenle2501/nlp4re)

# NLP4RE 
A small web application for evaluating and supporting requirements descriptions by using Natural Language Processing and requirements template

## Prerequisites

* Java JRE (Version 11 or newer).
* Maven
* Spring Boot
* ReactJS
* JUnit5

## Environment Setup
Follow the instructions below to setup your environment so that you can run `nlp4re` the application on your local machine.

* Before you build up the project, make sure that your MySQL was started and database `nlp4re` has been created with username and password `root`
* Download the source code from GitHub.
`git clone https://github.com/yenle2501/nlp4re.git`
* Install Node.js and npm ( download link https://nodejs.org/en/download/)
* Define environment variable `DATABASE_HOST: localhost`
* Change the directory `cd nlp4re` and build the project.
`mvn -B clean package -DskipTests=true  --file pom.xml` 
* To run web application, you should switch to path `cd /src/main/frontend` and run `npm start`
* start the server, open the `NLP4REApplication.java` and run as a SpringBoot application

## Architecture
![image](https://user-images.githubusercontent.com/30981043/124509005-5b06ae80-ddd1-11eb-94ff-8ff145fc6bd6.png)

### Important endpoint
* http://localhost:8080- for server
*  http://localhost:3000- for web application
### RESTful-Requests
Method	| Path	| Description	
------------- | ------------------------- | ------------- | 
GET	| /description	| Get startpage of the application 
GET | /description/getRules	| get all rules of the requirements template
PUT	| /description/check	| Check the requirements description
POST| /description/addRules	| Add new rules for the requirements template	

## Infrastructure automation
Following is the simple Continuous Delivery workflow for this project.
The `GitHub Actions` builds tagged images for each successful git push on `master` branch. There are always the `latest` images for the application on `Docker Hub` and the older images will be tagged with git commit hash. Additionaly each git push will be analyzed by `Codecov` to caculate the test coverage of the project.
![124441508-084ed780-dd7c-11eb-8898-d806bd96442b](https://user-images.githubusercontent.com/30981043/125703056-1756eea5-4a98-4972-8df0-4bc15351b2ef.png)

