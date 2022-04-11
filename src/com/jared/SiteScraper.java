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

    public static List<Item> scrapeSite(
            JSONArray itemInputs,
            JSONObject searchSettings,
            String websiteUrl,
            WebClient web)
    {
        return scrapeSite(
                itemInputs,
                searchSettings,
                websiteUrl,
                web,
                1);
    }

    public static List<Item> scrapeSite(
            JSONArray itemInputs,
            JSONObject searchSettings,
            String websiteUrl,
            WebClient web,
            long currentPageNum) {

        System.out.println("currentPageNum: " + currentPageNum);
//        web.setCssEnabled(false);
        HtmlPage page = getPage(web, websiteUrl);
        assert page != null;

        List<HtmlElement> elements = getRootElements(page, searchSettings);

        List<Item> products = new ArrayList<Item>();
        if (currentPageNum < Long.parseLong(searchSettings.get("numOfNextPages").toString())) {
            products.addAll(scrapeSite(
                                itemInputs,
                                searchSettings,
                                getNextPage(page, searchSettings),
                                web,
                                currentPageNum+1));
        }
        products.addAll(getProductList(elements, itemInputs));
      return products;
    }


    public static String getNextPage(HtmlPage page, JSONObject searchSettings) {
        for (HtmlElement elem : page.getHtmlElementDescendants()) {
            if(isEqual(searchSettings, "nextSearchType", "Attribute")) {
                if (elem.getAttribute("aria-label").contains( searchSettings.get("nextsearchAttribute").toString())) {
                    return searchSettings.get("website").toString() +  elem.getAttribute("href").toString();
                }
            }
        }
        System.out.println("Error: getNextPage");
        return "";
    }

    public static List<HtmlElement> getRootElements(HtmlPage page, JSONObject searchSettings) {
        List<HtmlElement> elements = new ArrayList<HtmlElement>();
        String elemClass = "";
        boolean searchType;

        for (HtmlElement elem : page.getHtmlElementDescendants()) {

            if ( (Objects.equals(searchSettings.get("searchType").toString(), "Attribute")) || (Objects.equals(searchSettings.get("searchType").toString(), "class"))) {
                if(Objects.equals(searchSettings.get("pull").toString(), "asText")) {
                    elemClass = elem.asNormalizedText();
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

    public static boolean isEqual(JSONObject currentInput, String searchJSON, String string) {
        return (Objects.equals(currentInput.get(searchJSON).toString(), string));
    }

    public static void setAttributeByJson(Item item, JSONObject currentInput, HtmlElement childElem) {
        boolean isFound = false;

        if (isEqual(currentInput, "searchType", "class")) {
            isFound = isEqual(currentInput, "searchString", childElem.getAttribute(currentInput.get("searchType").toString()));
        } else if (isEqual(currentInput, "searchType", "Attribute")) {
            isFound = ( childElem.getAttribute(currentInput.get("searchString").toString()).length() != 0 );
        }   else if (isEqual(currentInput, "searchType", "QualifiedName")) {
            isFound = isEqual(currentInput, "searchString", childElem.getQualifiedName().toString());
        }

        if (isFound) {
            if (Objects.equals(currentInput.get("pull").toString(), "Attribute")) {
                item.setAttribute(currentInput.get("name").toString(), childElem.getAttribute(currentInput.get("searchAttribute").toString()));
            } else if (Objects.equals(currentInput.get("pull").toString(), "asText")) {
                item.setAttribute(currentInput.get("name").toString(), childElem.asNormalizedText());
            }
        }
    }

    private static List<Item> getProductList(List<HtmlElement> elements, JSONArray itemInputs) {
        List<Item> products = new ArrayList<>();
        Item item;

        for (HtmlElement elem : elements) {
            item = new Item();

//            item.setAttribute("amzId", elem.getAttribute((String) searchSettings.get("searchString")));
            for (Object currentInput0 :  itemInputs) {
                JSONObject currentInput = (JSONObject) currentInput0;
                if (isRootLevel(currentInput)) {
//                    System.out.println("isRootLevel: " + currentInput.get("name"));
//                    System.out.println("searchType: " + currentInput.get("searchType").toString());
//                    System.out.println("elem: " + elem.getAttribute("data-asin"));
                    setAttributeByJson(item, currentInput, elem);
                } else {
                    for (HtmlElement childElem : elem.getHtmlElementDescendants()) {
                        if (! isRootLevel(currentInput)) {
                            setAttributeByJson( item, currentInput, childElem);
                        }
                    }
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
            webClient.getOptions().setCssEnabled(false);
            return webClient.getPage(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isRootLevel(JSONObject currentInput) {
        if (currentInput.containsKey("rootLevel")) {
            return ((boolean) currentInput.get("rootLevel"));
        }
        return false;
    }

}
