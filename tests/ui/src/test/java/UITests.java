import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
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
        System.out.println("=== UI TEST SETUP ===");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        System.out.println("Navigating to: http://localhost:3000");
        driver.get("http://localhost:3000");
        
        System.out.println("=== SETUP COMPLETE ===");
    }

    @Test
    @Order(1)
    @DisplayName("Login page visual validation and invalid credentials test")
    public void loginWithInvalidCredentials() {
        System.out.println("=== TEST 1: Invalid Login ===");
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("input[placeholder='Username']")));
        
        WebElement usernameField = driver.findElement(By.cssSelector("input[placeholder='Username']"));
        WebElement passwordField = driver.findElement(By.cssSelector("input[placeholder='Password']"));
        WebElement loginButton = driver.findElement(By.tagName("button"));

        usernameField.clear();
        usernameField.sendKeys("wrong");
        passwordField.clear();
        passwordField.sendKeys("wrong");
        
        loginButton.click();

        // Wait for potential alert and dismiss it
        try {
            Thread.sleep(2000);
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            System.out.println("No alert found");
        }

        Assertions.assertTrue(driver.getPageSource().contains("Login"), 
            "User should remain on login page after invalid login");
        
        System.out.println("=== TEST 1 COMPLETE ===");
    }

    @Test
    @Order(2)
    @DisplayName("Login with valid credentials and capture todo page")
    public void loginWithValidCredentials() {
        System.out.println("=== TEST 2: Valid Login ===");
        
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
        
        System.out.println("=== TEST 2 COMPLETE ===");
    }

    @Test
    @Order(3)
    @DisplayName("Create item and capture state changes")
    public void createNewItem() {
        System.out.println("=== TEST 3: Create Item ===");
        
        WebElement newItemField = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("input[placeholder='New item']")));
        WebElement addButton = driver.findElement(By.xpath("//button[text()='Add']"));

        newItemField.sendKeys("Test Item");
        addButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(
            driver.findElement(By.tagName("body")), "Test Item"));
        
        Assertions.assertTrue(driver.getPageSource().contains("Test Item"), 
            "Newly created item should appear in the list");
        
        System.out.println("=== TEST 3 COMPLETE ===");
    }

    @Test
    @Order(4)
    @DisplayName("Edit item with visual state tracking")
    public void editItem() {
        System.out.println("=== TEST 4: Edit Item ===");
        
        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//li[contains(text(), 'Test Item')]/button[text()='Edit']")));
        editButton.click();

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().sendKeys("Test Item Updated");
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            System.out.println("Alert handling failed: " + e.getMessage());
        }

        wait.until(ExpectedConditions.textToBePresentInElement(
            driver.findElement(By.tagName("body")), "Test Item Updated"));
        
        Assertions.assertTrue(driver.getPageSource().contains("Test Item Updated"), 
            "Item should be updated with new text");
        
        System.out.println("=== TEST 4 COMPLETE ===");
    }

    @Test
    @Order(5)
    @DisplayName("Delete item and verify empty state")
    public void deleteItem() {
        System.out.println("=== TEST 5: Delete Item ===");
        
        WebElement deleteButton = null;
        try {
            deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(text(), 'Test Item Updated')]/button[text()='Delete']")));
        } catch (Exception e) {
            // Fallback to original item name
            deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(text(), 'Test Item')]/button[text()='Delete']")));
        }
        
        deleteButton.click();

        wait.until(ExpectedConditions.not(
            ExpectedConditions.textToBePresentInElement(
                driver.findElement(By.tagName("body")), "Test Item")));
        
        Assertions.assertFalse(driver.getPageSource().contains("Test Item"), 
            "Deleted item should no longer be present");
        
        System.out.println("=== TEST 5 COMPLETE ===");
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("=== TEARDOWN ===");
        
        if (driver != null) {
            driver.quit();
        }
        
        System.out.println("=== TEARDOWN COMPLETE ===");
    }
}