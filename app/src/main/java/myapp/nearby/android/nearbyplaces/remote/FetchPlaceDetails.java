package myapp.nearby.android.nearbyplaces.remote;

import android.content.Context;
import android.os.AsyncTask;

import java.net.URL;

import myapp.nearby.android.nearbyplaces.utilities.NetworkUtils;
import myapp.nearby.android.nearbyplaces.utilities.OpenPlaceDetailsJsonUtils;

/**
 * Created by Deep on 9/5/2017.
 */

public class FetchPlaceDetails extends AsyncTask<Void, Void, Void> {


    private static final String TAG = FetchPlaceDetails.class.getSimpleName();
    private Context mContext;
    private String mPlaceId;

    public FetchPlaceDetails(Context context, String placeId) {
        mContext = context;
        mPlaceId = placeId;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        URL placeDetailRequestUrl = NetworkUtils.buildPlaceDetailUrl(mContext, mPlaceId);
        try {
            String jsonPlaceDetailResponse = NetworkUtils
                    .getResponseFromHttpUrl(placeDetailRequestUrl);

            OpenPlaceDetailsJsonUtils.getPlaceDetailDataFromJson(jsonPlaceDetailResponse, mContext, mPlaceId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


}
