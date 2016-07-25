package com.skl.bingofire.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kristina on 6/28/16.
 */
public class MyProfile {

    public String tripId;

    public MyProfile() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tripId", tripId);

        return result;
    }
}
