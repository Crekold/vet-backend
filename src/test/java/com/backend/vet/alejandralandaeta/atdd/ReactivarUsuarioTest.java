package com.backend.vet.alejandralandaeta.atdd;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Test Case: Verificar que se pueda reactivar a un usuario que fue marcado como inactivo
 * 
 * Pre-condiciones:
 * - Tener acceso al sistema
 * - Tener al menos un usuario con permisos USUARIO_UPDATE y USUARIO_READ
 * - Tener al menos un usuario marcado como inactivo (creado en DataInitializer)
 * 
 * Pasos esperados:
 * 1. Ingresar al sistema
 * 2. Ingresar al apartado "Gestionar Usuarios" y marcar "Mostrar inactivos"
 * 3. Seleccionar el icono con flecha hacia arriba sobre el usuario inactivo
 * 4. Confirmar reactivación
 * 
 * Resultado esperado: El usuario aparece con estado "Activo"
 */
public class ReactivarUsuarioTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl = "http://localhost:5173";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    public void verificarReactivacionUsuario() throws Exception {

        /********** PASO 1: INGRESAR AL SISTEMA **********/
        driver.get(baseUrl + "/login");
        
        // Manejo de múltiples ventanas (Vite/HMR)
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
        }

        esperarDOMEstable();

        // Login con usuario que tiene permisos USUARIO_UPDATE y USUARIO_READ
        WebElement userField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombreUsuario")));
        userField.clear();
        userField.sendKeys("admin"); // Usuario ADMIN_USUARIOS creado en DataInitializer

        WebElement passField = driver.findElement(By.id("contrasena"));
        passField.clear();
        passField.sendKeys("admin");

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        // Esperar a que navegue al dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        Thread.sleep(1000); // Pequeña pausa para que Vue termine de renderizar
        esperarDOMEstable();

        /********** PASO 2: NAVEGAR A "GESTIONAR USUARIOS" Y MOSTRAR INACTIVOS **********/
        
        // Navegar al apartado "Gestionar Usuarios"
        WebElement gestionarUsuariosLink = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(),'Gestionar Usuarios')]")
            )
        );
        clickElemento(gestionarUsuariosLink);

        esperarDOMEstable();

        // Marcar la opción "Mostrar inactivos"
        WebElement showInactiveCheckbox = wait.until(
            ExpectedConditions.elementToBeClickable(By.id("showInactive"))
        );
        
        // Verificar si el checkbox está desmarcado y hacerlo click
        if (!showInactiveCheckbox.isSelected()) {
            clickElemento(showInactiveCheckbox);
        }

        esperarDOMEstable();
        Thread.sleep(500); // Pequeña pausa para actualizar tabla con usuarios inactivos

        /********** PASO 3: BUSCAR Y REACTIVAR EL USUARIO INACTIVO **********/
        
        // Buscar la fila del usuario inactivo "usuario_inactivo" (creado en DataInitializer)
        WebElement filaUsuario = buscarFilaUsuario("usuario_inactivo");
        Assert.assertNotNull(filaUsuario, "No se encontró la fila del usuario 'usuario_inactivo'");

        // Verificar que el usuario está marcado como INACTIVO antes de reactivar
        WebElement estadoAntes = filaUsuario.findElement(By.xpath(".//span[contains(text(),'Inactivo')]"));
        Assert.assertTrue(estadoAntes.isDisplayed(), "El usuario no aparece como INACTIVO antes de reactivarlo");

        // Buscar el botón de REACTIVAR (flecha hacia arriba)
        WebElement btnReactivar = filaUsuario.findElement(
            By.xpath(".//button[@title='Reactivar usuario']")
        );
        clickElemento(btnReactivar);

        esperarDOMEstable();

        /********** PASO 4: CONFIRMAR REACTIVACIÓN EN EL MODAL **********/
        
        // Esperar a que aparezca el modal de confirmación (div fixed con fondo oscuro)
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'fixed') and contains(@class, 'inset-0')]//div[contains(@class, 'bg-white')]")
            )
        );

        // Buscar y hacer click al botón "Reactivar" en el modal (botón verde)
        WebElement btnConfirmarReactivar = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'bg-green-600') and contains(text(), 'Reactivar')]")
            )
        );
        clickElemento(btnConfirmarReactivar);

        esperarDOMEstable();
        Thread.sleep(800); // Esperar actualización de tabla

        /********** VERIFICACIÓN: USUARIO APARECE COMO ACTIVO **********/
        
        // Recargar la fila después del cambio (puede haber sido reinsertada por Vue)
        filaUsuario = buscarFilaUsuario("usuario_inactivo");
        Assert.assertNotNull(filaUsuario, "No se encontró la fila del usuario tras reactivarlo");

        // Verificar que el usuario ahora aparece con estado ACTIVO
        WebElement estadoDespues = filaUsuario.findElement(By.xpath(".//span[contains(text(),'Activo')]"));
        Assert.assertTrue(estadoDespues.isDisplayed(), 
            "El usuario NO aparece como ACTIVO tras reactivarlo. Estado esperado: Activo");

        System.out.println("✓ Test PASSED: Usuario 'usuario_inactivo' reactivado exitosamente.");
    }

    /*************** MÉTODOS AUXILIARES ROBUSTOS *****************/

    /**
     * Busca una fila en la tabla por el nombre de usuario
     * Maneja StaleElementReferenceException causadas por re-renders de Vue
     */
    private WebElement buscarFilaUsuario(String nombreUsuario) {
        return wait.until(driver1 -> {
            try {
                List<WebElement> filas = driver1.findElements(By.xpath("//tr"));
                for (WebElement fila : filas) {
                    if (fila.getText().contains(nombreUsuario)) {
                        return fila;
                    }
                }
                return null;
            } catch (StaleElementReferenceException ex) {
                return null; // Reintentar
            }
        });
    }

    /**
     * Ejecuta click de forma robusta con fallback a JavaScript
     */
    private void clickElemento(WebElement el) {
        try {
            el.click();
        } catch (Exception ex) {
            // Fallback: usar JavaScript click (para overlays o elementos no clickeables)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    /**
     * Espera a que el DOM se estabilice (útil con Vue/React que constantemente re-renderizan)
     * Maneja excepciones si la conexión DevTools se pierde
     */
    private void esperarDOMEstable() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            ExpectedCondition<Boolean> domListo = webDriver -> {
                try {
                    Long size1 = (Long) js.executeScript("return document.getElementsByTagName('*').length;");
                    if (size1 == null) return true;
                    Thread.sleep(100);
                    Long size2 = (Long) js.executeScript("return document.getElementsByTagName('*').length;");
                    if (size2 == null) return true;
                    return size1.equals(size2);
                } catch (Exception ex) {
                    return true; // Si hay error, asumir que está listo
                }
            };
            wait.until(domListo);
        } catch (Exception ex) {
            System.out.println("Advertencia: esperarDOMEstable falló, continuando...");
        }
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}