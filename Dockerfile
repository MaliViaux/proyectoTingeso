FROM openjdk:17
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} proyectoTingeso.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/proyectoTingeso.jar"]