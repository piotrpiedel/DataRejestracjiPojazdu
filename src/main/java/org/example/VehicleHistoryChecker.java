package org.example;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class VehicleHistoryChecker {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String URL = "https://historiapojazdu.gov.pl/";

    public static void main(String[] args) throws Exception {
        String vin = "JM1BL1V87C1635221";
        String plate = "KR032XV";
        int yearOfProduction = 2011;

        check(yearOfProduction, plate, vin);
    }

    private static void check(int yearOfProduction, String plate, String vin) throws Exception {
        List<String> dates = generateDates(yearOfProduction);

        for (String dateOfFirstRegistration : dates) {
            System.out.println("Checking database for " + dateOfFirstRegistration + "...");


            String raport = sendPost(plate, vin, dateOfFirstRegistration);

            if (raport.contains("raport-main-information")) {
                System.out.println("Valid first registration date found: " + dateOfFirstRegistration);
                return;
            }
        }
    }

    private static int daysInMonth(int month, int year) {
        return java.time.YearMonth.of(year, month).lengthOfMonth();
    }

    private static List<String> generateDates(int yearOfProduction) {
        List<String> dates = new ArrayList<>();
        int currentYear = java.time.Year.now().getValue();

        for (int year = yearOfProduction; year <= currentYear; year++) {
            for (int month = 1; month <= 12; month++) {
                for (int day = 1; day <= daysInMonth(month, year); day++) {
                    dates.add(zeroPad(day) + "." + zeroPad(month) + "." + year);
                }
            }
        }

        return dates;
    }

    private static String zeroPad(int number) {
        return String.format("%02d", number);
    }


    private static String sendPost(String numerRejestracyjny, String numerVIN, String dataPierwszejRejestracji) throws Exception {
        // Set up a cookie manager to handle cookies across requests
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        // First GET request to retrieve hidden fields
        HttpsURLConnection connection = (HttpsURLConnection) new URL(VehicleHistoryChecker.URL).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        String pageContent = content.toString();
        String postURL = extractBetween(pageContent,
                "<form id=\"_historiapojazduportlet_WAR_historiapojazduportlet_:formularz\" name=\"_historiapojazduportlet_WAR_historiapojazduportlet_:formularz\" method=\"post\" action=\"");
        String encodedURL = extractBetween(pageContent,
                "<input type=\"hidden\" name=\"javax.faces.encodedURL\" value=\"");
        String viewState = extractBetween(pageContent,
                "<input type=\"hidden\" name=\"javax.faces.ViewState\" id=\"javax.faces.ViewState\" value=\"");

        String urlParameters = "_historiapojazduportlet_WAR_historiapojazduportlet_:formularz="
                + URLEncoder.encode("_historiapojazduportlet_WAR_historiapojazduportlet_:formularz", StandardCharsets.UTF_8)
                + "&javax.faces.encodedURL=" + URLEncoder.encode(encodedURL, StandardCharsets.UTF_8)
                + "&_historiapojazduportlet_WAR_historiapojazduportlet_:rej=" + URLEncoder.encode(numerRejestracyjny, StandardCharsets.UTF_8)
                + "&_historiapojazduportlet_WAR_historiapojazduportlet_:vin=" + URLEncoder.encode(numerVIN, StandardCharsets.UTF_8)
                + "&_historiapojazduportlet_WAR_historiapojazduportlet_:data=" + URLEncoder.encode(dataPierwszejRejestracji, StandardCharsets.UTF_8)
                + "&_historiapojazduportlet_WAR_historiapojazduportlet_:btnSprawdz=" + URLEncoder.encode("Sprawdź pojazd »", StandardCharsets.UTF_8)
                + "&javax.faces.ViewState=" + URLEncoder.encode(viewState, StandardCharsets.UTF_8);

        // Second POST request to submit form
        connection = (HttpsURLConnection) new URL(postURL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        // Return the response content
        return content.toString();
    }

    private static String extractBetween(String source, String start) {
        int startIndex = source.indexOf(start) + start.length();
        int endIndex = source.indexOf("\"", startIndex);
        return source.substring(startIndex, endIndex);
    }
}