package com.jared;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class SiteScraper {

    /**
     *
     * @param inputsJson
     * @return
     */
    public List<Item> scrapeSite(JSONObject inputsJson) {

        WebClient web = new WebClient();
        web.setJavaScriptEnabled(false);
        web.setCssEnabled(false);

        HtmlPage page = URLmethods.getPage(web, inputsJson.get("url").toString());

        assert page != null;

        List<HtmlElement> elements = page.getElementsByIdAndOrName( inputsJson.get("rootIdOrName").toString() );
//        List<HtmlElement> elements = (List<HtmlElement>) page.getByXPath( inputsJson.get("xPath").toString());
        List<Item> products = new ArrayList<>();
        Item item;

        for (HtmlElement elem : elements) {
            item = new Item();

            for (HtmlElement childElem : elem.getHtmlElementDescendants()){

                if (hasClassMatch(childElem, inputsJson, "price")) {
                    item.setAttribute("price", childElem.asText());
                }

                if (hasClassMatch(childElem, inputsJson, "authorLink")) {
                    item.setAttribute("author", childElem.asText());
                }

                if (hasClassMatch(childElem, inputsJson, "authorNoLink")) {
                    item.setAttribute("author", childElem.asText());
                }

                if (hasClassMatch(childElem, inputsJson, "rating5Star")) {
                    item.setAttribute("rating5Star", childElem.asText());
                }


                if (hasClassMatch(childElem, inputsJson, "image")) {
                    item.setAttribute("image", childElem.getAttribute("src"));
                    item.setAttribute("title", childElem.getAttribute("alt"));
                }

                if (hasClassMatch(childElem, inputsJson, "multiple")) {
                    String urlNew = inputsJson.get("website") + childElem.getAttribute("href");
                    item.setAttribute("url", urlNew);

                }

            }
            products.add(item);
        }


        return products;
    }

    /**
     *
     * @param elementClasses
     * @param searchClasses
     * @return
     */
    public static boolean hasMatch(List<String> elementClasses, List<String> searchClasses) {
        for(String searchClass : searchClasses) {
            boolean found = false;
            for(String elementClass : elementClasses) {
                if(elementClass.equals(searchClass)){
                    found = true;
                    break;
                }
            }
            if(!found) {
                return false;
            }

        }
        return true;
    }

    /**
     *
     * @param element
     * @param inputs
     * @param inputType
     * @return
     */
    public static boolean hasClassMatch(HtmlElement element, JSONObject inputs, String inputType){
        return hasMatch(
                List.of(element.getAttribute("class").split(" ")),
                Arrays.asList(inputs.get(inputType).toString().split(" ")));

    }




}
