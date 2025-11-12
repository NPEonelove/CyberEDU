FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests=true

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV SERVER_PORT=8080
ENV JWT_SECRET_KEY="s7k5n2x9r1v6y8w4z7u5i3o9l2p5n0x3r1v6y8w4z7u5i3o9l2p5n0x3"
ENV SPRING_DATASOURCE_URL="jdbc:postgresql://cyberedu_db:5432/cyberEdu"
ENV SPRING_DATASOURCE_USERNAME="postgres"
ENV SPRING_DATASOURCE_PASSWORD="1"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]