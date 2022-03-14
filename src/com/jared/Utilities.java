package com.jared;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

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

    /** Uses Regex to extract the Amazon product ID from a URL
     *
     * @param url
     * @return
     */
    public static String extractAmzIdFromUrl(String url) {
        String searchPattern = "(?<=/)([a-zA-Z0-9]{10,13})(?=[/\"?]|$)";
        Pattern pattern = Pattern.compile(searchPattern);
        Matcher matcher = pattern.matcher(url);
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
    public static boolean isMatchAllRequiredKeys(JSONArray requiredKeys, Item data_keys) {
//        System.out.println("requiredKeys: " + requiredKeys.toString());
//        System.out.println("keySet: " + data_keys.getSet().toString());

        for (Object requiredKey : requiredKeys) {
//            System.out.println("here: " + requiredKey.toString());
            if (!data_keys.hasKey(requiredKey.toString())) {
//                System.out.println("dnu");
                return false;
            }
        }
        return true;

    }

    /** Converts List of Item's to JSONArray
     *
     * @param scrapedData
     * @param inputsJson
     * @return
     */
    public static JSONArray itemListToJson (List<Item> scrapedData, JSONObject inputsJson) {
        // Combines all scraped data into JSONArray (jsonArray or .Blank/Partial)
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArrayBlank = new JSONArray();
        JSONArray jsonArrayPartial = new JSONArray();
        JSONArray requiredKeys = (JSONArray) inputsJson.get("requiredKeys");

        // filters each item on keys (# and required)
        for (Item item : scrapedData) {
            if (item.itemSize() == 0) {
                jsonArrayBlank.add( item.HashMaptoJSON());
            } else if(isMatchAllRequiredKeys( requiredKeys, item )) {
                jsonArray.add( item.HashMaptoJSON());
            } else {
                jsonArrayPartial.add( item.HashMaptoJSON());
            }
        }

        System.out.printf("%d empty items.%n",       jsonArrayBlank.size());
        System.out.printf("%d partial items.%n",     jsonArrayPartial.size());
        System.out.printf("%d filled items. (%s)%n", jsonArray.size(), requiredKeys);

//         Main file (jsonArray) is written elsewhere, this is for debugging.
        if (!jsonArrayPartial.isEmpty()) {
            Utilities.writeToFile(
                    jsonArrayPartial,
                    "partial" + inputsJson.get("fileName").toString(),
                    inputsJson.get("folderName").toString());
        }
        return jsonArray;
    }

}
