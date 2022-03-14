package com.jared;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    /**
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
            System.out.println("Wrote to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param obj
     * @param fileName
     * @param folderName
     */
    public static void writeToFile(JSONArray obj, String fileName, String folderName) {
        writeToFile(obj.toJSONString(), fileName, folderName);
    }

    /**
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

    /**
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

    public static boolean isMatchAllRequiredKeys(JSONArray json, Item item) {
        System.out.println("json: " + json.toString());
        System.out.println("keySet: " + item.getSet().toString());

        for (Object req : json) {
            System.out.println("here: " + req.toString());
            if (!item.hasKey(req.toString())) {
                System.out.println("dnu");
                return false;
            }
        }
        return true;

    }

}
