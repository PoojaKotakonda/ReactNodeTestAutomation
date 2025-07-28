
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UITests {
    static WebDriver driver;

    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver"); // Adjust path if needed
        driver = new ChromeDriver();
        driver.get("http://localhost:3000");
    }

    @Test @Order(1)
    public void loginWithInvalidCredentials() {
        driver.findElement(By.cssSelector("input[placeholder='Username']")).sendKeys("wrong");
        driver.findElement(By.cssSelector("input[placeholder='Password']")).sendKeys("wrong");
        driver.findElement(By.tagName("button")).click();
        Assertions.assertTrue(driver.getPageSource().contains("Login"));
    }

    @Test @Order(2)
    public void loginWithValidCredentials() {
        driver.navigate().refresh();
        driver.findElement(By.cssSelector("input[placeholder='Username']")).sendKeys("test");
        driver.findElement(By.cssSelector("input[placeholder='Password']")).sendKeys("test123");
        driver.findElement(By.tagName("button")).click();
        Assertions.assertTrue(driver.getPageSource().contains("Todo List"));
    }

    @Test @Order(3)
    public void createNewItem() {
        WebElement input = driver.findElement(By.cssSelector("input[placeholder='New item']"));
        input.sendKeys("Test Item");
        driver.findElement(By.xpath("//button[text()='Add']")).click();
        Assertions.assertTrue(driver.getPageSource().contains("Test Item"));
    }

    @Test @Order(4)
    public void editItem() {
        driver.findElement(By.xpath("//li[contains(text(), 'Test Item')]/button[text()='Edit']")).click();
        driver.switchTo().alert().sendKeys("Updated Item");
        driver.switchTo().alert().accept();
        Assertions.assertTrue(driver.getPageSource().contains("Updated Item"));
    }

    @Test @Order(5)
    public void deleteItem() {
        driver.findElement(By.xpath("//li[contains(text(), 'Updated Item')]/button[text()='Delete']")).click();
        Assertions.assertFalse(driver.getPageSource().contains("Updated Item"));
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
