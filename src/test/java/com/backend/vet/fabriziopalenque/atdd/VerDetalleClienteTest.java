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

public class VerDetalleClienteTest {
    
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
    public void verificarDetalleDeCliente() throws InterruptedException {
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
        WebElement clientesLink = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(),'Clientes')]")
            )
        );
        clientesLink.click();

        esperarDOMEstable();

        /********** PASO 3: BUSCAR CLIENTE Y VER DETALLES **********/
        // Usamos "Juan" que sabemos que se crea en DataInitializer
        String nombreCliente = "Juan";
        
        // Buscar botón de acción (Editar/Ver)
        // Usamos un wait explícito para encontrar el botón directamente dentro de la fila del cliente
        // Esto es más robusto que buscar la fila y luego el botón sin espera
        WebElement actionButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//tr[contains(., '" + nombreCliente + "')]//button")
        ));
        actionButton.click();

        esperarDOMEstable();

        /********** PASO 4: VERIFICAR VISTA DE DETALLE/EDICIÓN **********/
        // Verificar que el nombre está presente en el formulario o vista
        // Puede ser un input con value='Juan' o un texto 'Juan'
        
        boolean nombreEncontrado = false;
        try {
            // Caso 1: Es un formulario de edición (input)
            WebElement inputNombre = driver.findElement(By.xpath("//input[@value='" + nombreCliente + "']"));
            nombreEncontrado = inputNombre.isDisplayed();
        } catch (Exception e) {
            try {
                // Caso 2: Es una vista de detalles (texto)
                WebElement textoNombre = driver.findElement(By.xpath("//*[contains(text(), '" + nombreCliente + "')]"));
                nombreEncontrado = textoNombre.isDisplayed();
            } catch (Exception ex) {
                nombreEncontrado = false;
            }
        }

        Assert.assertTrue(nombreEncontrado, "No se encontró el nombre del cliente '" + nombreCliente + "' en la vista de detalles/edición");
        
        System.out.println("✓ Test PASSED: Detalle de cliente verificado correctamente.");
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