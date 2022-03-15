package com.jared;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.jared.Utilities.extractAmzIdFromUrl;


public class SiteScraper {

    /**
     *
     * @param inputsJson
     * @return
     */
    public static List<Item> scrapeSite(JSONObject inputsJson) {

        WebClient web = new WebClient();
        web.setJavaScriptEnabled(false);
//        web.setCssEnabled(false);

        HtmlPage page = getPage(web, inputsJson.get("SearchURL").toString());
        assert page != null;

        List<HtmlElement> elements = getElements(page, inputsJson);

        List<Item> products = new ArrayList<>();
        Item item;

        for (HtmlElement elem : elements) {
            item = new Item();
            item.setAttribute("amz1D", elem.getAttribute((String) inputsJson.get("amzId")));

            for (HtmlElement childElem : elem.getHtmlElementDescendants()){

                if (childElem.getAttribute("class").equals(inputsJson.get("priceWhole"))) {
                    item.setAttribute("priceWhole", childElem.asText());
                }

                if (childElem.getAttribute("class").equals(inputsJson.get("priceSymbol"))) {
                    item.setAttribute("priceSymbol", childElem.asText());
                }

                if (childElem.getAttribute("class").equals(inputsJson.get("priceFraction"))) {
                    item.setAttribute("priceFraction", childElem.asText());
                }

                if (childElem.getAttribute("class").equals(inputsJson.get("rating5Star"))) {
                    item.setAttribute("rating5Star", childElem.asText());
                }


                if (hasClassMatch(childElem, inputsJson, "url")) {
                    String urlNew = inputsJson.get("website") + childElem.getAttribute("href");
                    item.setAttribute("url", urlNew);
                }


                if (Objects.equals(childElem.getQualifiedName().toString(), "img")) {
                    item.setAttribute("image", childElem.getAttribute("src"));
                }

                if (Objects.equals(childElem.getQualifiedName().toString(), "img")) {
                    item.setAttribute("title", childElem.getAttribute("alt"));
                }


            }
            products.add(item);
        }


        return products;
    }

    private static List<HtmlElement> getElements(HtmlPage page, JSONObject inputsJson) {
        List<HtmlElement> elements = new ArrayList<HtmlElement>();
        for (HtmlElement elem : page.getHtmlElementDescendants()) {
            String elemClass = elem.getAttribute("data-asin");
//            if (!elemClass.isEmpty()) {System.out.println(elemClass);}
            if (!elemClass.isEmpty()) {
//                System.out.println(elemClass);
                elements.add(elem);
            } else {
//                System.out.println("empty");
            }
        }
        System.out.println(elements.size() + " root elements");
        return elements;
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
                if(elementClass.contains(searchClass)){
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

    /**
     * gets webClient-page for a given url
     * @param webClient
     * @param url
     * @return page
     */
    public static HtmlPage getPage(WebClient webClient, String url) {
        try {
            webClient.setCssEnabled(false);
            return webClient.getPage(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}
