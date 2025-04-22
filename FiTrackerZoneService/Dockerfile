FROM openjdk:23-jdk
WORKDIR /app
RUN microdnf install findutils dos2unix

COPY gradlew .
COPY gradle gradle
COPY . .
RUN dos2unix ./gradlew \
    chmod +x ./gradlew \
    ./gradlew buildWithoutTests

ENTRYPOINT ["java", "-jar", "build/libs/FiTrackerZone-0.0.1-SNAPSHOT.jar", "--spring.config.additional-location=/usr/app/application.properties"]