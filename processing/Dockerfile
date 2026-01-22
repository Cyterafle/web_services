# Étape 1 : Build avec Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY src ./src

# Construire l'application (skip tests pour aller plus vite)
RUN mvn clean package -DskipTests

# Étape 2 : Image finale légère
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer les ports
EXPOSE 8081 9090

# Variables d'environnement
ENV JAVA_OPTS=""

# Lancer l'application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
