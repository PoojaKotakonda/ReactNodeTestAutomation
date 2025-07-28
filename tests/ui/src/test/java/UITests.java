import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UITests {

    static WebDriver driver;
    static WebDriverWait wait;

    @BeforeAll
    public static void setup() {
        // Setup Chrome driver with headless mode for CI
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        driver.get("http://localhost:3000");
    }

    @Test
    @Order(1)
    @DisplayName("Login with invalid credentials should stay on login page")
    public void loginWithInvalidCredentials() {
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("input[placeholder='Username']")));
        WebElement passwordField = driver.findElement(By.cssSelector("input[placeholder='Password']"));
        WebElement loginButton = driver.findElement(By.tagName("button"));

        usernameField.clear();
        usernameField.sendKeys("wrong");
        passwordField.clear();
        passwordField.sendKeys("wrong");
        loginButton.click();

        // Wait for potential alert and dismiss it
        try {
            Thread.sleep(1000); // Brief wait for alert
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            // Alert might not appear, continue
        }

        Assertions.assertTrue(driver.getPageSource().contains("Login"), 
            "User should remain on login page after invalid login");
    }

    @Test
    @Order(2)
    @DisplayName("Login with valid credentials should navigate to Todo List page")
    public void loginWithValidCredentials() {
        driver.navigate().refresh();
        
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("input[placeholder='Username']")));
        WebElement passwordField = driver.findElement(By.cssSelector("input[placeholder='Password']"));
        WebElement loginButton = driver.findElement(By.tagName("button"));

        usernameField.clear();
        usernameField.sendKeys("test");
        passwordField.clear();
        passwordField.sendKeys("test123");
        loginButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(
            driver.findElement(By.tagName("body")), "Todo List"));
        
        Assertions.assertTrue(driver.getPageSource().contains("Todo List"), 
            "User should be on Todo List page after successful login");
    }

    @Test
    @Order(3)
    @DisplayName("Create a new todo item and verify it is displayed")
    public void createNewItem() {
        WebElement newItemField = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("input[placeholder='New item']")));
        WebElement addButton = driver.findElement(By.xpath("//button[text()='Add']"));

        newItemField.sendKeys("Test Item");
        addButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(
            driver.findElement(By.tagName("body")), "Test Item"));
        
        Assertions.assertTrue(driver.getPageSource().contains("Test Item"), 
            "Newly created item should appear in the list");
    }

    @Test
    @Order(4)
    @DisplayName("Edit an existing todo item and verify updated text")
    public void editItem() {
        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//li[contains(text(), 'Test Item')]/button[text()='Edit']")));
        editButton.click();

        // Handle the prompt dialog
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().sendKeys(" Updated");
        driver.switchTo().alert().accept();

        wait.until(ExpectedConditions.textToBePresentInElement(
            driver.findElement(By.tagName("body")), "Test Item Updated"));
        
        Assertions.assertTrue(driver.getPageSource().contains("Test Item Updated"), 
            "Item should be updated with new text");
    }

    @Test
    @Order(5)
    @DisplayName("Delete the todo item and verify it is removed")
    public void deleteItem() {
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//li[contains(text(), 'Test Item Updated')]/button[text()='Delete']")));
        deleteButton.click();

        wait.until(ExpectedConditions.not(
            ExpectedConditions.textToBePresentInElement(
                driver.findElement(By.tagName("body")), "Test Item Updated")));
        
        Assertions.assertFalse(driver.getPageSource().contains("Test Item Updated"), 
            "Deleted item should no longer be present");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}