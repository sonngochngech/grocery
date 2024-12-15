FROM openjdk:17.0.2-jdk

WORKDIR /app

COPY target/*.jar /app/grocery.jar

EXPOSE 8081


ENTRYPOINT ["java", "-jar", "/app/grocery.jar"]