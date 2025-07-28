import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UITests {

    static WebDriver driver;

    @BeforeAll
    public static void setup() {
        // Explicitly set chromedriver path
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        System.out.println("Using chromedriver from: " + System.getProperty("webdriver.chrome.driver"));

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);

        driver.get("http://localhost:3000");
    }

    @Test
    @Order(1)
    @DisplayName("Login with invalid credentials should stay on login page")
    public void loginWithInvalidCredentials() {
        driver.findElement(By.cssSelector("input[placeholder='Username']")).clear();
        driver.findElement(By.cssSelector("input[placeholder='Username']")).sendKeys("wrong");
        driver.findElement(By.cssSelector("input[placeholder='Password']")).clear();
        driver.findElement(By.cssSelector("input[placeholder='Password']")).sendKeys("wrong");
        driver.findElement(By.tagName("button")).click();
        Assertions.assertTrue(driver.getPageSource().contains("Login"), "User should remain on login page after invalid login");
    }

    @Test
    @Order(2)
    @DisplayName("Login with valid credentials should navigate to Todo List page")
    public void loginWithValidCredentials() {
        driver.navigate().refresh();
        driver.findElement(By.cssSelector("input[placeholder='Username']")).clear();
        driver.findElement(By.cssSelector("input[placeholder='Username']")).sendKeys("test");
        driver.findElement(By.cssSelector("input[placeholder='Password']")).clear();
        driver.findElement(By.cssSelector("input[placeholder='Password']")).sendKeys("test123");
        driver.findElement(By.tagName("button")).click();
        Assertions.assertTrue(driver.getPageSource().contains("Todo List"), "User should be on Todo List page after successful login");
    }

    @Test
    @Order(3)
    @DisplayName("Create a new todo item and verify it is displayed")
    public void createNewItem() {
        driver.findElement(By.cssSelector("input[placeholder='New item']")).sendKeys("Test Item");
        driver.findElement(By.xpath("//button[text()='Add']")).click();
        Assertions.assertTrue(driver.getPageSource().contains("Test Item"), "Newly created item should appear in the list");
    }

    @Test
    @Order(4)
    @DisplayName("Edit an existing todo item and verify updated text")
    public void editItem() {
        driver.findElement(By.xpath("//li[contains(text(), 'Test Item')]/button[text()='Edit']")).click();
        // Assuming an alert prompt for editing:
        driver.switchTo().alert().sendKeys(" Updated");
        driver.switchTo().alert().accept();
        Assertions.assertTrue(driver.getPageSource().contains("Test Item Updated"), "Item should be updated with new text");
    }

    @Test
    @Order(5)
    @DisplayName("Delete the todo item and verify it is removed")
    public void deleteItem() {
        driver.findElement(By.xpath("//li[contains(text(), 'Test Item Updated')]/button[text()='Delete']")).click();
        Assertions.assertFalse(driver.getPageSource().contains("Test Item Updated"), "Deleted item should no longer be present");
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}