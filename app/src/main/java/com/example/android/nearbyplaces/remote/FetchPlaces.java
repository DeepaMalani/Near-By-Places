package com.example.android.nearbyplaces.remote;

import android.content.Context;
import android.os.AsyncTask;

import com.example.android.nearbyplaces.utilities.NetworkUtils;
import com.example.android.nearbyplaces.utilities.OpenPlacesJsonUtils;

import java.net.URL;

/**
 * Created by Deep on 9/1/2017.
 */

public class FetchPlaces extends AsyncTask<Void, Void, Void> {

    private static final String TAG = FetchPlaces.class.getSimpleName();
    private Context mContext;
    private String mType;
    private String mLocation;

    public FetchPlaces(Context context, String type, String location) {
        mContext = context;
        mType = type;
        mLocation = location;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        URL placesRequestUrl = NetworkUtils.buildUrl(mContext, mType, mLocation);
        try {
            String jsonPlacesResponse = NetworkUtils
                    .getResponseFromHttpUrl(placesRequestUrl);

            OpenPlacesJsonUtils.getPlacesDataFromJson(jsonPlacesResponse, mContext, mType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}

