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
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
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
            Thread.sleep(2000);
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            // No alert, continue
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
        
        // Debug: Print current page content
        System.out.println("=== AFTER CREATE ITEM ===");
        System.out.println(driver.getPageSource());
    }

    @Test
    @Order(4)
    @DisplayName("Edit an existing todo item and verify updated text")
    public void editItem() {
        // Debug: Print page state before edit
        System.out.println("=== BEFORE EDIT ===");
        System.out.println(driver.getPageSource());
        
        // Find the edit button for "Test Item"
        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//li[contains(text(), 'Test Item')]/button[text()='Edit']")));
        
        System.out.println("Found edit button, clicking...");
        editButton.click();

        // Handle the JavaScript prompt
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("Alert detected, entering text...");
            
            // Clear existing text and enter new text
            driver.switchTo().alert().sendKeys("Test Item Updated");
            driver.switchTo().alert().accept();
            
            System.out.println("Alert accepted");
        } catch (Exception e) {
            System.out.println("No alert found or alert handling failed: " + e.getMessage());
            // Fallback: try to handle without alert
        }

        // Wait for the page to update and check for the updated text
        try {
            wait.until(ExpectedConditions.textToBePresentInElement(
                driver.findElement(By.tagName("body")), "Test Item Updated"));
            System.out.println("Updated text found in page");
        } catch (Exception e) {
            System.out.println("Updated text not found, current page content:");
            System.out.println(driver.getPageSource());
            throw e;
        }
        
        Assertions.assertTrue(driver.getPageSource().contains("Test Item Updated"), 
            "Item should be updated with new text");
    }

    @Test
    @Order(5)
    @DisplayName("Delete the todo item and verify it is removed")
    public void deleteItem() {
        // Debug: Print page state before delete
        System.out.println("=== BEFORE DELETE ===");
        System.out.println(driver.getPageSource());
        
        // Look for either "Test Item Updated" or just "Test Item" in case edit didn't work
        WebElement deleteButton = null;
        try {
            deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(text(), 'Test Item Updated')]/button[text()='Delete']")));
        } catch (Exception e) {
            System.out.println("Couldn't find 'Test Item Updated', looking for 'Test Item'...");
            try {
                deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[contains(text(), 'Test Item')]/button[text()='Delete']")));
            } catch (Exception e2) {
                System.out.println("Couldn't find either item to delete. Current page:");
                System.out.println(driver.getPageSource());
                throw e2;
            }
        }
        
        deleteButton.click();

        // Wait for the item to be removed
        wait.until(ExpectedConditions.not(
            ExpectedConditions.textToBePresentInElement(
                driver.findElement(By.tagName("body")), "Test Item")));
        
        Assertions.assertFalse(driver.getPageSource().contains("Test Item"), 
            "Deleted item should no longer be present");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            System.out.println("=== FINAL PAGE STATE ===");
            System.out.println(driver.getPageSource());
            driver.quit();
        }
    }
}