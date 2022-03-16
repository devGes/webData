package com.jared;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

import static com.jared.SiteScraper.scrapeSite;
import static com.jared.Utilities.*;


public class Program {

    public static void main(String[] args) {
        extractGroupedPages();
    }

    public static void extractGroupedPages() {
        // Loads JSON input values
        JSONObject searchSettings = objectFromJson(".\\src\\com\\jared\\AmazonSiteInput.json");
        JSONArray inputsJson = arrayFromJson(".\\src\\com\\jared\\" + searchSettings.get("inputType"));

        // Collects data from site (using inputsJson)
        List<Item> scrapedData    = scrapeSite(inputsJson, searchSettings);

        // Converts data to JSONArray
        JSONArray scrapedDataJson = itemListToJson(
                scrapedData,
                searchSettings,
                getRequiredKeys(inputsJson));

        // writes JSONArray of scraped data to file
        Utilities.writeToFile(
                scrapedDataJson,
                searchSettings.get("fileName").toString(),
                searchSettings.get("folderName").toString());

    }






}
