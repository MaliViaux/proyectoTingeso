FROM openjdk:17
ARG JAR_FILE=target/proyecto_tingeso.jar
COPY ${JAR_FILE} proyecto_tingeso.jar
ENTRYPOINT ["java","-jar","/payroll-backend.jar"]