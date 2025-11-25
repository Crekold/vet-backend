package com.backend.vet.alejandralandaeta.atdd;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
 * Test Case: Comprobar si es posible ingresar un precio negativo 
 *              en la creación/edición de un servicio
 * 
 * Pre-condiciones:
 * - Tener acceso al sistema
 * - Tener un usuario con permisos SERVICIO_CREATE, SERVICIO_UPDATE y SERVICIO_READ
 * - Tener al menos un servicio creado (creado en DataInitializer)
 * 
 * Pasos esperados:
 * 1. Ingresar al sistema
 * 2. Ingresar al apartado "Servicios" → Seleccionar "+ Nuevo Servicio"
 * 3. Ingresar datos con precio negativo y hacer click en "Crear Servicio"
 * 4. El sistema debe mostrar un mensaje indicando que el precio debe ser mayor a 0
 * 
 * Resultado esperado: Mensaje de validación en el campo "Precio"
 */
public class PrecioNegativoServicioTest {

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
    public void verificarValidacionPrecioNegativoServicio() throws Exception {

        /********** PASO 1: INGRESAR AL SISTEMA **********/
        driver.get(baseUrl + "/login");

        // Manejo de múltiples ventanas (Vite/HMR)
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
        }

        esperarDOMEstable();

        // Login con usuario que tiene permisos de servicio
        WebElement userField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("nombreUsuario"))
        );
        userField.clear();
        userField.sendKeys("admin"); // Usuario ADMIN_USUARIOS con permisos de servicio

        WebElement passField = driver.findElement(By.id("contrasena"));
        passField.clear();
        passField.sendKeys("admin");

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        // Esperar a que navegue al dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        Thread.sleep(1000); // Pequeña pausa para que Vue termine de renderizar
        esperarDOMEstable();

        /********** PASO 2: NAVEGAR A SERVICIOS Y CREAR NUEVO **********/
        
        // Navegar al apartado de Servicios
        WebElement serviciosLink = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(),'Servicios')]")
            )
        );
        clickElemento(serviciosLink);

        esperarDOMEstable();

        // Hacer click en "+ Nuevo Servicio"
        WebElement btnNuevoServicio = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Nuevo Servicio') or contains(., '+ Nuevo')]")
            )
        );
        clickElemento(btnNuevoServicio);

        esperarDOMEstable();

        /********** PASO 3: LLENAR FORMULARIO CON PRECIO NEGATIVO **********/
        
        // Esperar visibilidad de los campos del formulario
        WebElement nombreField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("nombre"))
        );

        // Llenar datos del servicio
        nombreField.clear();
        nombreField.sendKeys("Servicio Test Negativo");

        WebElement descripcionField = driver.findElement(By.id("descripcion"));
        descripcionField.clear();
        descripcionField.sendKeys("Descripción de prueba para validación");

        // INGRESAR PRECIO NEGATIVO
        WebElement precioField = driver.findElement(By.id("precio"));
        precioField.clear();
        precioField.sendKeys("-50.00"); // Precio negativo para validación

        esperarDOMEstable();

        /********** PASO 4: INTENTAR CREAR SERVICIO CON PRECIO NEGATIVO **********/
        
        // Buscar y hacer click al botón "Crear Servicio"
        WebElement btnCrearServicio = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Crear Servicio') or contains(., 'Guardar')]")
            )
        );
        clickElemento(btnCrearServicio);

        esperarDOMEstable();

        /********** VERIFICACIÓN: MENSAJE DE ERROR EN CAMPO PRECIO **********/
        
        // El frontend tiene dos niveles de validación:
        // 1. HTML5 nativo (min="0") - muestra tooltip del navegador
        // 2. Vue.js (validateForm) - muestra "El precio debe ser mayor que cero"
        
        // Primero verificar si el campo precio tiene validación HTML5 fallida
        WebElement precioFieldValidation = driver.findElement(By.id("precio"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // Verificar validez del campo HTML5
        Boolean isInvalid = (Boolean) js.executeScript(
            "return !arguments[0].validity.valid;", precioFieldValidation
        );
        
        // También buscar mensaje de error de Vue
        By errorMensajeVue = By.xpath(
            "//*[" +
            "contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'mayor que cero') or " +
            "contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'el precio debe') or " +
            "contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'mayor o igual')" +
            "]"
        );
        
        boolean hayMensajeVue = false;
        try {
            WebElement mensajeError = new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOfElementLocated(errorMensajeVue));
            hayMensajeVue = mensajeError.isDisplayed();
        } catch (Exception e) {
            // No se encontró mensaje de Vue, verificar validación HTML5
            hayMensajeVue = false;
        }
        
        // El test pasa si: hay validación HTML5 inválida O hay mensaje de error de Vue
        Assert.assertTrue(isInvalid || hayMensajeVue, 
            "No se detectó validación de precio negativo (ni HTML5 ni mensaje Vue)");

        // Verificar que el formulario NO fue enviado (seguimos en la página de creación)
        WebElement formServicio = driver.findElement(By.id("nombre")); // El campo nombre debe seguir existiendo
        Assert.assertTrue(formServicio.isDisplayed(), 
            "El formulario fue enviado a pesar del precio negativo");

        System.out.println("✓ Test PASSED: Validación de precio negativo funcionando correctamente.");
    }

    /*************** MÉTODOS AUXILIARES ROBUSTOS *****************/

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