FROM gradle:jdk17 AS builder
WORKDIR /app

COPY build.gradle.kts .
COPY settings.gradle.kts .

COPY src/ src/

RUN gradle build --no-daemon -x test

FROM builder AS test
WORKDIR /app

COPY --from=builder /app/build/ .

CMD ["gradle", "test"]

FROM openjdk:17-jdk-slim AS app
WORKDIR /app

COPY --from=builder /app/build/libs/server-0.0.1.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]