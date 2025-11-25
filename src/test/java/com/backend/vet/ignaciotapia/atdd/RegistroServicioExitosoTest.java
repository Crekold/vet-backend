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

public class RegistroServicioExitosoTest {
    
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
    public void verificarRegistroServicio() throws Exception {
        
        // 1. Navegar a Servicios
        driver.get(baseUrl + "/services");

        // 2. Click en Nuevo Servicio
        WebElement btnNuevo = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(., 'Nuevo Servicio')]")));
        btnNuevo.click();

        // 3. Esperar a que el formulario sea visible y llenar campos
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre")));
        
        driver.findElement(By.id("nombre")).sendKeys("Vacunación Antirrábica");
        driver.findElement(By.id("descripcion")).sendKeys("Aplicación de vacuna antirrábica para perros y gatos");
        driver.findElement(By.id("precio")).sendKeys("350.00");

        // 4. Guardar
        WebElement btnGuardar = driver.findElement(By.xpath("//button[@type='submit']"));
        btnGuardar.click();

        // 5. Esperar a que se procese el envío
        Thread.sleep(1500);

        // --- VERIFICACIÓN ---
        
        // Verificar que el servicio aparece en la lista o que hay mensaje de éxito
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
            Assert.assertTrue(body.getText().contains("Vacunación Antirrábica"), "El servicio no aparece en la lista");
            Assert.assertTrue(body.getText().contains("350"), "El precio del servicio no aparece en la lista");
        }
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}