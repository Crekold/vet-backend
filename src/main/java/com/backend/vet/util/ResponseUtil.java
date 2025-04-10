package com.backend.vet.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Clase utilitaria para estandarizar las respuestas HTTP en toda la aplicación
 */
public class ResponseUtil {

    /**
     * Crea una respuesta exitosa (200 OK) con el cuerpo proporcionado
     *
     * @param body el cuerpo de la respuesta
     * @return ResponseEntity con estado 200 OK
     */
    public static <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    /**
     * Crea una respuesta de creación exitosa (201 Created) con el cuerpo proporcionado
     *
     * @param body el cuerpo de la respuesta
     * @return ResponseEntity con estado 201 Created
     */
    public static <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Crea una respuesta sin contenido (204 No Content)
     *
     * @return ResponseEntity con estado 204 No Content
     */
    public static <T> ResponseEntity<T> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Crea una respuesta de error no encontrado (404 Not Found)
     *
     * @return ResponseEntity con estado 404 Not Found
     */
    public static <T> ResponseEntity<T> notFound() {
        return ResponseEntity.notFound().build();
    }

    /**
     * Crea una respuesta para una operación de eliminación basada en el resultado
     *
     * @param deleted indica si la operación de eliminación tuvo éxito
     * @return ResponseEntity con estado 204 No Content si tuvo éxito, o 404 Not Found si no
     */
    public static <T> ResponseEntity<T> deleteResponse(boolean deleted) {
        return deleted ? noContent() : notFound();
    }
}
