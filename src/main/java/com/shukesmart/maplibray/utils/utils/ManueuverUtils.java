package com.shukesmart.maplibray.utils.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ManueuverUtils {
    public static JSONObject manueuver() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ARRIVE","You have arrived");
        return jsonObject;
    }
}
