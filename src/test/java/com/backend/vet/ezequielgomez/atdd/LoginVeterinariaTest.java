package com.backend.vet.ezequielgomez.atdd; // Asegúrate que el paquete coincida con tu estructura

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

public class LoginVeterinariaTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    // Puerto de Vite (Frontend)
    private String baseUrl = "http://localhost:5173"; 

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Configurar espera explícita de hasta 10 segundos
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void verificarLoginExitoso() {
        
        /********** Preparación de la prueba **********/
        driver.get(baseUrl + "/login");

        /*********** Lógica de la prueba ***********/
        
        // 1. Esperar a que el campo usuario sea visible (Maneja la animación fade-in)
        WebElement userField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombreUsuario"))); // ID CORREGIDO
        userField.clear();
        userField.sendKeys("admin"); // Usuario que exista en tu BD (ej: 'admin' o 'veterinario1')

        // 2. Ingresar contraseña
        WebElement passField = driver.findElement(By.id("contrasena")); // ID CORREGIDO
        passField.clear();
        passField.sendKeys("admin"); // Contraseña correcta para ese usuario

        // 3. Presionar el botón Ingresar
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        /************ Verificación del resultado esperado - Assert ***************/
        
        // Esperar a que la URL cambie a /dashboard
        try {
            wait.until(ExpectedConditions.urlContains("/dashboard"));
        } catch (Exception e) {
            Assert.fail("El login no redirigió al dashboard. URL actual: " + driver.getCurrentUrl());
        }
        
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/dashboard"), "No se redirigió correctamente al Dashboard");
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}