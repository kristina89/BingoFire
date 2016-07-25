package com.skl.bingofire.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skl.bingofire.R;
import com.skl.bingofire.model.Message;

/**
 * Created by Kristina on 6/12/16.
 */
public class MessageAdapter extends LinearAdapter<Message> {

    public MessageAdapter(ViewGroup parent) {
        super(parent);
    }

    @Override
    protected View getView(Message item) {
        View view = inflater.inflate(R.layout.message_list_item, null);

        TextView userTextView = (TextView) view.findViewById(R.id.userTextView);
        TextView messageTextView = (TextView) view.findViewById(R.id.messageTextView);

        userTextView.setText(item.user);
        messageTextView.setText(item.message);

        return view;
    }
}
