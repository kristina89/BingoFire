package com.skl.bingofire.adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.skl.bingofire.R;
import com.skl.bingofire.VolleyHelper;
import com.skl.bingofire.model.Trip;
import com.skl.bingofire.utils.ImageUtil;

/**
 * Created by Kristina on 6/28/16.
 */
public class CardViewAdapter extends LinearAdapter<Trip> {

    private String selectedTrip;

    private ImageLoader imageLoader;
    private OnTripSelectedListener onTripSelectedListener;

    public CardViewAdapter(ViewGroup parent, OnTripSelectedListener onTripSelectedListener) {
        super(parent);

        imageLoader = VolleyHelper.getImageLoader();
        this.onTripSelectedListener = onTripSelectedListener;
    }

    @Override
    protected View getView(final Trip item) {
        final View view = inflater.inflate(R.layout.trip_list_item, null);

        NetworkImageView tripNetworkImageView = (NetworkImageView) view.findViewById(R.id.tripNetworkImageView);
        TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        final CheckBox tripCheckBox = (CheckBox) view.findViewById(R.id.tripCheckBox);

        if (item.image != null) {

            ImageLoader.ImageContainer imageRequest = imageLoader.get(item.image, ImageLoader.getImageListener(tripNetworkImageView, R.drawable.trip_stub, R.drawable.trip_stub));
            tripNetworkImageView.setImageUrl(item.image, imageLoader);
        }

        nameTextView.setText(item.name);

        if (selectedTrip != null) {
            if (item.id.equals(selectedTrip)) {
                tripCheckBox.setChecked(true);
            } else {
                tripCheckBox.setEnabled(false);
            }
        }

        tripCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (tripCheckBox.isEnabled()) {
                    if (isChecked) {
                        selectedTrip = item.id;
                        onTripSelectedListener.onTripSelected(item);
                    } else {
                        selectedTrip = null;
                        onTripSelectedListener.onTripSelected(null);
                    }

                    updateView();
                }
            }
        });

        return view;
    }

    public String getSelectedTrip() {
        return selectedTrip;
    }

    public interface OnTripSelectedListener {
        public void onTripSelected(Trip trip);
    }
}
