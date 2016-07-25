package com.skl.bingofire.spinners;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skl.bingofire.R;
import com.skl.bingofire.model.ChatPost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kristina on 7/3/16.
 */
public class ChatSpinnerAdapter  extends ArrayAdapter<ChatPost> {

    class ViewHolder {
        TextView nameTextView;
    }

    private List<ChatPost> list = new ArrayList<ChatPost>();

    private int textViewResourceId;
    private int selected = 0;

    public ChatSpinnerAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.textViewResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomDropDownView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {

            // Initialize views
            convertView = super.getView(position, convertView, parent);
            holder.nameTextView = (TextView) convertView.findViewById(textViewResourceId);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatPost chatPost = list.get(position);
        holder.nameTextView.setText(chatPost.topic);

        return convertView;
    }

    public View getCustomDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {

            // Initialize views
//            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = super.getDropDownView(position, convertView, parent);
            holder.nameTextView = (TextView) convertView.findViewById(textViewResourceId);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatPost chatPost = list.get(position);
        holder.nameTextView.setText(chatPost.topic);
        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ChatPost getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getPosition(ChatPost item) {
        return list.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public void setList(List<ChatPost> list) {
        this.list = new ArrayList<ChatPost>();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
}
