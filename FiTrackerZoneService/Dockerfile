FROM openjdk:23-jdk
WORKDIR /app
RUN microdnf install findutils

COPY gradlew .
COPY gradle gradle
COPY . .

RUN ./gradlew build -x test

ENTRYPOINT ["java", "-jar", "build/libs/FiTrackerZone-0.0.1-SNAPSHOT.jar", "--spring.config.additional-location=/usr/app/application.properties"]