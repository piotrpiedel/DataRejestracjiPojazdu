package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class VehicleHistoryChecker {

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

//            for (String date : dates) {
            System.out.println("Checking database for "  + "01.01.2013");
            String formData = getFormData(form, plate, vin, "01.01.2013");

            Document response = Jsoup.connect(uri)
                    .userAgent("Mozilla")
                    .requestBody(formData)
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .post();

            System.out.println(response);

            if (response.html().contains("raport-main-information")) {
                System.out.println("Valid first registration date found: " + "01.01.2013");
            } else if (response.html().contains("<partial-response>")) {
                // Parse partial response
                String viewState = parsePartialResponse(response.html());
                if (viewState != null) {
                    System.out.println("ViewState received: " + viewState);
                    // Handle the ViewState as needed
                    formData = getFormDataWithViewState(form, plate, vin, "01.01.2013", viewState);
                    Document subsequentResponse = Jsoup.connect(uri)
                            .userAgent("Mozilla")
                            .requestBody(formData)
                            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            .post();

                    if (subsequentResponse.html().contains("raport-main-information")) {
                        System.out.println("Valid first registration date found: " + "01.01.2013");
                        return;
                    } else {
                        System.out.println("Failed to find registration date with subsequent request.");
                    }
                } else {
                    System.out.println("ViewState not found in the partial response.");
                }
            }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String parsePartialResponse(String partialResponse) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(partialResponse)));

            // Find the 'update' element with the id 'javax.faces.ViewState'
            Node viewStateNode = null;
            Node root = doc.getDocumentElement();
            for (int i = 0; i < root.getChildNodes().getLength(); i++) {
                Node node = root.getChildNodes().item(i);
                if (node.getNodeName().equals("changes")) {
                    for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                        Node changeNode = node.getChildNodes().item(j);
                        if (changeNode.getNodeName().equals("update") && "javax.faces.ViewState".equals(changeNode.getAttributes().getNamedItem("id").getNodeValue())) {
                            viewStateNode = changeNode;
                            break;
                        }
                    }
                }
            }

            if (viewStateNode != null) {
                // Extract CDATA content
                String cdata = viewStateNode.getTextContent();
                return cdata != null ? cdata.trim() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getFormDataWithViewState(Element form, String plate, String vin, String date, String viewState) {
        // Build the form data string with the ViewState included
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
        formData.append("javax.faces.ViewState=").append(viewState).append("&");
        formData.append(fieldPrefix).append(":btnSprawdz=Sprawdź pojazd »");

        return formData.toString();
    }

    public static void main(String[] args) {
        String plate = "WR510EM";
        String vin = "KMHD35LE6DU138018";
        int yearOfProduction = 2010;

        check(plate, vin, yearOfProduction);
    }
}
