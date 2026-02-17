FROM eclipse-temurin:21-alpine

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY build/libs/*.jar app.jar

RUN chown -R spring:spring /app

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage", "85.0", "-jar", "app.jar"]
