package com.skl.bingofire.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.skl.bingofire.R;
import com.skl.bingofire.model.ChatPost;
import com.skl.bingofire.spinners.ChatSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kristina on 7/3/16.
 */
public class AddMessageDialog extends DialogFragment {

    public static final String ARGUMENT_TOPICS = "AddMessageDialog.topics";

    private List<ChatPost> topics = new ArrayList<ChatPost>();
    private OnMessageAddListener messageAddListener;
    private Spinner chatTopicsSpinner;
    private EditText messageEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments.containsKey(ARGUMENT_TOPICS)) {
            topics = (ArrayList<ChatPost>) arguments.getSerializable(ARGUMENT_TOPICS);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnMessageAddListener) {
            messageAddListener = (OnMessageAddListener) activity;
        } else {
            throw new ClassCastException("Should implement OnMessageAddListener interface!");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.add_message_layout_view, null);

        chatTopicsSpinner = (Spinner) view.findViewById(R.id.chatTopicsSpinner);
        messageEditText = (EditText) view.findViewById(R.id.messageEditText);

        final ChatSpinnerAdapter adapter = new ChatSpinnerAdapter(getActivity(), R.layout.spinner_list_item, R.id.nameTextView);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_list_item);
        adapter.setList(topics);

        chatTopicsSpinner.setAdapter(adapter);

        builder.setView(view);
        builder.setTitle(R.string.add_message_title);
        builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                messageAddListener.onAddMessage((ChatPost) chatTopicsSpinner.getSelectedItem(), messageEditText.getText().toString().trim());
            }
        })
        .setNegativeButton(R.string.cancel, null);

        builder.setCancelable(false);
        Dialog dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public interface OnMessageAddListener {
        public void onAddMessage(ChatPost chat, String message);
    }
}
