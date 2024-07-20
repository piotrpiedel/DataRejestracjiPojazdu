package org.example;

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
        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\piopid\\Music\\DataRejestracjiPojazdu\\chromedriver\\win64-116.0.5793.0\\chromedriver-win64\\chromedriver.exe");

        // Set up Chrome options to use a specific Chrome binary
        ChromeOptions options = new ChromeOptions();
        options.setBinary("C:\\Users\\piopid\\Music\\DataRejestracjiPojazdu\\chrome\\win64-116.0.5793.0\\chrome-win64\\chrome.exe");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless"); // Remove if you want to see the browser UI
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.5793.0 Safari/537.36");

        // Initialize the WebDriver with options
        WebDriver driver = new ChromeDriver(options);

        try {
            // Navigate to the web page containing the form
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
            driver.quit();
        }
    }
}
