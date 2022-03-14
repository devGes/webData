package com.jared;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.jared.Utilities.*;


public class Program {

    public static void main(String[] args) {
        method1();
//        method2();
    }

    public static void method1() {
        // Loads JSON input values
        JSONObject inputsJson = objectFromJson(".\\src\\com\\jared\\AmazonInput.json");

        // Collects data from site (using inputsJson)
        SiteScraper siteScraper = new SiteScraper();
        List<Item> scrapedData = siteScraper.scrapeSite(inputsJson);

        // Combines all scraped data into 1 JSONArray
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArrayBlank = new JSONArray();
        JSONArray jsonArrayPartial = new JSONArray();
        for (Item item : scrapedData) {
            if (item.itemSize() == 0) {
                jsonArrayBlank.add( item.HashMaptoJSON());
//                System.out.println("empty");
            } else if(isMatchAllRequiredKeys( (JSONArray) inputsJson.get("requiredKeys"), item )) {
                jsonArray.add( item.HashMaptoJSON());
//                System.out.println("filled");
            } else {
                jsonArrayPartial.add( item.HashMaptoJSON());
//                System.out.println("partial");
            }

//            System.out.println(item.HashMaptoJSON().toString());
//            jsonArray.add( item.HashMaptoJSON() );
        }
        System.out.println("Should be JSON output: " + jsonArray.toString());

        System.out.println(jsonArrayBlank.size() + " empty items.");
        System.out.println(jsonArrayPartial.size() + " partial items.");
        System.out.println(jsonArray.size() + " filled items. (" + inputsJson.get("requiredKeys") + ")");
        // writes JSONArray of scraped data to file
        Utilities.writeToFile(
                jsonArray.toJSONString(),
                inputsJson.get("fileName").toString(),
                inputsJson.get("folderName").toString());
        Utilities.writeToFile(
                jsonArrayBlank.toJSONString(),
                "blank" + inputsJson.get("fileName").toString(),
                inputsJson.get("folderName").toString());
        Utilities.writeToFile(
                jsonArrayPartial.toJSONString(),
                "partial" + inputsJson.get("fileName").toString(),
                inputsJson.get("folderName").toString());

    }

    public static void method2() {
        JSONArray inputsJson = arrayFromJson(".\\src\\com\\jared\\AmazonProductURLs.json");
        ArrayList<String> urlInputs = new ArrayList<>();

        for (Object X : inputsJson) {
            urlInputs.add(extractAmzIdFromUrl(X.toString()));
//            System.out.println(extractAmzIdFromUrl(X.toString()));
        }




    }

}
