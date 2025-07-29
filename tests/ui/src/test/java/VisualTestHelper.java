import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VisualTestHelper {
    
    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final String BASELINE_DIR = "src/test/resources/baselines";
    
    static {
        // Create directories if they don't exist
        new File(SCREENSHOT_DIR).mkdirs();
        new File(BASELINE_DIR).mkdirs();
    }
    
    public static void takeScreenshot(WebDriver driver, String testName) {
        try {
            // Full page screenshot
            Screenshot screenshot = new AShot()
                .shootingStrategy(ShootingStrategies.viewportPasting(1000))
                .takeScreenshot(driver);
            
            String fileName = String.format("%s/%s_%s.png", 
                SCREENSHOT_DIR, testName, System.currentTimeMillis());
            
            ImageIO.write(screenshot.getImage(), "PNG", new File(fileName));
            System.out.println("Screenshot saved: " + fileName);
            
        } catch (IOException e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }
    
    public static void takeElementScreenshot(WebDriver driver, String testName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String fileName = String.format("%s/%s_element_%s.png", 
                SCREENSHOT_DIR, testName, System.currentTimeMillis());
            
            FileUtils.copyFile(screenshot, new File(fileName));
            System.out.println("Element screenshot saved: " + fileName);
            
        } catch (IOException e) {
            System.err.println("Failed to take element screenshot: " + e.getMessage());
        }
    }
    
    public static boolean compareScreenshots(String baselinePath, String actualPath) {
        try {
            BufferedImage baseline = ImageIO.read(new File(baselinePath));
            BufferedImage actual = ImageIO.read(new File(actualPath));
            
            if (baseline.getWidth() != actual.getWidth() || 
                baseline.getHeight() != actual.getHeight()) {
                return false;
            }
            
            // Simple pixel comparison (can be enhanced with tolerance)
            for (int x = 0; x < baseline.getWidth(); x++) {
                for (int y = 0; y < baseline.getHeight(); y++) {
                    if (baseline.getRGB(x, y) != actual.getRGB(x, y)) {
                        return false;
                    }
                }
            }
            return true;
            
        } catch (IOException e) {
            System.err.println("Error comparing screenshots: " + e.getMessage());
            return false;
        }
    }
}