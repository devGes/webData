package com.jared;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class SiteScraper {


    public static List<Item> scrapeSite(JSONArray itemInputs, JSONObject searchSettings) {

        WebClient web = new WebClient();
        web.setJavaScriptEnabled(false);
//        web.setCssEnabled(false);

        HtmlPage page = getPage(web, searchSettings.get("SearchURL").toString());
        assert page != null;

        List<HtmlElement> elements = getRootElements(page, searchSettings);

        return getProductList(elements, itemInputs);


    }

    public static List<HtmlElement> getRootElements(HtmlPage page, JSONObject searchSettings) {
        List<HtmlElement> elements = new ArrayList<HtmlElement>();
        String elemClass = "";
        boolean searchType;

        for (HtmlElement elem : page.getHtmlElementDescendants()) {

            if ( (Objects.equals(searchSettings.get("searchType").toString(), "Attribute")) || (Objects.equals(searchSettings.get("searchType").toString(), "class"))) {
                if(Objects.equals(searchSettings.get("pull").toString(), "asText")) {
                    elemClass = elem.asText();
                } else if (Objects.equals(searchSettings.get("pull").toString(), searchSettings.get("searchType").toString())) {
                    elemClass = elem.getAttribute(searchSettings.get("searchString").toString());
                }
            }

            if (!elemClass.isEmpty()) {
                elements.add(elem);
            }

        }


        System.out.println(elements.size() + " root elements");
        return elements;
    }

    public static void setAttributeByJson(Item item, JSONObject currentInput, HtmlElement childElem) {
        boolean isFound = false;
//        System.out.println(currentInput.get("searchType").toString());
        if (    (Objects.equals(currentInput.get("searchType").toString(), "Attribute"))
                || (Objects.equals(currentInput.get("searchType").toString(), "class"))) {
            isFound = (childElem.getAttribute(currentInput.get("searchType").toString()).equals(currentInput.get("searchString").toString()));
        } else if (currentInput.get("searchType") == "QualifiedName") {
            isFound = Objects.equals(childElem.getQualifiedName().toString(), currentInput.get("searchAttribute").toString());
        }


        if (isFound) {
            if (currentInput.get("pull") == "Attribute") {
                item.setAttribute(currentInput.get("name").toString(), childElem.getAttribute(currentInput.get("searchAttribute").toString()));
            } else if (Objects.equals(currentInput.get("pull").toString(), "asText")) {
                item.setAttribute(currentInput.get("name").toString(), childElem.asText());
            }
        }
    }

    private static List<Item> getProductList(List<HtmlElement> elements, JSONArray itemInputs) {
        List<Item> products = new ArrayList<>();
        Item item;
        for (HtmlElement elem : elements) {
            item = new Item();

//            item.setAttribute("amzId", elem.getAttribute((String) searchSettings.get("searchString")));

            for (HtmlElement childElem : elem.getHtmlElementDescendants()) {

                for (Object currentInput :  itemInputs) {
                    setAttributeByJson( item, (JSONObject) currentInput, childElem);
                }

            }
            products.add(item);
        }



        return products;
    }



    /**
     * @param elementClasses
     * @param searchClasses
     * @return
     */
    public static boolean hasMatch(List<String> elementClasses, List<String> searchClasses) {
        for (String searchClass : searchClasses) {
            boolean found = false;
            for (String elementClass : elementClasses) {
                if (elementClass.contains(searchClass)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }

        }
        return true;
    }

    /**
     * @param element
     * @param inputs
     * @param inputType
     * @return
     */
    public static boolean hasClassMatch(HtmlElement element, JSONObject inputs, String inputType) {
        return hasMatch(
                List.of(element.getAttribute("class").split(" ")),
                Arrays.asList(inputs.get(inputType).toString().split(" ")));

    }

    /**
     * gets webClient-page for a given url
     *
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
