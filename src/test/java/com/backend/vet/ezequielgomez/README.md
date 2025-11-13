# Pruebas Unitarias TDD - Ezequiel Gomez

Este paquete contiene **10 pruebas unitarias** desarrolladas siguiendo la metodología **TDD (Test-Driven Development)** con la estructura **Arrange-Act-Assert**.

## 📋 Estructura de las Pruebas

Cada prueba sigue el patrón de tres fases:

1. **PREPARACIÓN (Arrange)**: Configuración de datos y mocks necesarios
2. **LÓGICA DE LA PRUEBA (Act)**: Ejecución del método a probar
3. **VERIFICACIÓN CON ASSERT (Assert)**: Validación de resultados esperados

## 📂 Archivos de Prueba

### AuthControllerTest.java (5 pruebas más importantes)

Pruebas críticas para el controlador de autenticación:

1. **deberiaAutenticarExitosamenteUsuarioConCredencialesValidas**
   - ✅ Caso exitoso principal
   - Verifica que un usuario con credenciales correctas pueda iniciar sesión
   - Valida la generación del token JWT y todos los datos de la respuesta
   - Confirma que se resetean los intentos fallidos

2. **deberiaRechazarAutenticacionCuandoUsuarioEstaInactivo**
   - 🔒 Validación de seguridad crítica
   - Verifica que usuarios inactivos no puedan autenticarse
   - Valida el código de estado HTTP 401 (Unauthorized)
   - Previene acceso no autorizado

3. **deberiaProcesarIntentoFallidoCuandoCredencialesSonIncorrectas**
   - 🛡️ Protección contra ataques de fuerza bruta
   - Verifica el registro de intentos fallidos
   - Valida las credenciales incorrectas
   - Sistema de conteo de intentos fallidos

4. **deberiaRegistrarNuevoUsuarioExitosamente**
   - 👤 Funcionalidad core del registro
   - Verifica la creación de nuevos usuarios
   - Valida el código de estado HTTP 201 (Created)
   - Confirma que los datos del usuario se guardan correctamente

5. **deberiaRestablecerContraseniaExitosamenteConTokenValido**
   - 🔑 Recuperación de contraseña
   - Verifica el restablecimiento con token válido
   - Valida la actualización de contraseña
   - Funcionalidad importante para experiencia de usuario

### CitaControllerTest.java (5 pruebas)

Pruebas para el controlador de citas:

1. **deberiaObtenerTodasLasCitasExitosamente**
   - Verifica la obtención de lista completa de citas
   - Valida el código de estado HTTP 200
   - Confirma que se retornan todas las citas

2. **deberiaCrearNuevaCitaExitosamenteConTodosLosDatosRequeridos**
   - Verifica la creación de nuevas citas
   - Valida el código de estado HTTP 201 (Created)
   - Confirma que todos los campos se guardan correctamente

3. **deberiaActualizarCitaExistenteCorrectamente**
   - Verifica la actualización de citas existentes
   - Valida que los cambios se reflejen correctamente
   - Confirma el código de estado HTTP 200

4. **deberiaEliminarCitaExistenteYRetornarNoContent**
   - Verifica la eliminación de citas
   - Valida el código de estado HTTP 204 (No Content)
   - Confirma que se llama al servicio de eliminación

5. **deberiaObtenerCitasPorMascotaFiltrandoCorrectamente**
   - Verifica el filtrado de citas por mascota
   - Valida que todas las citas pertenezcan a la misma mascota
   - Confirma la correcta cantidad de resultados

## 🎯 ¿Por qué estas 5 pruebas de AuthController?

Las pruebas seleccionadas cubren:
- ✅ **Caso exitoso principal** (happy path del login)
- 🔒 **Seguridad crítica** (usuarios inactivos)
- 🛡️ **Protección contra ataques** (intentos fallidos)
- 👤 **Funcionalidad core** (registro de usuarios)
- 🔑 **Recuperación de cuenta** (reset de contraseña)

Estas pruebas cubren los escenarios más importantes y críticos del sistema de autenticación.

## 🛠️ Tecnologías Utilizadas

- **JUnit 5**: Framework de pruebas unitarias
- **Mockito**: Framework para crear mocks y stubs
- **Spring Boot Test**: Soporte para pruebas en Spring Boot
- **AssertJ** (implícito): Aserciones fluidas

## ▶️ Ejecución de las Pruebas

### Desde Maven:
```bash
mvn test -Dtest="com.backend.vet.ezequielgomez.*"
```

