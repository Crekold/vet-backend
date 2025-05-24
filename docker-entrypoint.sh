#!/bin/bash
set -e

# Esperar a que PostgreSQL esté disponible
echo "Esperando a que la base de datos esté disponible..."
wait-for-it.sh db:5432 -t 60

# Mostrar información de conexión
echo "Intentando conectar a la base de datos en: db:5432"

# Verificar conexión a PostgreSQL 
for i in {1..30}; do
  echo "Intento $i: Verificando conexión a PostgreSQL..."
  if pg_isready -h db -U postgres -q; then
    echo "Conexión a PostgreSQL establecida!"
    break
  fi
  
  # Si llegamos al último intento y sigue fallando
  if [ $i -eq 30 ]; then
    echo "No se pudo establecer conexión a PostgreSQL después de 30 intentos"
    echo "Información de depuración:"
    ping -c 1 db || echo "No se pudo hacer ping a 'db'"
    curl -v telnet://db:5432 || echo "No se pudo conectar a db:5432"
  fi
  
  echo "Esperando 2 segundos antes del siguiente intento..."
  sleep 2
done

# Mostrar configuración activa
echo "Perfil activo: $SPRING_PROFILES_ACTIVE"
echo "URL de base de datos configurada: jdbc:postgresql://db:5432/vet"

# Ejecutar la aplicación con retry
echo "Iniciando la aplicación Spring Boot..."
java -jar app.jar
