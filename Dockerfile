FROM gradle:8-jdk24 as build
WORKDIR /home/gradle/project
COPY . .
RUN gradle clean bootJar --no-daemon

FROM bellsoft/liberica-openjre-debian:24-cds AS runtime
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
