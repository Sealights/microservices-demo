# Use a base image with Java installed
FROM maven:3.9.6-sapmachine-17

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged JAR file into the container
COPY . /app
RUN mvn clean package
# Expose the port that your Spring Boot application listens on
EXPOSE 5732

# Command to run the Spring Boot application when the container starts
CMD ["java", "-jar", "target/sealights-0.0.1-SNAPSHOT.jar"]
