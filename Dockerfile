FROM openjdk:17-alpine
COPY /build/libs/waambokt-1.0.jar waambokt.jar
ENV ENV=${ENV}
ENTRYPOINT ["java", "-jar", "/waambokt.jar"]
