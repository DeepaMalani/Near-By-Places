package com.example.android.nearbyplaces.widgets;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.example.android.nearbyplaces.ui.ViewNearByFragment;

/**
 * Created by Deep on 9/14/2017.
 */

public class UpdateWidgetService extends IntentService {
    public static final String ACTION_UPDATE_PLACE_WIDGETS = "com.example.android.nearbyplaces.action.update_place_widgets";
    public static final String EXTRA_PLACE_ID = "com.example.android.nearbyplaces.extra.PLACE_ID";
    public static final String EXTRA_PLACE_NAME = "com.example.android.nearbyplaces.extra.PLACE_NAME";
    public static final String EXTRA_PHOTO_REFERENCE = "com.example.android.nearbyplaces.extra.PHOTO_REFERENCE";
    private static Context mContext;
    private static String mPlaceName;
    ;
    private static String mPlaceId;
    ;
    private static String mPhoto_reference;
    ;


    public UpdateWidgetService() {
        super("UpdatePlaceService");
        ;
    }

    /**
     * Starts this service to perform UpdateIngredientWidgets action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdatePlaceWidgets(Context context) {

        mContext = context;
        getPlaceDetails();
        Intent intent = new Intent(context, UpdateWidgetService.class);
        intent.setAction(ACTION_UPDATE_PLACE_WIDGETS);
        intent.putExtra(EXTRA_PLACE_ID, mPlaceId);
        intent.putExtra(EXTRA_PLACE_NAME, mPlaceName);
        intent.putExtra(EXTRA_PHOTO_REFERENCE, mPhoto_reference);
        context.startService(intent);
    }

    private static void getPlaceDetails() {
        SharedPreferences prefs = mContext.getSharedPreferences(ViewNearByFragment.PLACE_ID_PREF_NAME, MODE_PRIVATE);
        mPlaceId = prefs.getString("placeId", "");
        mPlaceName = prefs.getString("placeName", "");
        mPhoto_reference = prefs.getString("photo_reference", "");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_PLACE_WIDGETS.equals(action)) {
                final String placeId = intent.getStringExtra(EXTRA_PLACE_ID);
                final String placeName = intent.getStringExtra(EXTRA_PLACE_NAME);
                final String photo_reference = intent.getStringExtra(EXTRA_PHOTO_REFERENCE);
                handleActionUpdatePlaceWidgets(placeId, placeName, photo_reference);
            }
        }
    }

    /**
     * Handle action UpdateIngredientWidgets in the provided background thread
     */
    private void handleActionUpdatePlaceWidgets(String placeId, String placeName, String photo_reference) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PlaceWidget.class));
        //Get last place details using shared preference

        //Now update all widgets
        PlaceWidget.updatePlaceWidgets(this, appWidgetManager, placeId, placeName, photo_reference, appWidgetIds);
    }
}
