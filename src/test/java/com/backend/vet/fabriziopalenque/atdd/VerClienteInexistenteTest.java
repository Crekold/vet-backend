package com.backend.vet.fabriziopalenque.atdd;

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

public class VerClienteInexistenteTest {
    
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
    public void verificarClienteInexistente() throws InterruptedException {
        /********** PASO 1: INGRESAR AL SISTEMA **********/
        driver.get(baseUrl + "/login");
        
        // Manejo de múltiples ventanas (Vite/HMR)
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
        }

        esperarDOMEstable();

        // Login
        WebElement userField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombreUsuario")));
        userField.clear();
        userField.sendKeys("admin");

        WebElement passField = driver.findElement(By.id("contrasena"));
        passField.clear();
        passField.sendKeys("admin");

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        // Esperar a que navegue al dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        Thread.sleep(1000);
        esperarDOMEstable();

        /********** PASO 2: NAVEGAR A CLIENTE INEXISTENTE **********/
        // Intentar acceder a un ID que no existe
        driver.get(baseUrl + "/clients/9999");
        
        esperarDOMEstable();

        /*********** PASO 3: VERIFICAR MENSAJE DE ERROR ***********/
        // Buscar mensaje de error genérico o específico (toast, texto en página, etc.)
        By errorLocator = By.xpath(
            "//*[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'no encontrado') or " +
            "contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'no existe') or " +
            "contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'error') or " +
            "@id='not-found-message']"
        );
        
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator));
        Assert.assertTrue(errorMessage.isDisplayed(), "No se mostró mensaje de error para cliente inexistente");
        
        System.out.println("✓ Test PASSED: Manejo de cliente inexistente verificado.");
    }

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
                    return true; 
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