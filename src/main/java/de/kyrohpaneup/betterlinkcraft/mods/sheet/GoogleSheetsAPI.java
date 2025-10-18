package de.kyrohpaneup.betterlinkcraft.mods.sheet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GoogleSheetsAPI {

    private static final String SCRIPT_URL = "https://script.google.com/macros/s/AKfycbyWHlTimjuxv6CDVoldyE4C9xOty4iCclw2pulwiOW2z_7OX4iZKT_riBi2XMX6XLdkCw/exec";
    private static final Gson gson = new Gson();

    public static class SheetInfo {
        public String name;
        public int id;

        @Override
        public String toString() {
            return name + " (ID: " + id + ")";
        }
    }

    public static class SheetData {
        public SheetInfo sheet;
        public String[][] data;
    }

    public static class APIResponse {
        public boolean success;
        public String error;
        public List<SheetInfo> sheets;
        public SheetInfo sheet;
        public String[][] data;
    }

    // Get all sheets from Google Spreadsheet
    public static List<SheetInfo> getAllSheets() {
        try {
            String url = SCRIPT_URL + "?action=getSheets";
            String response = sendGetRequest(url);

            APIResponse apiResponse = gson.fromJson(response, APIResponse.class);

            if (apiResponse.success && apiResponse.sheets != null) {
                return apiResponse.sheets;
            } else {
                System.err.println("Error getting sheets: " + (apiResponse.error != null ? apiResponse.error : "Unknown error"));
            }
        } catch (Exception e) {
            System.err.println("Exception in getAllSheets: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // Get data from specific sheet
    public static SheetData getSheetData(String sheetIdentifier) {
        String response = null;
        try {
            String url = SCRIPT_URL + "?action=getData&sheet=" + java.net.URLEncoder.encode(sheetIdentifier, "UTF-8");
            response = sendGetRequest(url);

            if (response.isEmpty()) {
                System.err.println("Empty response from server");
                return null;
            }

            System.out.println("Response received, length: " + response.length());

            Gson gson = new Gson();

            try {
                APIResponse apiResponse = gson.fromJson(response, APIResponse.class);

                if (apiResponse.success && apiResponse.data != null) {
                    // Unescape special characters in the data
                    String[][] unescapedData = unescapeData(apiResponse.data);

                    SheetData sheetData = new SheetData();
                    sheetData.sheet = apiResponse.sheet;
                    sheetData.data = unescapedData;
                    return sheetData;
                } else {
                    System.err.println("API returned error: " + (apiResponse.error != null ? apiResponse.error : "Unknown error"));
                }
            } catch (Exception e) {
                System.err.println("Standard parsing failed, trying manual parsing: " + e.getMessage());
                return parseSheetDataManually(response);
            }

        } catch (Exception e) {
            System.err.println("Exception in getSheetData: " + e.getMessage());
            e.printStackTrace();

            if (response != null) {
                return parseSheetDataManually(response);
            }
        }
        return null;
    }

    private static String[][] unescapeData(String[][] data) {
        if (data == null) return new String[0][0];

        String[][] result = new String[data.length][];
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                result[i] = new String[data[i].length];
                for (int j = 0; j < data[i].length; j++) {
                    String cell = data[i][j];
                    if (cell != null) {
                        // Unescape special characters
                        result[i][j] = cell.replace("\\n", "\n")
                                .replace("\\r", "\r")
                                .replace("\\t", "\t")
                                .replace("\\\\", "\\");
                    } else {
                        result[i][j] = "";
                    }
                }
            } else {
                result[i] = new String[0];
            }
        }
        return result;
    }

    // HTTP GET request
    private static String sendGetRequest(String urlString) {
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            // Follow redirects
            connection.setInstanceFollowRedirects(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                System.err.println("HTTP error: " + responseCode + " for URL: " + urlString);

                // Try to read error stream
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                System.err.println("Error response: " + errorResponse);
            }
        } catch (Exception e) {
            System.err.println("Exception in sendGetRequest: " + e.getMessage());
            e.printStackTrace();
        }

        return response.toString();
    }

    private static SheetData parseSheetDataManually(String jsonResponse) {
        try {
            SheetData sheetData = new SheetData();

            if (!jsonResponse.contains("\"success\":true")) {
                System.err.println("Response does not indicate success");
                return null;
            }

            int dataStart = jsonResponse.indexOf("\"data\":[");
            if (dataStart == -1) {
                System.err.println("No data array found in response");
                return null;
            }

            dataStart += 7;
            int dataEnd = findMatchingBracket(jsonResponse, dataStart);

            if (dataEnd == -1) {
                System.err.println("Could not find end of data array");
                return null;
            }

            String dataArrayJson = jsonResponse.substring(dataStart, dataEnd + 1);

            Gson gson = new Gson();
            String[][] data = gson.fromJson(dataArrayJson, String[][].class);

            if (data != null) {
                // Unescape the data
                sheetData.data = unescapeData(data);

                try {
                    int sheetStart = jsonResponse.indexOf("\"sheet\":{");
                    if (sheetStart != -1) {
                        sheetStart += 8;
                        int sheetEnd = findMatchingBracket(jsonResponse, sheetStart);
                        if (sheetEnd != -1) {
                            String sheetJson = jsonResponse.substring(sheetStart, sheetEnd + 1);
                            sheetData.sheet = gson.fromJson(sheetJson, SheetInfo.class);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Could not parse sheet info: " + e.getMessage());
                }

                return sheetData;
            }

        } catch (Exception e) {
            System.err.println("Manual parsing failed: " + e.getMessage());
        }
        return null;
    }

    private static int findMatchingBracket(String str, int start) {
        int count = 1;
        for (int i = start + 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '[' || c == '{') {
                count++;
            } else if (c == ']' || c == '}') {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
}