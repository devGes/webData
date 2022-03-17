package com.jared;


import com.gargoylesoftware.htmlunit.WebClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    /**
     *
     * @return
     */
    public static WebClient getWebClient() {
        WebClient web = new WebClient();
        web.setJavaScriptEnabled(false);
        return web;
    }

    /** Basic FileWriter
     *
     * @param fileData
     * @param fileName
     * @param folderName
     */
    public static void writeToFile(String fileData, String fileName, String folderName) {
        try {
            Files.createDirectories(Paths.get(folderName));
            FileWriter myWriter = new FileWriter(folderName + "\\" + fileName);

            myWriter.write(fileData);
            myWriter.flush();
            myWriter.close();
            System.out.println("Wrote to file: " + Paths.get(folderName + "\\" + fileName).toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Writes JSONArray to file
     *
     * @param obj
     * @param fileName
     * @param folderName
     */
    public static void writeToFile(JSONArray obj, String fileName, String folderName) {
        writeToFile(obj.toJSONString(), fileName, folderName);
    }

    /** Loads JSON file and returns an Object
     *
     * @param fileName
     * @return
     */
    public static JSONObject objectFromJson(String fileName){
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            JSONObject inputList = (JSONObject) jsonParser.parse(reader);
//            System.out.println("Reading: \n" + inputList);
            return inputList;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    /** Loads JSON file and returns an Array
     *
     * @param fileName
     * @return
     */
    public static JSONArray arrayFromJson(String fileName){
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            JSONArray inputList = (JSONArray) jsonParser.parse(reader);
//            System.out.println("Reading: \n" + inputList);
            return inputList;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    /** Uses Regex to extract the Amazon product ID from a URL (has issues)
     *
     * @param url
     * @return
     */
    public static String extractAmzIdFromUrl(String url) {
        String searchPattern = "(?<=/)([a-zA-Z0-9]{10,13})(?=[^]|$)";
        Pattern pattern = Pattern.compile(searchPattern);
        Matcher matcher = pattern.matcher(url);
        boolean matchFound = matcher.find();
        if(matchFound) {
            return matcher.group();
        } else {
            return "";
        }
    }

    /** Uses Regex to extract the Amazon product ID from an HTML String
     *
     * @param htmlString
     * @return
     */
    public static String extractAmzIdFromHTML(String htmlString) {
        String searchPattern = "(?<=asin=)([a-zA-Z0-9]{10,13})(?=[^a-zA-Z0-9])";
        Pattern pattern = Pattern.compile(searchPattern);
        Matcher matcher = pattern.matcher(htmlString);
        boolean matchFound = matcher.find();
        if(matchFound) {
            return matcher.group();
        } else {
            return "";
        }
    }

    /** Checks that an item has all requiredKeys
     *
     * @param requiredKeys
     * @param data_keys
     * @return
     */
    public static boolean isMatchAllRequiredKeys(List<String> requiredKeys, Item data_keys) {
//        System.out.println("requiredKeys: " + requiredKeys.toString());
//        System.out.println("keySet: " + data_keys.getSet().toString());

        for (String requiredKey : requiredKeys) {
//            System.out.println("here: " + requiredKey.toString());
            if (!data_keys.hasKey(requiredKey)) {
//                System.out.println("dnu");
                return false;
            }
        }
        return true;

    }

    /** Creates JSON from list of Item's (also filters based on requiredKey values
     *
     * @param scrapedData
     * @param fileSettings
     * @param requiredKeys
     * @return
     */
    public static JSONArray itemListToJson (List<Item> scrapedData, JSONObject fileSettings, List<String> requiredKeys) {
        // Combines all scraped data into JSONArray (jsonArray or .Blank/Partial)
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArrayBlank = new JSONArray();
        JSONArray jsonArrayPartial = new JSONArray();

        // filters each item on keys (# and required)
        for (Item item : scrapedData) {
//            System.out.println(item.getSet());
            if (item.size() == 0) {
                jsonArrayBlank.add( item.HashMaptoJSON());
            } else if(isMatchAllRequiredKeys( requiredKeys, item )) {
                jsonArray.add( item.HashMaptoJSON());
            } else {
                jsonArrayPartial.add( item.HashMaptoJSON());
            }
        }

        System.out.printf("%d empty items.%n",       jsonArrayBlank.size());
        System.out.printf("%d partial items.%n",     jsonArrayPartial.size());
        System.out.printf("%d filled items. %s%n", jsonArray.size(), requiredKeys);

//         Main file (jsonArray) is written elsewhere (Program.java), this is for debugging.
        if (!jsonArrayPartial.isEmpty()) {
            Utilities.writeToFile(
                    jsonArrayPartial,
                    "partial" + fileSettings.get("fileName").toString(),
                    fileSettings.get("folderName").toString());
        }
        return jsonArray;
    }

    /** Extracts list of requiredKeys from inputsJson
     *
     * @param inputsJson
     * @return
     */
    public static ArrayList<String> getRequiredKeys(JSONArray inputsJson) {
        ArrayList<String> requiredKeys = new ArrayList<>();
        for (Object obj : inputsJson) {
            JSONObject jobj = (JSONObject) obj;
            if ((boolean) jobj.get("required")) {
                requiredKeys.add(jobj.get("name").toString());
            }
        }
        return requiredKeys;
    }

    /**
     *
     * @return
     */
    public static ArrayList<String> extractAmzIdFromUrlJson(String fileName) {
        JSONArray inputsJson = arrayFromJson(fileName);
        ArrayList<String> urlInputs = new ArrayList<>();

        for (Object X : inputsJson) {
            urlInputs.add(extractAmzIdFromUrl(X.toString()));
            System.out.println(extractAmzIdFromUrl(X.toString()));
        }
        return urlInputs;
    }

    /**
     *
     * @return
     */
    public static ArrayList<String> extractAmzIdFromUrlJson () {
        return extractAmzIdFromUrlJson(".\\src\\com\\jared\\AmazonProductURLs.json");
    }

    /**
     *
     * @param searchInput
     * @return
     */
    public static String stringToAmzSearch (String searchInput) {
        return "https://www.amazon.com/s?k=" + searchInput.replace(" ", "+").replace("'", "%27").replace("/","%2F");
    }



    }
