package com.jared;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.jared.SiteScraper.scrapeSite;
import static com.jared.Utilities.*;


public class Program {

    public static void main(String[] args) {
        extractGroupedPages();
//        extractIndividualPages();
    }

    public static void extractGroupedPages() {
        // Loads JSON input values
        JSONObject inputsJson = objectFromJson(".\\src\\com\\jared\\AmazonInput.json");

        // Collects data from site (using inputsJson)
        List<Item> scrapedData    = scrapeSite(inputsJson);

        // Converts data to JSONArray
        JSONArray scrapedDataJson = itemListToJson(scrapedData, inputsJson);

        // writes JSONArray of scraped data to file
        Utilities.writeToFile(
                scrapedDataJson,
                inputsJson.get("fileName").toString(),
                inputsJson.get("folderName").toString());

    }

    public static void extractIndividualPages() {
        JSONArray inputsJson = arrayFromJson(".\\src\\com\\jared\\AmazonProductURLs.json");
        ArrayList<String> urlInputs = new ArrayList<>();

        for (Object X : inputsJson) {
            urlInputs.add(extractAmzIdFromUrl(X.toString()));
            System.out.println(extractAmzIdFromUrl(X.toString()));
        }




    }

}
