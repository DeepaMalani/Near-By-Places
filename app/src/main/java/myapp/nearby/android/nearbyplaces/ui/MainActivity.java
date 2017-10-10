package myapp.nearby.android.nearbyplaces.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import myapp.nearby.PlacesTypesSource;
import myapp.nearby.android.nearbyplaces.BuildConfig;
import myapp.nearby.android.nearbyplaces.R;
import myapp.nearby.android.nearbyplaces.adapter.PlacesTypeAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 111;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public static String TITLE = "activity_title";
    public static String TYPE = "type";
    public static String LOCATION = "location";
    String[] mPlaceTitles;
    String[] mPlaceValues;
    TypedArray mGridViewIcons;
    @BindView(myapp.nearby.android.nearbyplaces.R.id.recycler_view_places)
    RecyclerView mRecyclerView;
//    @BindView(R.id.adView)
//    AdView mAdView;
//    @BindView(R.id.text_view_location)
//    TextView textViewLocation;
    private PlacesTypeAdapter mPlacesTypeAdapter;
    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    private FusedLocationProviderClient mFusedLocationClient;
    private double mLatitude;
    private double mLongitude;
    private String strLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(myapp.nearby.android.nearbyplaces.R.layout.activity_main);

        ButterKnife.bind(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .build();
//        mAdView.loadAd(adRequest);

        //Get the place type values and titles using java library.
        PlacesTypesSource placesTypesSource = new PlacesTypesSource();
        mPlaceTitles = placesTypesSource.getPlaceTitles();
        mPlaceValues = placesTypesSource.getPlaceValues();

        mGridViewIcons = this.getResources().obtainTypedArray(myapp.nearby.android.nearbyplaces.R.array.places_icon_array);


        boolean isPhone = getResources().getBoolean(myapp.nearby.android.nearbyplaces.R.bool.is_phone);
        //Set recycler view liner layout manager for phones and grid layout manager for tablets
        if (isPhone) {
            //Set linear layout manager for recycler view phone
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        } else {
            //Set grid layout manager for recycler view tablets
            GridLayoutManager gridLayoutManager
                    = new GridLayoutManager(MainActivity.this, numberOfColumns());

            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }

        //RecipeAdapter is responsible for linking the data with recycler views
        mPlacesTypeAdapter = new PlacesTypeAdapter(MainActivity.this, mPlaceTitles, mGridViewIcons, mPlaceValues);
        //Attach adapter to recycler view
        mRecyclerView.setAdapter(mPlacesTypeAdapter);
        mPlacesTypeAdapter.setOnItemClickListener(new PlacesTypeAdapter.PlaceAdapterOnClickHandler() {
            @Override
            public void onClick(String placeTitle, String placeValue) {

                Intent intent = new Intent(MainActivity.this, ViewNearByPlacesActivity.class);
                intent.putExtra(TITLE, placeTitle);
                intent.putExtra(TYPE, placeValue);
                intent.putExtra(LOCATION, String.valueOf(mLatitude) + "," + String.valueOf(mLongitude));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if ( Build.VERSION.SDK_INT >= 23) {
//            checkPermission();
//        }
//        else
//            getCurrentLocation();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }

    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }



    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

//                            mLatitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
//                                    mLatitudeLabel,
//                                    mLastLocation.getLatitude()));

                            mLatitude = mLastLocation.getLatitude();
                            mLongitude = mLastLocation.getLongitude();
//                            mLongitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
//                                    mLongitudeLabel,
//                                    mLastLocation.getLongitude()));
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.linear_layout_places);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
//    public void getLocation(View view)
//    {
//        String strLocation = "Latitude: " + String.valueOf(mLatitude) + " Longitude: " + mLongitude;
//        textViewLocation.setText(strLocation);
//    }
}
