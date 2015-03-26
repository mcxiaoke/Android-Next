package com.mcxiaoke.next.samples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * User: mcxiaoke
 * Date: 15/3/26
 * Time: 11:50
 */
public class SampleUtils {

    public static String prettyPrintJson(final String rawJson) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(new JsonParser().parse(rawJson));
    }
}
