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


    public static void extractGroupedPages () {
        extractGroupedPages(
                ".\\src\\com\\jared\\AmazonSiteInput.json",
                "https://www.amazon.com/s?k=usb+cables&page=1");
    }

    public static void extractGroupedPages(String searchFileLocation, String websiteUrl) {
        // Loads JSON input values
        JSONObject searchSettings = objectFromJson(searchFileLocation);
        JSONArray itemInputs = arrayFromJson(".\\src\\com\\jared\\" + searchSettings.get("inputType"));

        // Collects data from site (using itemInputs)
        List<Item> scrapedData    = scrapeSite(itemInputs, searchSettings, websiteUrl);

        // Converts data to JSONArray
        JSONArray scrapedDataJson = itemListToJson(
                scrapedData,
                searchSettings,
                getRequiredKeys(itemInputs));

        // writes JSONArray of scraped data to file
        writeToFile(
                scrapedDataJson,
                searchSettings.get("fileName").toString(),
                searchSettings.get("folderName").toString());

    }
}
