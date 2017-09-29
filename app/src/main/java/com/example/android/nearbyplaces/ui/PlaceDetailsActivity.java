package com.example.android.nearbyplaces.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.nearbyplaces.R;

public class PlaceDetailsActivity extends AppCompatActivity {

    public String mPlaceId = "";
    private String mPhoto_reference;
    private String mIconPath;
    private String mPlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ViewNearByFragment.PLACE_ID)) {
            mPlaceId = intent.getStringExtra(ViewNearByFragment.PLACE_ID);
            mPlaceName = intent.getStringExtra(ViewNearByFragment.PLACE_NAME);
            mPhoto_reference = intent.getStringExtra(ViewNearByFragment.PHOTO_REFERENCE);

        }

        // Only create new fragments when there is no previously saved state
        if (savedInstanceState == null) {

            PlaceDetailsFragment placeDetailsFragment = new PlaceDetailsFragment();
            placeDetailsFragment.setPlaceDetails(mPlaceId, mPlaceName, mPhoto_reference);
            placeDetailsFragment.setTwoPane(false);
            // Replace the old  fragment with a new one
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.place_details_container, placeDetailsFragment)
                    .commit();

        }
    }
}
