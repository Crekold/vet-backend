# 🐾 VET-BACKEND | Sistema de Gestión Veterinaria

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Licencia](https://img.shields.io/badge/Licencia-MIT-blue)

API REST para la gestión completa de clínicas veterinarias. Sistema integral para administrar pacientes, historiales clínicos, citas, servicios y documentación médica.

## 📋 Contenido

- Descripción
- Características
- Tecnologías
- Requisitos previos
- Instalación y configuración
- Documentación de la API
- Estructura del proyecto
- Pruebas
- Contribución
- Licencia

## 📄 Descripción

VET-BACKEND es una solución completa para la gestión de clínicas veterinarias, desarrollada con Spring Boot. Proporciona una API REST para administrar todos los aspectos relacionados con la atención veterinaria, incluyendo:

- Gestión de historiales clínicos de pacientes
- Administración de citas y servicios
- Almacenamiento y gestión de archivos clínicos (radiografías, análisis, etc.)
- Sistema de usuarios con roles específicos (administrador, veterinario, empleado)
- Dashboard con estadísticas y métricas relevantes

## ✨ Características

- **Gestión de pacientes y clientes**: Registro completo de mascotas y sus propietarios
- **Historiales clínicos**: Creación y seguimiento de registros médicos detallados
- **Sistema de citas**: Programación y gestión de citas con veterinarios específicos
- **Gestión de archivos**: Almacenamiento de documentos clínicos (radiografías, análisis, etc.)
- **Control de usuarios**: Sistema de autenticación y autorización basado en roles
- **Estadísticas**: Dashboard con indicadores clave para la administración
- **API RESTful**: Endpoints bien documentados para integraciones

## 🔧 Tecnologías

- **Lenguaje**: Java 17+
- **Framework**: Spring Boot 3.x
- **Seguridad**: Spring Security con JWT
- **Documentación**: OpenAPI / Swagger
- **Persistencia**: JPA/Hibernate
- **Base de datos**: PostgreSQL (configurable)
- **Construcción**: Maven
- **Testing**: JUnit 5, Mockito

## 🔍 Requisitos previos

Para configurar este proyecto necesitarás:

- Java JDK 17 o superior
- Maven 3.8+ o Gradle 7.0+
- PostgreSQL u otra base de datos compatible
- Git
- IDE compatible con Spring Boot (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/vet-backend.git
cd vet-backend
```

### 2. Configuración de la base de datos

Edita el archivo application.yml para configurar la conexión a tu base de datos:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vet_db
    username: tu_usuario
    password: tu_contraseña
```

### 3. Construir el proyecto

Con Maven:
```bash
mvn clean install
```

### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## 📚 Documentación de la API

La documentación interactiva de la API está disponible a través de Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

### Endpoints principales:

#### Historiales Clínicos
- `GET /api/historial-clinico`: Listar todos los historiales clínicos
- `GET /api/historial-clinico/{id}`: Obtener historial por ID
- `POST /api/historial-clinico`: Crear nuevo historial clínico
- `PUT /api/historial-clinico/{id}`: Actualizar historial clínico
- `DELETE /api/historial-clinico/{id}`: Eliminar historial clínico

#### Citas
- `GET /api/citas`: Listar todas las citas
- `POST /api/citas`: Crear nueva cita
- `PUT /api/citas/{id}`: Actualizar cita
- `DELETE /api/citas/{id}`: Cancelar cita

#### Archivos Clínicos
- `GET /api/archivos-clinicos`: Listar todos los archivos
- `GET /api/archivos-clinicos/download/{fileName}`: Descargar archivo
- `POST /api/archivos-clinicos/upload`: Subir nuevo archivo
- `DELETE /api/archivos-clinicos/{id}`: Eliminar archivo

## 📂 Estructura del proyecto

```
vet-backend/
├── src/
│   ├── main/
│   │   ├── java/com/backend/vet/
│   │   │   ├── config/         # Configuraciones de la aplicación
│   │   │   ├── controller/     # Controladores REST
│   │   │   ├── dto/            # Objetos de transferencia de datos
│   │   │   ├── exception/      # Manejo de excepciones
│   │   │   ├── model/          # Entidades JPA
│   │   │   ├── repository/     # Repositorios de datos
│   │   │   ├── service/        # Servicios de negocio
│   │   │   ├── util/           # Utilidades generales
│   │   │   └── VetApplication.java  # Punto de entrada
│   │   └── resources/
│   │       ├── application.yml      # Configuración principal
│   │       └── application-openapi.yml  # Configuración OpenAPI
│   │
│   └── test/                   # Pruebas unitarias e integración
│
├── pom.xml                     # Configuración de Maven
└── README.md                   # Este archivo
```

## 🧪 Pruebas

Para ejecutar las pruebas automatizadas:

```bash
mvn test
```

Para generar un informe de cobertura:

```bash
mvn verify
```

## 👥 Contribución

Las contribuciones son bienvenidas. Para contribuir:

1. Haz un fork del proyecto
2. Crea una rama para tu funcionalidad (`git checkout -b feature/nueva-funcionalidad`)
3. Realiza tus cambios y haz commit (`git commit -m 'Agrega nueva funcionalidad'`)
4. Sube los cambios a tu fork (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

Por favor, asegúrate de seguir las buenas prácticas de código y de añadir pruebas para cualquier nueva funcionalidad.

## 📜 Licencia

Este proyecto está licenciado bajo la Licencia MIT - consulta el archivo `LICENSE` para más detalles.

---

Desarrollado por [Tu Nombre](https://github.com/tu-usuario) | [Contacto](mailto:tu-email@ejemplo.com)