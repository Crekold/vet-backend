package com.backend.vet.alejandralandaeta.atdd;

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

public class LoginUsuarioInactivoTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl = "http://localhost:5173";

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void verificarLoginUsuarioInactivo() {

        /********** Preparación de la prueba **********/
        driver.get(baseUrl + "/login");

        /*********** Lógica de la prueba ***********/
        
        // 1. Campo usuario visible
        WebElement userField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombreUsuario")));
        userField.clear();
        userField.sendKeys("usuarioInactivo"); // Usuario creado en DataInitializer

        // 2. Contraseña
        WebElement passField = driver.findElement(By.id("contrasena"));
        passField.clear();
        passField.sendKeys("123456");

        // 3. Clic en iniciar sesión
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        /************ Verificación del resultado esperado ***************/

        // Esperar mensaje de error de login
        try {
            WebElement alerta = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Error al iniciar sesión')]")
            ));
            Assert.assertTrue(alerta.isDisplayed(), "El mensaje de error no se muestra correctamente para usuarios inactivos.");
        } catch (Exception e) {
            Assert.fail("No se mostró mensaje de error para usuario inactivo.");
        }
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}