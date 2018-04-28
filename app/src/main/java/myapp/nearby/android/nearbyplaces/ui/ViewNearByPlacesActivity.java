package myapp.nearby.android.nearbyplaces.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import myapp.nearby.android.nearbyplaces.R;

import static myapp.nearby.android.nearbyplaces.ui.ViewNearByFragment.PHOTO_REFERENCE;
import static myapp.nearby.android.nearbyplaces.ui.ViewNearByFragment.PLACE_Address;
import static myapp.nearby.android.nearbyplaces.ui.ViewNearByFragment.PLACE_ID;
import static myapp.nearby.android.nearbyplaces.ui.ViewNearByFragment.PLACE_NAME;

public class ViewNearByPlacesActivity extends AppCompatActivity implements ViewNearByFragment.OnPlaceNameClickListener, ViewNearByFragment.DisplayFirstRecord {

    public static final int INDEX_PLACE_ID = 1;
    public static final int INDEX_PLACE_NAME = 2;
    public static final int INDEX_PLACE_ADDRESS = 4;
    public static final int INDEX_RATING = 5;
    public static final int INDEX_PHOTO_REFERENCE = 6;
    public static final int INDEX_DISTANCE = 7;

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mTwoPane;
    private boolean mSavedInstance = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_near_by_places);
        // Determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.place_details_container) != null) {

            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;
            if (savedInstanceState == null) {
                mSavedInstance = true;
                // In two-pane mode, add initial description fragment to the screen
                FragmentManager fragmentManager = getSupportFragmentManager();

                // Creating a new place details fragment
                PlaceDetailsFragment placeDetailsFragment = new PlaceDetailsFragment();
                placeDetailsFragment.setTwoPane(true);
                // Add the fragment to its container using a transaction
                fragmentManager.beginTransaction()
                        .add(R.id.place_details_container, placeDetailsFragment)
                        .commit();
            }
        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
            mTwoPane = false;
        }
    }

    @Override
    public void replaceFirstRecordFragment(String placeId, String placeName, String photo_reference,String placeAddress) {

        if (mTwoPane && mSavedInstance) {
            PlaceDetailsFragment newFragment = new PlaceDetailsFragment();
            newFragment.setPlaceDetails(placeId, placeName, photo_reference,placeAddress);
            newFragment.setTwoPane(true);
            // Replace the old  fragment with a new one
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.place_details_container, newFragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int Id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlaceNameSelected(String placeId, String placeName, String photo_reference,String placeAddress) {

        // Handle the two-pane case and replace existing fragments right when a new place name is selected from the master list
        if (mTwoPane) {
            // Create two=pane interaction

            PlaceDetailsFragment newFragment = new PlaceDetailsFragment();
            newFragment.setPlaceDetails(placeId, placeName, photo_reference,placeAddress);
            newFragment.setTwoPane(true);
            // Replace the old  fragment with a new one
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.place_details_container, newFragment)
                    .commit();
        } else {
            Intent intent = new Intent(ViewNearByPlacesActivity.this, PlaceDetailsActivity.class);
            intent.putExtra(PLACE_ID, placeId);
            intent.putExtra(PLACE_NAME, placeName);
            intent.putExtra(PHOTO_REFERENCE, photo_reference);
            intent.putExtra(PLACE_Address,placeAddress);
            startActivity(intent);
        }

    }


}
