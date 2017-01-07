FROM java:8-alpine
MAINTAINER stephan.goertz@gmail.com
EXPOSE 4567
ADD target/zeiterfassung-backend-1.0-SNAPSHOT.jar /zeiterfassung-backend.jar
ENTRYPOINT ["java",  "-jar", "/zeiterfassung-backend.jar"]