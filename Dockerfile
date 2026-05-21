# Koristi Java 21 i instaliraj maven
FROM eclipse-temurin:21-jdk-jammy

# Instaliraj maven (ovo će nam omogućiti da koristimo 'mvn' komandu direktno)
RUN apt-get update && apt-get install -y maven

# Kopiraj fajlove
COPY . /app
WORKDIR /app

# Builduj projekat koristeći 'mvn' umesto './mvnw'
RUN mvn clean install -DskipTests

# Pokreni aplikaciju
CMD ["java", "-jar", "target/backendFotograf-0.0.1-SNAPSHOT.jar"]