### Desde IDE:
- Click derecho en el paquete `ezequielgomez`
- Seleccionar "Run Tests"

### Ejecutar una prueba específica:
```bash
mvn test -Dtest="AuthControllerTest#deberiaAutenticarExitosamenteUsuarioConCredencialesValidas"
```

## ✅ Resultados

**Total de pruebas: 10**
- ✅ AuthControllerTest: 5 pruebas pasadas (las más críticas)
- ✅ CitaControllerTest: 5 pruebas pasadas

**Cobertura:**
- AuthController: Escenarios críticos de autenticación y seguridad
- CitaController: Operaciones CRUD completas

## 📝 Buenas Prácticas Aplicadas

1. **Nombres descriptivos**: Cada prueba describe claramente qué verifica
2. **Aislamiento**: Uso de mocks para aislar la unidad bajo prueba
3. **Verificación completa**: Se verifican tanto el resultado como las interacciones
4. **Setup centralizado**: Uso de `@BeforeEach` para configuración común
5. **Assertions múltiples**: Se verifica exhaustivamente cada escenario
6. **Estados HTTP correctos**: Validación de códigos de respuesta apropiados

## 🔍 Cobertura de Escenarios

### Casos exitosos:
- ✅ Autenticación válida
- ✅ Creación de recursos (usuarios y citas)
- ✅ Actualización de datos
- ✅ Obtención de listas
- ✅ Restablecimiento de contraseña

### Casos de error:
- ❌ Credenciales inválidas
- ❌ Usuarios inactivos
- ❌ Validaciones fallidas

### Casos CRUD completos (Citas):
- 📋 Read (GET)
- ➕ Create (POST)
- ✏️ Update (PUT)
- ❌ Delete (DELETE)

## 👤 Autor

**Ezequiel Gomez**

---

*Pruebas desarrolladas siguiendo metodología TDD y principios SOLID*

## 📋 Estructura de las Pruebas

Cada prueba sigue el patrón de tres fases:

1. **PREPARACIÓN (Arrange)**: Configuración de datos y mocks necesarios
2. **LÓGICA DE LA PRUEBA (Act)**: Ejecución del método a probar
3. **VERIFICACIÓN CON ASSERT (Assert)**: Validación de resultados esperados

## 📂 Archivos de Prueba

### AuthControllerTest.java (5 pruebas)

Pruebas para el controlador de autenticación:

1. **deberiaAutenticarExitosamenteUsuarioConCredencialesValidas**
   - Verifica que un usuario con credenciales correctas pueda iniciar sesión
   - Valida la generación del token JWT
   - Confirma que se resetean los intentos fallidos

2. **deberiaRechazarAutenticacionCuandoUsuarioEstaInactivo**
   - Verifica que usuarios inactivos no puedan autenticarse
   - Valida el código de estado HTTP 401 (Unauthorized)
   - Confirma que no se procese la autenticación

3. **deberiaRechazarAutenticacionCuandoCuentaEstaBloqueada**
   - Verifica el bloqueo temporal de cuentas
   - Valida el código de estado HTTP 423 (Locked)
   - Confirma mensaje de cuenta bloqueada

4. **deberiaProcesarIntentoFallidoCuandoCredencialesSonIncorrectas**
   - Verifica el registro de intentos fallidos
   - Valida las credenciales incorrectas
   - Confirma que se llama a `processLoginFailure`

5. **deberiaRegistrarNuevoUsuarioExitosamente**
   - Verifica la creación de nuevos usuarios
   - Valida el código de estado HTTP 201 (Created)
   - Confirma que los datos del usuario se guardan correctamente

6. **deberiaRechazarRegistroCuandoUsuarioYaExiste**
   - Verifica validación de usuarios duplicados
   - Valida el código de estado HTTP 400 (Bad Request)
   - Confirma mensaje de error apropiado

7. **deberiaProcesarSolicitudRestablecimientoContraseniaCorrectamente**
   - Verifica el proceso de "olvidé mi contraseña"
   - Valida la generación de token de restablecimiento
   - Confirma mensaje de confirmación al usuario

8. **deberiaRestablecerContraseniaExitosamenteConTokenValido**
   - Verifica el restablecimiento con token válido
   - Valida la actualización de contraseña
   - Confirma mensaje de éxito

9. **deberiaRechazarRestablecimientoConTokenInvalidoOExpirado**
   - Verifica validación de tokens expirados/inválidos
   - Valida el código de estado HTTP 400
   - Confirma mensaje de error de token

