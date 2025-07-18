# Utilise une image Java 17 adaptée à Spring Boot
FROM openjdk:17-jdk-alpine

# Argument pour le jar généré par Maven
ARG JAR_FILE=target/*.jar

# Copie le jar dans l'image
COPY ${JAR_FILE} app.jar

# Expose le port par défaut de Spring Boot
EXPOSE 8080

# Commande de lancement
ENTRYPOINT ["java","-jar","/app.jar"] 