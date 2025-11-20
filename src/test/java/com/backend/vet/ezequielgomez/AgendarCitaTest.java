package com.backend.vet.ezequielgomez;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor; // [NUEVO] Necesario para la fecha
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class AgendarCitaTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl = "http://localhost:5173";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // --- LOGIN ---
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombreUsuario"))).sendKeys("admin");
        driver.findElement(By.id("contrasena")).sendKeys("admin");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void verificarAgendamientoCita() {
        
        // 1. Ir a Citas
        driver.get(baseUrl + "/appointments"); 

        // 2. Click en Nueva Cita
        WebElement btnNueva = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(., 'Nueva Cita')]")));
        btnNueva.click();

        // --- FLUJO DEPENDIENTE ---
        
        // A. Seleccionar Cliente
        WebElement clienteSelectElem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("clienteId")));
        new Select(clienteSelectElem).selectByIndex(1); 

        // B. Esperar a que cargue la mascota
        WebElement mascotaSelectElem = driver.findElement(By.id("mascotaId"));
        wait.until(ExpectedConditions.elementToBeClickable(mascotaSelectElem));
        try {
            new Select(mascotaSelectElem).selectByIndex(1); 
        } catch (Exception e) {
            Assert.fail("El cliente seleccionado no tiene mascotas. Asegúrate de correr DataInitializer.");
        }

        // C. Seleccionar Veterinario
        new Select(driver.findElement(By.id("usuarioId"))).selectByIndex(1);

        // D. Fecha y Hora (USANDO JAVASCRIPT PARA EVITAR ERRORES DE FORMATO)
        // Esto fuerza el valor '2025-12-12' directamente en el input, sin importar si tu Windows está en español o inglés.
        WebElement fechaInput = driver.findElement(By.id("fecha"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '2025-12-12';", fechaInput);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input'));", fechaInput); // Notificar a Vue

        // CORRECCIÓN: Usar JS también para la hora para evitar problemas con el time-picker
        WebElement horaInput = driver.findElement(By.id("hora"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '10:30';", horaInput);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input'));", horaInput);

        // E. Motivo
        driver.findElement(By.id("motivo")).sendKeys("Consulta General con Selenium");

        // 3. Guardar
        WebElement btnGuardar = driver.findElement(By.xpath("//button[contains(., 'Crear Cita')]"));
        btnGuardar.click();

        // --- VERIFICACIÓN Y MANEJO DE ERRORES ---
        
        // Verificamos si aparece una ALERTA de error del navegador (window.alert)
        try {
            // Esperamos un momento breve por si sale una alerta
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.alertIsPresent());
            String alertText = driver.switchTo().alert().getText();
            driver.switchTo().alert().accept(); // Cerrar alerta
            Assert.fail("Error del Backend detectado: " + alertText); // Fallar el test con el mensaje real
        } catch (Exception e) {
            // No hubo alerta, continuamos normal
        }

        // Esperar a que el botón desaparezca (señal de éxito)
        try {
            wait.until(ExpectedConditions.invisibilityOf(btnGuardar));
        } catch (Exception e) {
             // Si falla aquí, puede ser validación visual (ej. campo requerido)
             // Imprimimos el error para debug
             System.out.println("El formulario no se cerró. Posible error de validación en pantalla.");
             throw e;
        }

        // Verificar en la tabla
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        WebElement body = driver.findElement(By.tagName("body"));
        Assert.assertTrue(body.getText().contains("Consulta General con Selenium"), "La cita no aparece en la lista.");
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}