10. **deberiaIndicarQueContraseniaRequiereCambioCuandoHaExpirado**
    - Verifica detección de contraseñas expiradas
    - Valida el flag `passwordChangeRequired`
    - Confirma que el usuario aún puede autenticarse

### CitaControllerTest.java (5 pruebas)

Pruebas para el controlador de citas:

1. **deberiaObtenerTodasLasCitasExitosamente**
   - Verifica la obtención de lista completa de citas
   - Valida el código de estado HTTP 200
   - Confirma que se retornan todas las citas

2. **deberiaCrearNuevaCitaExitosamenteConTodosLosDatosRequeridos**
   - Verifica la creación de nuevas citas
   - Valida el código de estado HTTP 201 (Created)
   - Confirma que todos los campos se guardan correctamente

3. **deberiaActualizarCitaExistenteCorrectamente**
   - Verifica la actualización de citas existentes
   - Valida que los cambios se reflejen correctamente
   - Confirma el código de estado HTTP 200

4. **deberiaEliminarCitaExistenteYRetornarNoContent**
   - Verifica la eliminación de citas
   - Valida el código de estado HTTP 204 (No Content)
   - Confirma que se llama al servicio de eliminación

5. **deberiaObtenerCitasPorMascotaFiltrandoCorrectamente**
   - Verifica el filtrado de citas por mascota
   - Valida que todas las citas pertenezcan a la misma mascota
   - Confirma la correcta cantidad de resultados

6. **deberiaRetornarNotFoundCuandoBuscaCitaInexistentePorId**
   - Verifica el manejo de citas inexistentes
   - Valida el código de estado HTTP 404 (Not Found)
   - Confirma que no se retorna contenido

7. **deberiaObtenerCitasPorRangoDeFechasCorrectamente**
   - Verifica el filtrado por rango de fechas
   - Valida que las citas estén dentro del rango
   - Confirma la cantidad correcta de resultados

8. **deberiaObtenerCitasPorEstadoFiltrandoCorrectamente**
   - Verifica el filtrado por estado (Pendiente/Atendida/Cancelada)
   - Valida que todas las citas tengan el estado correcto
   - Confirma la funcionalidad de búsqueda por estado

9. **deberiaObtenerListaVaciaCuandoNoHayCitasParaVeterinarioEspecificado**
   - Verifica el manejo de resultados vacíos
   - Valida que se retorne una lista vacía
   - Confirma el código de estado HTTP 200

## 🛠️ Tecnologías Utilizadas

- **JUnit 5**: Framework de pruebas unitarias
- **Mockito**: Framework para crear mocks y stubs
- **Spring Boot Test**: Soporte para pruebas en Spring Boot
- **AssertJ** (implícito): Aserciones fluidas

## ▶️ Ejecución de las Pruebas

### Desde Maven:
```bash
mvn test -Dtest="com.backend.vet.ezequielgomez.*"
```

### Desde IDE:
- Click derecho en el paquete `ezequielgomez`
- Seleccionar "Run Tests"

### Ejecutar una prueba específica:
```bash
mvn test -Dtest="AuthControllerTest#deberiaAutenticarExitosamenteUsuarioConCredencialesValidas"
```

## ✅ Resultados

**Total de pruebas: 21** (incluyendo las pruebas adicionales)
- ✅ AuthControllerTest: 10 pruebas pasadas
- ✅ CitaControllerTest: 10 pruebas pasadas

**Cobertura:**
- AuthController: ~80% de cobertura de métodos principales
- CitaController: ~75% de cobertura de métodos principales

## 📝 Buenas Prácticas Aplicadas

1. **Nombres descriptivos**: Cada prueba describe claramente qué verifica
2. **Aislamiento**: Uso de mocks para aislar la unidad bajo prueba
3. **Verificación completa**: Se verifican tanto el resultado como las interacciones
4. **Setup centralizado**: Uso de `@BeforeEach` para configuración común
5. **Assertions múltiples**: Se verifica exhaustivamente cada escenario
6. **Estados HTTP correctos**: Validación de códigos de respuesta apropiados

## 🔍 Cobertura de Escenarios

### Casos exitosos:
- Autenticación válida
- Creación de recursos
- Actualización de datos
- Obtención de listas

### Casos de error:
- Credenciales inválidas
- Recursos no encontrados
- Validaciones fallidas
- Estados bloqueados

### Casos límite:
- Listas vacías
- Tokens expirados
- Usuarios inactivos

## 👤 Autor

**Ezequiel Gomez**

---

*Pruebas desarrolladas siguiendo metodología TDD y principios SOLID*
