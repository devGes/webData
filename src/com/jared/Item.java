package com.jared;
import org.json.simple.JSONObject;

import java.util.*;

public class Item {
    private HashMap<String, String> dict = new HashMap<String,String>();

    public Item() {
        new Item(new HashMap<>());
    }

    public Item(HashMap<String, String> dict) {
        this.dict = dict;
    }


    public String getAttribute(String atr) {
        if (!this.dict.containsKey(atr)) {
            System.out.println("error. No attribute: " + atr);
            System.out.println(this.dict.toString());
            throw new AssertionError();
        }
        return this.dict.get(atr);
    }

    public void setAttribute(String key, String val) {
        assert val!=null;
        if (val.isEmpty() || val.isBlank()){
//            System.out.printf("Trying to write empty value. Key=%s%n", key);
        }
        else {
            this.dict.put(key, val);
        }
    }

    public JSONObject HashMaptoJSON() {
        return new JSONObject(this.dict);
    }

    public Set getSet() {
        return this.dict.keySet();
    }

    public boolean hasKey(String key) {
        return this.dict.containsKey(key);
    }

    public int size(){
        return this.dict.size();
    }

}