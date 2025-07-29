import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotHelper {
    
    private static final String SCREENSHOT_DIR = "target/screenshots";
    
    // Ensure directory exists
    static {
        File dir = new File(SCREENSHOT_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("Screenshots directory created: " + created + " at " + dir.getAbsolutePath());
        }
    }
    
    public static void takeScreenshot(WebDriver driver, String testName) {
        try {
            // Create timestamp for unique filenames
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String fileName = String.format("%s/%s_%s.png", SCREENSHOT_DIR, testName, timestamp);
            
            // Take screenshot
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(fileName);
            
            // Copy to destination
            FileUtils.copyFile(screenshotFile, destinationFile);
            
            System.out.println("✅ Screenshot saved: " + destinationFile.getAbsolutePath());
            System.out.println("   File size: " + destinationFile.length() + " bytes");
            
        } catch (IOException e) {
            System.err.println("❌ Failed to take screenshot for " + testName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void listAllScreenshots() {
        File screenshotDir = new File(SCREENSHOT_DIR);
        System.out.println("=== SCREENSHOT DIRECTORY LISTING ===");
        System.out.println("Directory: " + screenshotDir.getAbsolutePath());
        System.out.println("Exists: " + screenshotDir.exists());
        System.out.println("Is Directory: " + screenshotDir.isDirectory());
        
        if (screenshotDir.exists() && screenshotDir.isDirectory()) {
            File[] files = screenshotDir.listFiles();
            if (files != null && files.length > 0) {
                System.out.println("Files found: " + files.length);
                for (File file : files) {
                    System.out.println("  📸 " + file.getName() + " (" + file.length() + " bytes)");
                }
            } else {
                System.out.println("❌ No files found in directory");
            }
        } else {
            System.out.println("❌ Directory does not exist");
        }
        System.out.println("=== END LISTING ===");
    }
}