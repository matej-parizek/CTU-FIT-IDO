FROM gitlab.fit.cvut.cz:5050/bi-ido/bi-ido-registry/eclipse-temurin:17-jre-jammy
ARG JAR_FILE=app/target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]