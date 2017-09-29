package com.example.android.nearbyplaces.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.nearbyplaces.R;
import com.example.android.nearbyplaces.ui.PlaceDetailsActivity;
import com.example.android.nearbyplaces.ui.ViewNearByFragment;

/**
 * Implementation of App Widget functionality.
 */
public class PlaceWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                String placeId, String placeName, String photo_reference, int appWidgetId) {

        String strWidgetText;

        //if(placeName==null)
        if (placeName.equals(""))
            strWidgetText = context.getString(R.string.empty_widget_text);
        else
            strWidgetText = placeName;

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.place_widget);
        views.setTextViewText(R.id.appwidget_text, strWidgetText);
        views.setContentDescription(R.id.appwidget_text, strWidgetText);


        //Create an Intent to launch place details activity when clicked
        Intent intent = new Intent(context, PlaceDetailsActivity.class);
        intent.putExtra(ViewNearByFragment.PLACE_ID, placeId);
        intent.putExtra(ViewNearByFragment.PLACE_NAME, placeName);
        intent.putExtra(ViewNearByFragment.PHOTO_REFERENCE, photo_reference);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //widget click
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    /**
     * Updates all widget instances given the widget Ids and display information
     *
     * @param context          The calling context
     * @param appWidgetManager The widget manager
     * @param appWidgetIds     Array of widget Ids to be updated
     */
    public static void updatePlaceWidgets(Context context, AppWidgetManager appWidgetManager,
                                          String placeId, String placeName, String photo_reference, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, placeId, placeName, photo_reference, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        UpdateWidgetService.startActionUpdatePlaceWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

