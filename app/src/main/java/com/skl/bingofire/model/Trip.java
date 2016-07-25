package com.skl.bingofire.model;

/**
 * Created by Kristina on 6/28/16.
 */
public class Trip implements Comparable<Trip> {
    public String id;
    public String image;
    public String name;

    public Trip() {
    }

    @Override
    public int compareTo(Trip another) {
        if (another != null) {
            return id.compareTo(another.id);
        }

        return 0;
    }
}
