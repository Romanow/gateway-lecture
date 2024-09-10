FROM amazoncorretto:17 AS builder
WORKDIR application
ARG JAR_FILE=build/libs/dictionary.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM amazoncorretto:17
WORKDIR application
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
