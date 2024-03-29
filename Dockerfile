FROM gradle:jdk17 AS builder
WORKDIR /app

COPY build.gradle.kts .
COPY settings.gradle.kts .

COPY src/ src/

RUN gradle build --no-daemon -x test

FROM builder AS test

RUN mkdir images

CMD ["gradle", "test", "--warning-mode", "all"]

FROM openjdk:17-jdk-slim AS app
WORKDIR /app

RUN mkdir images

COPY mock-images/ mock-images/
COPY --from=builder /app/build/libs/server-0.0.1.jar ./app.jar

EXPOSE 8080

CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
