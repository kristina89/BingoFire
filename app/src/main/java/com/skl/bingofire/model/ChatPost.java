package com.skl.bingofire.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kristina on 6/12/16.
 */
public class ChatPost implements Comparable<ChatPost>, Serializable {
    public String id;
    public String topic;
    public String color;
    public HashMap<String, Message> messages;

    public ChatPost() {
    }

    public ChatPost(String id, String topic) {
        this.id = id;
        this.topic = topic;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ChatPost && ((ChatPost) o).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(ChatPost another) {
        if (another != null) {
            return id.compareTo(another.id);
        }

        return 0;
    }
}
