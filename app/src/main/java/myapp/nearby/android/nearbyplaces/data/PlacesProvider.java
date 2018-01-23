package myapp.nearby.android.nearbyplaces.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class PlacesProvider extends ContentProvider {

    // Use an int for each URI we will run, this represents the different queries

    static final int PLACE = 100;
    static final int PLACE_ID = 101;
    static final int PLACE_DETAILS = 200;
    static final int PLACE_DETAILS_ID = 201;
    static final int REVIEW = 300;
    static final int PLACE_WITH_REVIEW = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PlacesDatabase mOpenHelper;


    private static final SQLiteQueryBuilder sPLACEDETAILSWITHREWIESQueryBuilder;

    static{
        sPLACEDETAILSWITHREWIESQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sPLACEDETAILSWITHREWIESQueryBuilder.setTables(
                PlacesContract.PlaceReviewsEntry.TABLE_NAME + " INNER JOIN " +
                        PlacesContract.PlaceDetailEntry.TABLE_NAME +
                        " ON " + PlacesContract.PlaceReviewsEntry.TABLE_NAME +
                        "." + PlacesContract.PlaceReviewsEntry.COLUMN_PLACE_DETAILS_KEY +
                        " = " + PlacesContract.PlaceDetailEntry.TABLE_NAME +
                        "." + PlacesContract.PlaceDetailEntry._ID);
    }

    //place_details.place_id = ?
    private static final String sPlaceIdSelection =
            PlacesContract.PlaceDetailEntry.TABLE_NAME+
                    "." + PlacesContract.PlaceDetailEntry.COLUMN_PLACE_ID + " = ? ";


    private Cursor getReviewsByPlaceId(Uri uri, String[] projection, String sortOrder,String selection,String[] selectionArgs) {

        String placeId = PlacesContract.PlaceReviewsEntry.getPlaceIdFromUri(uri);

        return sPLACEDETAILSWITHREWIESQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                new String[]{placeId},
                null,
                null,
                sortOrder
        );
    }

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
        matcher.addURI(content, PlacesContract.PATH_PLACE_REVIEWS , REVIEW);
        matcher.addURI(content, PlacesContract.PATH_PLACE_REVIEWS + "/*", PLACE_WITH_REVIEW);


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
            case PLACE_WITH_REVIEW:
               return  PlacesContract.PlaceReviewsEntry.CONTENT_TYPE;
            case REVIEW:
                return  PlacesContract.PlaceReviewsEntry.CONTENT_TYPE;


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

            case REVIEW:
                retCursor = db.query(
                        PlacesContract.PlaceReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PLACE_WITH_REVIEW:
               // long _idPlaceDetail = ContentUris.parseId(uri);
                retCursor = getReviewsByPlaceId(uri,projection,sortOrder,sPlaceIdSelection,new String[]{String.valueOf(selectionArgs)});
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

        Uri returnUri;

        switch (sUriMatcher.match(uri)) {

            case PLACE: {
                long _id = db.insert(PlacesContract.PlaceEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PlacesContract.PlaceEntry.buildPlacesUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            }
            case PLACE_DETAILS: {
                long _id = db.insert(PlacesContract.PlaceDetailEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PlacesContract.PlaceDetailEntry.buildPlaceDetailsUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            }
            case  REVIEW: {
                long _id = db.insert(PlacesContract.PlaceReviewsEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = PlacesContract.PlaceReviewsEntry.buildPlaceReviewsUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            }
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
            case  REVIEW:
                rows = db.delete(PlacesContract.PlaceReviewsEntry.TABLE_NAME,selection,selectionArgs);
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
            case  REVIEW:
                rows = db.update(PlacesContract.PlaceReviewsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REVIEW:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(PlacesContract.PlaceReviewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
