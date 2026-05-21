# Koristi Java 21 kao osnovu
FROM eclipse-temurin:21-jdk-jammy

# Kopiraj fajlove
COPY . /app
WORKDIR /app

# Builduj projekat
RUN ./mvnw clean install -DskipTests

# Pokreni aplikaciju
CMD ["java", "-jar", "target/backendFotograf-0.0.1-SNAPSHOT.jar"]