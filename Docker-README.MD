# How to create docker? #
## Step 1: building application ##
```java
mvn clean package
```
- we first build the application with Maven or Gradle

- ## Building container image ##
```java
docker build  -t gusstorestapi:v1 .
```

- Put executable JAR in a Docker image by running the docker build command from the root project directory containing the Docker file.

## List docker images locally ##
```java
docker images 
```