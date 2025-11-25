package com.backend.vet.ignaciotapia.atdd;

import java.time.Duration;
import org.openqa.selenium.By;
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

public class ValidacionContrasenasDebilesTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl = "http://localhost:5173";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // --- LOGIN CON PERMISOS ---
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombreUsuario"))).sendKeys("admin");
        driver.findElement(By.id("contrasena")).sendKeys("admin");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test(priority = 1)
    public void verificarContrasenaCorta() throws Exception {
        
        // 1. Navegar directamente a página de registro de usuarios
        driver.get(baseUrl + "/users/register");

        // 2. Esperar a que el formulario esté visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombreUsuario")));

        // 3. Llenar formulario con contraseña corta (5 caracteres, menos de 8)
        driver.findElement(By.id("nombreUsuario")).sendKeys("usuarioprueba");
        driver.findElement(By.id("correo")).sendKeys("usuario.prueba@vetclinica.com");
        
        WebElement campoContrasena = driver.findElement(By.id("contrasena"));
        campoContrasena.sendKeys("Abc1!");
        
        // Seleccionar rol
        new Select(driver.findElement(By.id("rol"))).selectByIndex(1);

        // 4. Intentar guardar - la validación HTML5 debe bloquear el envío
        WebElement btnGuardar = driver.findElement(By.cssSelector("button[type='submit']"));
        btnGuardar.click();

        // 5. Esperar un momento
        Thread.sleep(500);

        // --- VERIFICACIÓN ---
        // Verificar validación HTML5 (pattern requiere mínimo 8 caracteres)
        String validationMessage = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("return arguments[0].validationMessage;", campoContrasena);
        
        System.out.println("Validación HTML5: " + validationMessage);
        
        // Verificar que hay validación HTML5 o buscar mensaje de error del backend
        if (validationMessage != null && !validationMessage.isEmpty()) {
            Assert.assertTrue(true, "Validación HTML5 funcionó correctamente: " + validationMessage);
        } else {
            // Si pasa la validación HTML5, debe haber mensaje de error del backend
            WebElement mensajeError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'bg-red-100')]")));
            
            String textoError = mensajeError.getText();
            System.out.println("Mensaje de error del backend: " + textoError);
            Assert.assertTrue(
                textoError.toLowerCase().contains("contraseña") || textoError.toLowerCase().contains("password"),
                "Se esperaba mensaje de error de contraseña"
            );
        }
    }

    @Test(priority = 2)
    public void verificarContrasenaSinMayusculas() throws Exception {
        
        // 1. Navegar directamente a página de registro
        driver.get(baseUrl + "/users/register");

        // 2. Esperar a que el formulario esté visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombreUsuario")));

        // 3. Llenar formulario con contraseña sin mayúsculas (no cumple pattern)
        driver.findElement(By.id("nombreUsuario")).sendKeys("usuarioprueba2");
        driver.findElement(By.id("correo")).sendKeys("usuario.prueba2@vetclinica.com");
        
        WebElement campoContrasena = driver.findElement(By.id("contrasena"));
        campoContrasena.sendKeys("contraseña123!");
        
        new Select(driver.findElement(By.id("rol"))).selectByIndex(1);

        // 4. Intentar guardar
        WebElement btnGuardar = driver.findElement(By.cssSelector("button[type='submit']"));
        btnGuardar.click();

        // 5. Esperar un momento
        Thread.sleep(500);

        // --- VERIFICACIÓN ---
        String validationMessage = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("return arguments[0].validationMessage;", campoContrasena);
        
        System.out.println("Validación HTML5: " + validationMessage);
        
        if (validationMessage != null && !validationMessage.isEmpty()) {
            Assert.assertTrue(true, "Validación HTML5 funcionó correctamente: " + validationMessage);
        } else {
            // Si pasa la validación HTML5, debe haber mensaje de error del backend
            WebElement mensajeError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'bg-red-100')]")));
            
            String textoError = mensajeError.getText();
            System.out.println("Mensaje de error del backend: " + textoError);
            Assert.assertTrue(
                textoError.toLowerCase().contains("mayúscula") || textoError.toLowerCase().contains("formato"),
                "Se esperaba mensaje de error de formato de contraseña"
            );
        }
    }

    @Test(priority = 3)
    public void verificarContrasenaComun() throws Exception {
        
        // 1. Navegar directamente a página de registro
        driver.get(baseUrl + "/users/register");

        // 2. Esperar a que el formulario esté visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombreUsuario")));

        // 3. Llenar formulario con contraseña común (cumple pattern pero es débil)
        driver.findElement(By.id("nombreUsuario")).sendKeys("usuarioprueba3");
        driver.findElement(By.id("correo")).sendKeys("usuario.prueba3@vetclinica.com");
        driver.findElement(By.id("contrasena")).sendKeys("Password123!");
        
        new Select(driver.findElement(By.id("rol"))).selectByIndex(1);

        // 4. Intentar guardar - esta contraseña cumple el pattern HTML5
        WebElement btnGuardar = driver.findElement(By.cssSelector("button[type='submit']"));
        btnGuardar.click();

        // 5. Esperar respuesta del backend
        Thread.sleep(1500);

        // --- VERIFICACIÓN ---
        // Esta contraseña pasa validación HTML5, así que debe ser rechazada por el backend
        try {
            WebElement mensajeError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'bg-red-100')]")));
            
            String textoError = mensajeError.getText();
            System.out.println("Mensaje de error del backend: " + textoError);
            
            Assert.assertTrue(
                textoError.toLowerCase().contains("común") || 
                textoError.toLowerCase().contains("débil") || 
                textoError.toLowerCase().contains("password") ||
                textoError.toLowerCase().contains("error"),
                "Se esperaba mensaje de error para contraseña común/débil. Mensaje recibido: " + textoError
            );
        } catch (org.openqa.selenium.TimeoutException e) {
            // Si no hay mensaje de error, el usuario se creó exitosamente
            // Verificar si hay mensaje de éxito
            try {
                WebElement mensajeExito = driver.findElement(By.xpath("//div[contains(@class, 'bg-green-100')]"));
                if (mensajeExito.isDisplayed()) {
                    Assert.fail("La contraseña común 'Password123!' fue aceptada cuando debería ser rechazada");
                }
            } catch (Exception ex) {
                Assert.fail("No se mostró ningún mensaje de error para contraseña común");
            }
        }
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}