package com.backend.vet.ignaciotapia.atdd;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class CreacionClienteExitosaTest {
    
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
    public void verificarCreacionCliente() throws Exception {
        
        // 1. Navegar a Clientes
        driver.get(baseUrl + "/clients");

        // 2. Esperar a que la página cargue completamente
        Thread.sleep(2000);
        
        // 3. Buscar el botón "Nuevo Cliente" con diferentes estrategias
        WebElement btnNuevo = null;
        try {
            // Intentar por texto exacto
            btnNuevo = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Nuevo Cliente')]")));
        } catch (Exception e1) {
            try {
                // Intentar por texto parcial
                btnNuevo = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(., 'Nuevo')]")));
            } catch (Exception e2) {
                // Intentar por clase común de botones
                btnNuevo = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.bg-teal-600, button.bg-blue-600")));
            }
        }
        
        btnNuevo.click();

        // 4. Esperar a que el formulario sea visible y llenar campos
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre")));
        
        driver.findElement(By.id("nombre")).sendKeys("María");
        driver.findElement(By.id("apellido")).sendKeys("González");
        driver.findElement(By.id("correo")).sendKeys("maria.gonzalez@ejemplo.com");
        driver.findElement(By.id("telefono")).sendKeys("555-123-4567");
        driver.findElement(By.id("direccion")).sendKeys("Av. Principal 123");

        // 5. Guardar
        WebElement btnGuardar = driver.findElement(By.xpath("//button[@type='submit']"));
        btnGuardar.click();

        // 6. Esperar a que se procese el envío
        Thread.sleep(1500);

        // --- VERIFICACIÓN ---
        
        // Verificar que el cliente aparece en la lista o que hay mensaje de éxito
        try {
            // Intentar encontrar mensaje de éxito
            WebElement mensajeExito = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'bg-green-100')]")));
            Assert.assertTrue(mensajeExito.isDisplayed(), "No se mostró mensaje de éxito");
            System.out.println("Mensaje de éxito: " + mensajeExito.getText());
        } catch (Exception e) {
            // Si no hay mensaje de éxito, verificar que aparezca en la tabla
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
            WebElement body = driver.findElement(By.tagName("body"));
            Assert.assertTrue(
                body.getText().contains("María") || body.getText().contains("González"), 
                "El cliente no aparece en la lista"
            );
            Assert.assertTrue(
                body.getText().contains("555-123-4567") || body.getText().contains("maria.gonzalez@ejemplo.com"), 
                "Los datos del cliente no aparecen en la lista"
            );
        }
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}