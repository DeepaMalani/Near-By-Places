package com.example.android.nearbyplaces.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.PlacesTypesSource;
import com.example.android.nearbyplaces.R;
import com.example.android.nearbyplaces.adapter.PlacesTypeAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 111;
    public static String TITLE = "activity_title";
    public static String TYPE = "type";
    public static String LOCATION = "location";
    String[] mPlaceTitles;
    String[] mPlaceValues;
    TypedArray mGridViewIcons;
    @BindView(R.id.recycler_view_places)
    RecyclerView mRecyclerView;
    @BindView(R.id.adView)
    AdView mAdView;
    private PlacesTypeAdapter mPlacesTypeAdapter;
    private FusedLocationProviderClient mFusedLocationClient;
    private double mLatitude;
    private double mLongitude;
    private String strLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        //Get the place type values and titles using java library.
        PlacesTypesSource placesTypesSource = new PlacesTypesSource();
        mPlaceTitles = placesTypesSource.getPlaceTitles();
        mPlaceValues = placesTypesSource.getPlaceValues();

        mGridViewIcons = this.getResources().obtainTypedArray(R.array.places_icon_array);


        boolean isPhone = getResources().getBoolean(R.bool.is_phone);
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
                //  Toast.makeText(MainActivity.this, String.valueOf(mLatitude),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if ( Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }
        else
            getCurrentLocation();

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

    private void getCurrentLocation() {

        try {
            //Create instance for Fused Location Provider Client
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // ...
                                mLatitude = location.getLatitude();
                                mLongitude = location.getLongitude();
                                strLocation = String.valueOf(mLatitude) + "," + String.valueOf(mLongitude);
                            } else {
                                Log.e(TAG, "Not able to get user's current location .");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error trying to get last  location");
                            e.printStackTrace();
                        }
                    });
            ;
        } catch (SecurityException ex) {
            Log.e(TAG, "Error trying to get last  location");

        }

    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    getCurrentLocation();

                } else {

                    Log.e(TAG, "Permission denied");
                }
                return;
            }


        }
    }
}
