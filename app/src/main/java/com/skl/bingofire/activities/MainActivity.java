package com.skl.bingofire.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.skl.bingofire.R;
import com.skl.bingofire.adapters.ChatAdapter;
import com.skl.bingofire.dialogs.AddMessageDialog;
import com.skl.bingofire.model.ChatPost;
import com.skl.bingofire.model.Message;
import com.skl.bingofire.model.MyProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements AddMessageDialog.OnMessageAddListener {

    private String TAG = MainActivity.class.getSimpleName();

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private long cacheExpiration = 0;

    private ViewGroup mainLayoutViewGroup;
    private ViewGroup chatsContainer;
    private TextView titleTextView;
    private TextView userNameTextView;
    private TextView useTripTextView;

    private ChatAdapter chatAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private ArrayList<ChatPost> chatTopicsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton refreshActionButton = (FloatingActionButton) findViewById(R.id.refreshActionButton);
        refreshActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                fetchFirebaseRemoteConfigs();
            }
        });

        FloatingActionButton addActionButton = (FloatingActionButton) findViewById(R.id.addActionButton);
        addActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAddMessageDialog();
            }
        });

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        userNameTextView = (TextView) findViewById(R.id.userNameTextView);
        useTripTextView = (TextView) findViewById(R.id.useTripTextView);
        mainLayoutViewGroup = (ViewGroup) findViewById(R.id.mainLayoutViewGroup);
        chatsContainer = (ViewGroup) findViewById(R.id.chatsContainer);
        chatAdapter = new ChatAdapter(chatsContainer);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        setBackgroundColor();

        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.

        Log.d(TAG, "cacheExpiration = " + cacheExpiration);
    }

    private void openAddMessageDialog() {
        AddMessageDialog messageDialog = new AddMessageDialog();
        Bundle arguments = messageDialog.getArguments();
        if (arguments == null) {
            arguments = new Bundle();
        }
        arguments.putSerializable(AddMessageDialog.ARGUMENT_TOPICS, chatTopicsList);
        messageDialog.setArguments(arguments);
        messageDialog.show(getFragmentManager(), "add_message_dialog");
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            if (user.getDisplayName() != null) {
                userNameTextView.setText(getString(R.string.hello_user, mAuth.getCurrentUser().getDisplayName()));
                fetchFirebaseRemoteConfigs();
                readFromDatabase();
            } else {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    private void fetchFirebaseRemoteConfigs() {

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Fetch Succeeded");
                        // Once the config is successfully fetched it must be activated before newly fetched
                        // values are returned.
                        mFirebaseRemoteConfig.activateFetched();
                        setBackgroundColor();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(TAG, "Fetch failed");
                        mainLayoutViewGroup.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                    }
                });
    }

    private void setBackgroundColor() {
        String backgroundColor = mFirebaseRemoteConfig.getString("background_color");
        mainLayoutViewGroup.setBackgroundColor(Color.parseColor(backgroundColor));
    }

    private void readFromDatabase() {
    // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference titleRef = database.getReference("title");

        titleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                titleTextView.setText(value);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                titleTextView.setText(error.getMessage());
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        DatabaseReference profileRef = database.getReference("users");

        profileRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MyProfile value = dataSnapshot.getValue(MyProfile.class);
                mFirebaseAnalytics.setUserProperty("user_trip_property", value.tripId);

                useTripTextView.setText(getString(R.string.user_trip, value.tripId));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                useTripTextView.setText(error.getMessage());
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        DatabaseReference chatRef = database.getReference("chats");
        chatRef.keepSynced(true);
        Query queryRef = chatRef.orderByChild("id");

        queryRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                ChatPost newPost = snapshot.getValue(ChatPost.class);
                ChatPost topic = new ChatPost(newPost.id, newPost.topic);
                addChatTopic(topic);
                System.out.println("NEW Child Topic: " + newPost.topic);

                chatAdapter.addItem(newPost);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ChatPost newPost = dataSnapshot.getValue(ChatPost.class);
                ChatPost topic = new ChatPost(newPost.id, newPost.topic);
                addChatTopic(topic);
                System.out.println("CHANGED Child Topic: " + newPost.topic);

                chatAdapter.updateItem(newPost);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ChatPost newPost = dataSnapshot.getValue(ChatPost.class);
                ChatPost topic = new ChatPost(newPost.id, newPost.topic);
                chatTopicsList.add(topic);
                System.out.println("REMOVED Child Topic: " + newPost.topic);

                chatAdapter.removeItem(newPost);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addChatTopic(ChatPost chatPost) {
        if (chatTopicsList.contains(chatPost)) {
            chatTopicsList.remove(chatPost);
        }

        chatTopicsList.add(chatPost);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showWarningDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(null)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAddMessageDialog();
                    }
                });
        builder.create().show();
    }


    @Override
    public void onAddMessage(ChatPost chat, String message) {
        if (message.isEmpty()) {
            showWarningDialog(getString(R.string.add_message_title), getString(R.string.not_entered_text));
        } else {
            sendMessage(chat, message);
        }
    }

    private void sendMessage(ChatPost chat, String message) {

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference("chats");
            DatabaseReference chatReference = databaseReference.child(chat.id);
            String messageId = chatReference.child("messages").push().getKey();
            Message chatMessage = new Message();
            chatMessage.user = user.getDisplayName();
            chatMessage.uid = user.getUid();
            chatMessage.message = message;
            chatMessage.createdDate = new Date().getTime();
            Map<String, Object> messageValues = chatMessage.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/messages/" + messageId, messageValues);

            chatReference.updateChildren(childUpdates);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
