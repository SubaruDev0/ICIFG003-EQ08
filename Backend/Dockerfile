FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar el archivo de configuración de dependencias
COPY pom.xml .

# Descargar dependencias (esto ayuda a que los builds sean más rápidos si no cambias el pom)
RUN mvn dependency:go-offline

# Copiar el código fuente y compilar el .jar
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar solo el archivo .jar generado en la etapa anterior
# Usamos un comodín para capturar el jar sin importar la versión exacta
COPY --from=build /app/target/*.jar app.jar

# Configurar variables de entorno por defecto
# Render sobreescribirá PORT automáticamente, pero dejamos 6789 por seguridad
ENV PORT=6789
EXPOSE ${PORT}

# Comando de inicio
# Usamos -Dserver.port para obligar a Spring Boot a escuchar en el puerto que Render asigne
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-Xmx512m", "-jar", "app.jar"]