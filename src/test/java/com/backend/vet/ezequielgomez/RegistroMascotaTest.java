package com.backend.vet.ezequielgomez;

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

public class RegistroMascotaTest {
    
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
    public void verificarRegistroMascota() {
        
        // 1. Navegar a Pacientes
        driver.get(baseUrl + "/patients"); 

        // 2. Abrir formulario (Esperar botón "Nueva Mascota")
        WebElement btnNueva = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(., 'Nueva Mascota')]")));
        btnNueva.click();

        // 3. Llenar formulario
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre"))).sendKeys("Bobby Test");
        
        // Especie (Select)
        new Select(driver.findElement(By.id("especie"))).selectByVisibleText("Perro");
        
        driver.findElement(By.id("raza")).sendKeys("Golden Retriever");
        driver.findElement(By.id("fechaNacimiento")).sendKeys("01-05-2020");
        
        new Select(driver.findElement(By.id("sexo"))).selectByVisibleText("Macho");

        // Cliente (Usamos index 1 porque el 0 es el placeholder)
        new Select(driver.findElement(By.id("clienteId"))).selectByIndex(1); 

        // 4. Guardar
        WebElement btnGuardar = driver.findElement(By.xpath("//button[@type='submit']"));
        btnGuardar.click();

        // --- VERIFICACIÓN (Sin Refresh) ---
        
        // Esperamos a que el formulario desaparezca (volvemos a la lista)
        wait.until(ExpectedConditions.invisibilityOf(btnGuardar));
        
        // Esperamos a que la tabla de la lista sea visible nuevamente
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        
        // Buscamos el texto en el cuerpo de la página
        WebElement body = driver.findElement(By.tagName("body"));
        Assert.assertTrue(body.getText().contains("Bobby Test"), "La mascota no aparece en la lista tras el registro.");
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}