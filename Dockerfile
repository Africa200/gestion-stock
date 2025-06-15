FROM eclipse-temurin:21-jdk
LABEL authors="Abdoulfatah"
VOLUME /tmp
ADD target/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]
EXPOSE 8080