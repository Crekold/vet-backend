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

public class ListarClientesTest {
    
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
    public void verificarListadoDeClientes() throws InterruptedException {
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

        /********** PASO 2: NAVEGAR A CLIENTES **********/
        // Navegar al apartado "Clientes"
        WebElement clientesLink = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(),'Clientes')]")
            )
        );
        clientesLink.click();

        esperarDOMEstable();

        /*********** PASO 3: VERIFICAR LISTADO ***********/
        // Verificar que estamos en la URL correcta
        wait.until(ExpectedConditions.urlContains("/clients"));

        // Verificar que aparece el título
        WebElement titulo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Clientes')]")));
        Assert.assertTrue(titulo.isDisplayed(), "El título de Clientes no se muestra");

        // Verificar que hay una tabla visible
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        Assert.assertTrue(table.isDisplayed(), "La tabla de clientes no se muestra.");
        
        System.out.println("✓ Test PASSED: Listado de clientes verificado correctamente.");
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