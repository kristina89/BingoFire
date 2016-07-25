package com.skl.bingofire.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skl.bingofire.R;
import com.skl.bingofire.adapters.CardViewAdapter;
import com.skl.bingofire.model.ChatPost;
import com.skl.bingofire.model.MyProfile;
import com.skl.bingofire.model.Trip;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity implements CardViewAdapter.OnTripSelectedListener {

    private EditText userNameEditText;
    private ViewGroup tripCardsContainer;
    private CardViewAdapter cardViewAdapter;
    private Button nextButton;
    private ProgressBar progressBar;
    private SaveUserProfileAsyncTask saveUserProfileAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        tripCardsContainer = (ViewGroup) findViewById(R.id.tripCardsContainer);
        cardViewAdapter = new CardViewAdapter(tripCardsContainer, this);
        nextButton = (Button) findViewById(R.id.nextButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextButton.isEnabled()) {
                    doSaveUserProfile();
                }
            }
        });


        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (userNameEditText.getText().toString().trim().isEmpty()) {
                    nextButton.setEnabled(false);
                } else if (cardViewAdapter.getSelectedTrip() != null) {
                    nextButton.setEnabled(true);
                }
            }
        });
        readFromDatabase();
    }

    private void doSaveUserProfile() {
        showProgressbar(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userName = userNameEditText.getText().toString();
        String tripId = cardViewAdapter.getSelectedTrip();

        final MyProfile myProfile = new MyProfile();
        myProfile.tripId = tripId;


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");

                            if (saveUserProfileAsyncTask == null) {
                                saveUserProfileAsyncTask = new SaveUserProfileAsyncTask(myProfile);
                                saveUserProfileAsyncTask.execute();
                            }
                        }
                    }
                });
    }


    private boolean updateUserProfile(MyProfile myProfile) {
        mFirebaseAnalytics.setUserProperty("user_trip_property", myProfile.tripId);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        Map<String, Object> userValues = myProfile.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + user.getUid(), userValues);

        databaseReference.updateChildren(childUpdates);
        return true;
    }


    private void readFromDatabase() {
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference chatRef = database.getReference("wish_trips_enum");
        Query queryRef = chatRef.orderByChild("id");

        queryRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                Trip trip = snapshot.getValue(Trip.class);
                System.out.println("NEW Trip: " + trip.name);

                cardViewAdapter.addItem(trip);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                System.out.println("CHANGED Trip: " + trip.name);

                cardViewAdapter.updateItem(trip);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Trip trip = dataSnapshot.getValue(Trip.class);
                System.out.println("REMOVED Trip: " + trip.name);

                cardViewAdapter.removeItem(trip);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onTripSelected(Trip trip) {
        if (trip != null && userNameEditText.getText() != null && !userNameEditText.getText().toString().trim().isEmpty()) {
            nextButton.setEnabled(true);
        } else {
            nextButton.setEnabled(false);
        }
    }

    private void showProgressbar(boolean show) {
        nextButton.setVisibility(show ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private class SaveUserProfileAsyncTask extends AsyncTask<Void, Void, Boolean> {

        MyProfile myProfile;

        public SaveUserProfileAsyncTask(MyProfile myProfile) {
            this.myProfile = myProfile;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return updateUserProfile(myProfile);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            showProgressbar(false);
            if (result) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }
}
