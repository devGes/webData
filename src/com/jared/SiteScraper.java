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

    /**
     * @param inputsJson
     * @return
     */
    public static List<Item> scrapeSite(JSONArray inputsJson, JSONObject searchSettings) {

        WebClient web = new WebClient();
        web.setJavaScriptEnabled(false);
//        web.setCssEnabled(false);

        HtmlPage page = getPage(web, searchSettings.get("SearchURL").toString());


        List<Item> products = new ArrayList<>();
        JSONObject currentInput = new JSONObject();
        Item item;

        List<HtmlElement> elements = getRootElements(page, searchSettings);


        for (HtmlElement elem : elements) {
            item = new Item();

            item.setAttribute("amzID", elem.getAttribute((String) currentInput.get("amzId")));

            for (HtmlElement childElem : elem.getHtmlElementDescendants()) {

                if (childElem.getAttribute("class").equals(currentInput.get("priceWhole"))) {
                    item.setAttribute("priceWhole", childElem.asText());
                }

                if (childElem.getAttribute("class").equals(currentInput.get("priceSymbol"))) {
                    item.setAttribute("priceSymbol", childElem.asText());
                }

                if (childElem.getAttribute("class").equals(currentInput.get("priceFraction"))) {
                    item.setAttribute("priceFraction", childElem.asText());
                }

                if (childElem.getAttribute("class").equals(currentInput.get("rating5Star"))) {
                    item.setAttribute("rating5Star", childElem.asText());
                }

                if (childElem.getAttribute("class").equals(currentInput.get("url"))) {
                    String urlNew = searchSettings.get("website") + childElem.getAttribute("href");
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

    private static boolean isRootLevel(JSONObject currentInput) {
        if (currentInput.containsKey("rootLevel")) {
                return ((boolean) currentInput.get("rootLevel"));
            }
        return false;
    }

    private static void setAttributeByJson(Item item, JSONObject currentInput) {
        //
    }

    private static List<HtmlElement> getRootElements(HtmlPage page, JSONObject searchSettings) {
        List<HtmlElement> elements = new ArrayList<HtmlElement>();
        String elemClass = "";
        boolean searchType;

        for (HtmlElement elem : page.getHtmlElementDescendants()) {

            if (Objects.equals(searchSettings.get("searchType").toString(), "Attribute")) {
                System.out.println("Attribute: " + searchSettings.get("searchType"));
                if(Objects.equals(searchSettings.get("pull").toString(), "asText")) {
                    elemClass = elem.asText();
                } else if (Objects.equals(searchSettings.get("pull").toString(), "Attribute")) {
                    elemClass = elem.getAttribute(searchSettings.get("searchString").toString());
                }
            } else if (Objects.equals(searchSettings.get("searchType").toString(), "class")) {
                System.out.println("class: " + searchSettings.get("searchType"));
                if(Objects.equals(searchSettings.get("pull").toString(), "asText")) {
                    elemClass = elem.asText();
                } else if (Objects.equals(searchSettings.get("pull").toString(), "Attribute")) {
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
