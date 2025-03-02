FROM maven:3.9.9-amazoncorretto-17 AS build
WORKDIR /app
COPY --from=assets pom.xml .
RUN mvn dependency:go-offline
COPY --from=assets src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]