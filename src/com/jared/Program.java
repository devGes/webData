package com.jared;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

import static com.jared.Utilities.readFromJson;


public class Program {

    public static void main(String[] args) {
        // Loads JSON input values
        JSONObject inputsJson = readFromJson(".\\src\\com\\jared\\AmazonInput.json");

        // Collects data from site (using inputsJson)
        SiteScraper siteScraper = new SiteScraper();
        List<Item> scrapedData = siteScraper.scrapeSite(inputsJson);

        // Combines all scraped data into 1 JSONArray
        JSONArray jsonArray = new JSONArray();
        for (Item item : scrapedData) {
            jsonArray.add( item.HashMaptoJSON());
        }

        // writes JSONArray of scraped data to file
        Utilities.writeToFile(
                jsonArray.toJSONString(),
                inputsJson.get("fileName").toString(),
                inputsJson.get("folderName").toString());

    }

}
