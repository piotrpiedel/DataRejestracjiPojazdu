package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class VehicleHistoryChecker {

    private static final String fieldPrefix = "_historiapojazduportlet_WAR_historiapojazduportlet_";

    private static int daysInMonth(int month, int year) {
        return java.time.YearMonth.of(year, month).lengthOfMonth();
    }

//    private static String getFormUrl(Element form) {
//        return form.attr("action");
//    }
//
//    private static String zeroPad(int number) {
//        return String.format("%02d", number);
//    }

//    private static List<String> generateDates(int yearOfProduction) {
//        List<String> dates = new ArrayList<>();
//        int currentYear = java.time.Year.now().getValue();
//
//        for (int year = yearOfProduction; year <= currentYear; year++) {
//            for (int month = 1; month <= 12; month++) {
//                for (int day = 1; day <= daysInMonth(month, year); day++) {
//                    dates.add(zeroPad(day) + "." + zeroPad(month) + "." + year);
//                }
//            }
//        }
//
//        return dates;
//    }

    public static void main(String[] args) {
        WebDriver driver = null;

        try {
            // Setup ChromeDriver using WebDriverManager
            System.out.println("Setting up ChromeDriver...");
//            WebDriverManager.chromedriver().driverVersion("116.0.5793.0").browserVersion("116.0.5793.0").setup();
            System.setProperty("webdriver.chrome.driver", "C:\\Users\\piopid\\Music\\DataRejestracjiPojazdu\\chromedriver\\win64-116.0.5793.0\\chromedriver-win64\\chromedriver.exe");

            // ChromeOptions to specify the path to the Chrome binary
            ChromeOptions options = new ChromeOptions();
            String chromeBinaryPath = "C:\\Users\\piopid\\Music\\DataRejestracjiPojazdu\\chrome\\win64-116.0.5793.0\\chrome-win64\\chrome.exe";
            options.setBinary(chromeBinaryPath);
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--headless"); // Comment out if you want to see the browser UI

            // Initialize the WebDriver with options
            System.out.println("Initializing ChromeDriver...");
            driver = new ChromeDriver(options);
            // Open the URL
            System.out.println("Opening URL...");
            driver.get("https://historiapojazdu.gov.pl/");

            // Add a delay to mimic human behavior
            Thread.sleep(2000);

            // Define the vehicle registration and VIN
            String vehicleRegistration = "WR510EM";
            String vin = "KMHD35LE6DU138018";

            // Find the vehicle registration input field and fill it
            WebElement vehicleRegistrationField = driver.findElement(By.id("_historiapojazduportlet_WAR_historiapojazduportlet_:rej"));
            vehicleRegistrationField.clear(); // Clear any existing text
            vehicleRegistrationField.sendKeys(vehicleRegistration);

            // Add a delay to mimic human behavior
            Thread.sleep(2000);

            // Find the VIN input field and fill it
            WebElement vinField = driver.findElement(By.id("_historiapojazduportlet_WAR_historiapojazduportlet_:vin"));
            vinField.clear(); // Clear any existing text
            vinField.sendKeys(vin);

            // Add a delay to mimic human behavior
            Thread.sleep(2000);

            // Find the date input field and fill it
            WebElement dateField = driver.findElement(By.id("_historiapojazduportlet_WAR_historiapojazduportlet_:data"));
            dateField.clear(); // Clear any existing text
            dateField.sendKeys("01.01.2013");

            // Add a delay to mimic human behavior
            Thread.sleep(2000);

            // Find the submit button and click it
            WebElement submitButton = driver.findElement(By.id("btnSprawdz"));
            submitButton.click();

            // Wait for the response to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement responseElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("responseElementId"))); // Adjust the locator as needed

            // Get the response text
            String responseText = responseElement.getText();
            System.out.println("Response: " + responseText);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            if (driver != null)
                driver.quit();
        }
    }
}
