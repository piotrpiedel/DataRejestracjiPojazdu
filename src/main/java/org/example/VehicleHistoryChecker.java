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
            // Set path to the ChromeDriver executable
            System.setProperty("webdriver.chrome.driver", "C:\\Users\\piopid\\Music\\DataRejestracjiPojazdu\\chromedriver\\win64-116.0.5793.0\\chromedriver-win64\\chromedriver.exe");

            // ChromeOptions to specify the path to the Chrome binary
            ChromeOptions options = new ChromeOptions();
            String chromeBinaryPath = "C:\\Users\\piopid\\Music\\DataRejestracjiPojazdu\\chrome\\win64-116.0.5793.0\\chrome-win64\\chrome.exe";
            options.setBinary(chromeBinaryPath);
            // Initialize ChromeDriver with options
            driver = new ChromeDriver(options);

            // Open the URL
            driver.get("https://www.google.com");
            System.out.println("Page title is: " + driver.getTitle());

            // Additional code to interact with the page...
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

    }
}
