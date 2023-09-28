FROM eclipse-temurin:18-jdk-focal
RUN apt-get update -y && \
    apt-get install -y jq netcat-openbsd

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src
COPY ./log-generator.sh .

#RUN ./mvnw dependency:go-offline

RUN ./mvnw install

EXPOSE 8080
EXPOSE 5001

#CMD ["tail -f", "/dev/null"]
CMD ["/bin/bash", "/app/log-generator.sh"]
#CMD ["./mvnw", "spring-boot:run"]

