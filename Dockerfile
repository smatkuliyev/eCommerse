FROM openjdk:11
ADD target/ecommerse-0.0.1-SNAPSHOT.jar ecommerse-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ecommerse-0.0.1-SNAPSHOT.jar"]