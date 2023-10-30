package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class  VehicleHistoryChecker {

    private static final String fieldPrefix = "_historiapojazduportlet_WAR_historiapojazduportlet_";

    private static int daysInMonth(int month, int year) {
        return java.time.YearMonth.of(year, month).lengthOfMonth();
    }

    private static String getFormUrl(Element form) {
        return form.attr("action");
    }

    private static String zeroPad(int number) {
        return String.format("%02d", number);
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

    private static String getFormData(Element form, String plate, String vin, String date) {
        Elements inputElements = form.select("input");
        StringBuilder formData = new StringBuilder();

        for (Element input : inputElements) {
            String name = input.attr("name");
            String value = input.attr("value");
            formData.append(name).append("=").append(value).append("&");
        }

        formData.append(fieldPrefix).append(":rej=").append(plate).append("&");
        formData.append(fieldPrefix).append(":vin=").append(vin).append("&");
        formData.append(fieldPrefix).append(":data=").append(date).append("&");
        formData.append(fieldPrefix).append(":btnSprawdz=Sprawdź pojazd »");

        return formData.toString();
    }

    public static void check(String plate, String vin, int yearOfProduction) {
        try {
            Document document = Jsoup.connect("https://historiapojazdu.gov.pl/strona-glowna").get();
            Element form = document.getElementById(fieldPrefix + ":formularz");
            String uri = getFormUrl(form);
            List<String> dates = generateDates(yearOfProduction);

            for (String date : dates) {
                System.out.println("Checking database for " + date + "...");
                String formData = getFormData(form, plate, vin, date);

                Document response = Jsoup.connect(uri)
                        .requestBody(formData)
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .post();

                if (response.html().contains("raport-main-information")) {
                    System.out.println("Valid first registration date found: " + date);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String plate = "WZ9683A";
        String vin = "TMADB51CAAJ069475";
        int yearOfProduction = 2009;

        check(plate, vin, yearOfProduction);
    }
}
