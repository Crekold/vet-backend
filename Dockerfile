# Dockerfile optimizado para el backend de la veterinaria
FROM openjdk:21-jdk-slim

# Crear directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR generado
COPY target/*.jar app.jar

# Crear directorios necesarios
RUN mkdir -p logs uploads

# Instalar herramientas de depuración y wait-for-it script
RUN apt-get update && apt-get install -y curl postgresql-client wget && \
    rm -rf /var/lib/apt/lists/* && \
    wget -O /usr/local/bin/wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && \
    chmod +x /usr/local/bin/wait-for-it.sh

# Copiar script de inicio
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Exponer el puerto 8080
EXPOSE 8080

# Usar script de inicio en lugar de ejecutar directamente
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]