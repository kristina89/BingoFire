package com.skl.bingofire.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kristina on 6/12/16.
 */
public class Message implements Comparable, Serializable {
    public String uid;
    public String user;
    public String message;
    public long createdDate;

    public Message() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("user", user);
        result.put("message", message);
        result.put("createdDate", createdDate);

        return result;
    }

    @Override
    public int compareTo(Object another) {
        if (another instanceof Message) {
            if (createdDate < ((Message) another).createdDate) {
                return -1;
            } else {
                return 1;
            }
        }
        return 0;
    }
}
