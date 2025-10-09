# the base image
FROM openjdk:17.0.2-jdk-oracle
# Establece la variable de entorno para la zona horaria (ajusta según tu necesidad)
ENV TZ=America/Lima
# the JAR file path
ARG JAR_FILE=target/*.jar

# Copy the JAR file from the build context into the Docker image
COPY ${JAR_FILE} backend.jar

CMD apt-get update -y

# Exponer el puerto HTTP
EXPOSE 8080

# Set the default command to run the Java application
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/backend.jar"]