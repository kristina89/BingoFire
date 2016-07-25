package com.skl.bingofire.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kristina on 6/12/16.
 */
public abstract class LinearAdapter<T extends Comparable> {

    private List<T> list = new ArrayList<>();

    protected ViewGroup parent;
    protected LayoutInflater inflater;

    protected abstract View getView(T item);

    public LinearAdapter(ViewGroup parent) {
        this.parent = parent;
        this.inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(List<T> list) {
        this.list = list;
        updateView();
    }

    public void updateView() {
        Collections.sort(list);
        parent.removeAllViews();
        for (T item : list) {
            View view = getView(item);
            parent.addView(view);
        }
    }

    public void addItem(T item) {
        if (!list.contains(item)) {
            list.add(item);
            updateView();
        }
    }

    public void updateItem(T item) {
        boolean res = list.remove(item);
        if (res) {
            list.add(item);
            updateView();
        }
    }

    public void removeItem(T item) {
        boolean res = list.remove(item);
        if (res) {
            updateView();
        }
    }
}
