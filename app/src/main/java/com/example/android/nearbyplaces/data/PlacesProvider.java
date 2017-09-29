package com.example.android.nearbyplaces.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class PlacesProvider extends ContentProvider {

    // Use an int for each URI we will run, this represents the different queries

    static final int PLACE = 100;
    static final int PLACE_ID = 101;
    static final int PLACE_DETAILS = 200;
    static final int PLACE_DETAILS_ID = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PlacesDatabase mOpenHelper;

    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    public static UriMatcher buildUriMatcher() {
        String content = PlacesContract.CONTENT_AUTHORITY;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, PlacesContract.PATH_PLACES, PLACE);
        matcher.addURI(content, PlacesContract.PATH_PLACES + "/#", PLACE_ID);
        matcher.addURI(content, PlacesContract.PATH_PLACE_DETAILS, PLACE_DETAILS);
        matcher.addURI(content, PlacesContract.PATH_PLACE_DETAILS + "/#", PLACE_DETAILS_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PlacesDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PLACE:
                return PlacesContract.PlaceEntry.CONTENT_TYPE;
            case PLACE_ID:
                return PlacesContract.PlaceEntry.CONTENT_ITEM_TYPE;
            case PLACE_DETAILS:
                return PlacesContract.PlaceDetailEntry.CONTENT_TYPE;
            case PLACE_DETAILS_ID:
                return PlacesContract.PlaceDetailEntry.CONTENT_ITEM_TYPE;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case PLACE:
                retCursor = db.query(
                        PlacesContract.PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                int count = retCursor.getCount();

                break;
            case PLACE_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        PlacesContract.PlaceEntry.TABLE_NAME,
                        projection,
                        PlacesContract.PlaceEntry.COLUMN_PLACE_TYPE + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case PLACE_DETAILS:
                retCursor = db.query(
                        PlacesContract.PlaceDetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                int countFavorites = retCursor.getCount();

                break;
            case PLACE_DETAILS_ID:
                long _idPlaceDetails = ContentUris.parseId(uri);
                retCursor = db.query(
                        PlacesContract.PlaceDetailEntry.TABLE_NAME,
                        projection,
                        PlacesContract.PlaceDetailEntry.COLUMN_PLACE_ID + " = ?",
                        new String[]{String.valueOf(_idPlaceDetails)},
                        null,
                        null,
                        sortOrder
                );

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {

            case PLACE:
                _id = db.insert(PlacesContract.PlaceEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PlacesContract.PlaceEntry.buildPlacesUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case PLACE_DETAILS:
                _id = db.insert(PlacesContract.PlaceDetailEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PlacesContract.PlaceDetailEntry.buildPlaceDetailsUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows; // Number of rows effected

        switch (sUriMatcher.match(uri)) {
            case PLACE:
                rows = db.delete(PlacesContract.PlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLACE_DETAILS:
                rows = db.delete(PlacesContract.PlaceDetailEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because null could delete all rows:
        if (selection == null || rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch (sUriMatcher.match(uri)) {

            case PLACE:
                rows = db.update(PlacesContract.PlaceEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PLACE_DETAILS:
                rows = db.update(PlacesContract.PlaceDetailEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }
}
