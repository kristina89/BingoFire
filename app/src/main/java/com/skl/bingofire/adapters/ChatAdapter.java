package com.skl.bingofire.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skl.bingofire.R;
import com.skl.bingofire.model.ChatPost;
import com.skl.bingofire.model.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kristina on 6/12/16.
 */
public class ChatAdapter extends LinearAdapter<ChatPost> {

    public ChatAdapter(ViewGroup parent) {
        super(parent);
    }

    @Override
    protected View getView(ChatPost item) {
        View view = inflater.inflate(R.layout.chat_list_item, null);

        TextView topicTextView = (TextView)view.findViewById(R.id.topicTextView);
        ViewGroup messagesContainer = (ViewGroup) view.findViewById(R.id.messagesContainer);

        MessageAdapter messageAdapter = new MessageAdapter(messagesContainer);
        ArrayList messages = new ArrayList<Message>();
        messages.addAll(item.messages.values());
        Collections.sort(messages);
        messageAdapter.setList(messages);

        topicTextView.setText(item.topic);

        if (item.color != null && !item.color.isEmpty()) {
            view.setBackgroundColor(Color.parseColor(item.color));
        }

        return view;
    }
}
