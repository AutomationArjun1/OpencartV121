package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager; //log4j
import org.apache.logging.log4j.Logger; //log4j
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

public class BaseClass {

    static public WebDriver driver;
    public Logger logger;
    public Properties p;
    public ResourceBundle rb;// to read config.properties

    @BeforeClass(groups = {"sanity","regression","master"})
    @Parameters({"os", "browser"})
    public void setup(String os, String br) throws IOException {

        // loading properties file
        FileReader file = new FileReader(".//src//test//resources//config.properties");
        p = new Properties();
        p.load(file);

        // loading log4j
        logger = LogManager.getLogger(this.getClass());// Log4j

        // launching browser based on condition
        switch (br.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                if (System.getenv("CI") != null) { // running in GitHub Actions
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                }
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (System.getenv("CI") != null) {
                    firefoxOptions.addArguments("--headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                if (System.getenv("CI") != null) {
                    edgeOptions.addArguments("--headless=new");
                }
                driver = new EdgeDriver(edgeOptions);
                break;

            default:
                System.out.println("No matching browser..");
                return;
        }

        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

        driver.get(p.getProperty("appURL"));
        driver.manage().window().maximize();
    }

    @AfterClass(groups = {"sanity","regression","master"})
    public void tearDown() {
        driver.quit();
    }

    public String randomeString() {
        return RandomStringUtils.randomAlphabetic(5);
    }

    public String randomeNumber() {
        return RandomStringUtils.randomNumeric(10);
    }

    public String randomAlphaNumeric() {
        String str = RandomStringUtils.randomAlphabetic(3);
        String num = RandomStringUtils.randomNumeric(3);
        return (str + "@" + num);
    }

    public String captureScreen(String tname) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

        String targetFilePath = System.getProperty("user.dir") + "\\screenshots\\" + tname + "_" + timeStamp + ".png";
        File targetFile = new File(targetFilePath);

        sourceFile.renameTo(targetFile);

        return targetFilePath;
    }

}