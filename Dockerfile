FROM eclipse-temurin:21-jdk-jammy

# Kopiraj ceo sadrzaj foldera (ukljucujuci .mvn)
COPY . /app
WORKDIR /app

# Daj dozvolu za izvrsavanje wrapper-a (bitno za Linux)
RUN chmod +x ./mvnw

# Builduj projekat
RUN ./mvnw clean install -DskipTests

# Pokreni aplikaciju
CMD ["java", "-jar", "target/backendFotograf-0.0.1-SNAPSHOT.jar"]