# First stage: Build the application
FROM maven:3.8.1-openjdk-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Second stage: Prepare the runtime environment
FROM tomcat

COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
