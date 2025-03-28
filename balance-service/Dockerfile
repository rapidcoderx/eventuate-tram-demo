FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# Copy parent POM first
COPY pom.xml ./
COPY balance-service/pom.xml ./balance-service/

# Install parent POM
RUN ./mvnw install -N

# Now copy balance-service files
COPY balance-service/mvnw ./balance-service/
COPY balance-service/.mvn ./balance-service/.mvn/
COPY balance-service/src ./balance-service/src/

# Build the application
WORKDIR /workspace/app/balance-service
RUN ../mvnw package -DskipTests

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY --from=build /workspace/app/balance-service/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]