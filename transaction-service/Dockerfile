FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# Copy parent POM first
COPY pom.xml ./
COPY transaction-service/pom.xml ./transaction-service/

# Install parent POM
RUN ./mvnw install -N

# Now copy transaction-service files
COPY transaction-service/mvnw ./transaction-service/
COPY transaction-service/.mvn ./transaction-service/.mvn/
COPY transaction-service/src ./transaction-service/src/

# Build the application
WORKDIR /workspace/app/transaction-service
RUN ../mvnw package -DskipTests

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY --from=build /workspace/app/transaction-service/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]