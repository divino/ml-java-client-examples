package com.marklogic.example.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Created by dbagayau on 08/02/2017.
 */
public class JsonUtil {
    public static String prettify(String string) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(string);
        return gson.toJson(je);
    }
}
