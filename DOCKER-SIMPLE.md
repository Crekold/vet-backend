# Docker Simple - Backend Veterinaria

## Uso rápido (PowerShell)

```powershell
# Compilar y ejecutar todo
.\docker-vet.ps1 up

# Ver logs en tiempo real
.\docker-vet.ps1 logs

# Detener servicios
.\docker-vet.ps1 down
```

## Instrucciones básicas

### 1. Compilar la aplicación
```bash
./mvnw clean package -DskipTests
```

### 2. Ejecutar con Docker Compose
```bash
docker-compose up --build
```

### 3. Acceder a la aplicación
- Backend: http://localhost:8080
- Base de datos: localhost:5432

### 4. Detener los servicios
```bash
docker-compose down
```

## Solución de problemas

### Error de conexión a la base de datos
Si ves el error "Connection to localhost:5432 refused", significa que:

1. **Verifica que el perfil prod esté activo**: El backend debe usar `application-prod.yml` que conecta a `db:5432` (no `localhost:5432`)

2. **Espera a que la base de datos esté ready**: El `docker-compose.yml` incluye un healthcheck para PostgreSQL

3. **Rebuilding containers**: Si persiste el problema:
   ```bash
   docker-compose down -v
   docker-compose up --build
   ```

4. **Verificar logs**: Los logs de inicio muestran qué perfil y URL de BD se está usando:
   ```bash
   docker-compose logs backend
   ```

## Archivos importantes

- `docker-vet.ps1`: Script PowerShell para manejo fácil
- `Dockerfile`: Configuración simple del contenedor
- `docker-compose.yml`: Servicios básicos (PostgreSQL + Backend) con healthcheck
- `application-prod.yml`: Configuración de producción (conecta a `db:5432`)
- `application.yml`: Configuración local (conecta a `localhost:5432`)
- `logback-spring.xml`: Configuración básica de logs

## Logs
Los logs se guardan en:
- Consola: output directo de docker-compose
- Archivo: `./logs/vet-backend.log` (mapeado desde el contenedor)
- Logs de inicio muestran perfil activo y URL de base de datos
