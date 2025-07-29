import org.junit.jupiter.a.*;
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
    @DisplayName("Login page visual validation and invalid credentials test")
    public void loginWithInvalidCredentials() {
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("input[placeholder='Username']")));
        
        // Take screenshot for visual validation
        VisualTestHelper.takeScreenshot(driver, "login_page_initial");
        
        WebElement usernameField = driver.findElement(By.cssSelector("input[placeholder='Username']"));
        WebElement passwordField = driver.findElement(By.cssSelector("input[placeholder='Password']"));
        WebElement loginButton = driver.findElement(By.tagName("button"));

        usernameField.clear();
        usernameField.sendKeys("wrong");
        passwordField.clear();
        passwordField.sendKeys("wrong");
        
        VisualTestHelper.takeScreenshot(driver, "login_page_filled_invalid");
        loginButton.click();

        // Wait for potential alert and dismiss it
        try {
            Thread.sleep(2000);
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            // No alert, continue
        }

        VisualTestHelper.takeScreenshot(driver, "login_page_after_invalid_attempt");
        Assertions.assertTrue(driver.getPageSource().contains("Login"), 
            "User should remain on login page after invalid login");
    }

    @Test
    @Order(2)
    @DisplayName("Login with valid credentials and capture todo page")
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
        
        VisualTestHelper.takeScreenshot(driver, "login_page_filled_valid");
        loginButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(
            driver.findElement(By.tagName("body")), "Todo List"));
        
        // Take screenshot of todo list page
        VisualTestHelper.takeScreenshot(driver, "todo_list_page_empty");
        
        Assertions.assertTrue(driver.getPageSource().contains("Todo List"), 
            "User should be on Todo List page after successful login");
    }

    @Test
    @Order(3)
    @DisplayName("Create item and capture state changes")
    public void createNewItem() {
        // Before creating item
        VisualTestHelper.takeScreenshot(driver, "before_create_item");
        
        WebElement newItemField = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("input[placeholder='New item']")));
        WebElement addButton = driver.findElement(By.xpath("//button[text()='Add']"));

        newItemField.sendKeys("Test Item");
        VisualTestHelper.takeScreenshot(driver, "create_item_field_filled");
        
        addButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(
            driver.findElement(By.tagName("body")), "Test Item"));
        
        // After creating item
        VisualTestHelper.takeScreenshot(driver, "after_create_item");
        
        Assertions.assertTrue(driver.getPageSource().contains("Test Item"), 
            "Newly created item should appear in the list");
    }

    @Test
    @Order(4)
    @DisplayName("Edit item with visual state tracking")
    public void editItem() {
        // Before edit
        VisualTestHelper.takeScreenshot(driver, "before_edit_item");
        
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
        
        // After edit
        VisualTestHelper.takeScreenshot(driver, "after_edit_item");
        
        Assertions.assertTrue(driver.getPageSource().contains("Test Item Updated"), 
            "Item should be updated with new text");
    }

    @Test
    @Order(5)
    @DisplayName("Delete item and verify empty state")
    public void deleteItem() {
        // Before delete
        VisualTestHelper.takeScreenshot(driver, "before_delete_item");
        
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
        
        // After delete - empty state
        VisualTestHelper.takeScreenshot(driver, "after_delete_empty_state");
        
        Assertions.assertFalse(driver.getPageSource().contains("Test Item"), 
            "Deleted item should no longer be present");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            // Final state screenshot
            VisualTestHelper.takeScreenshot(driver, "final_state");
            driver.quit();
        }
    }
}