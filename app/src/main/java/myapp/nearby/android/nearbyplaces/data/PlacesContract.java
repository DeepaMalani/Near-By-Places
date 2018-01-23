package myapp.nearby.android.nearbyplaces.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public final class PlacesContract {

    public static final String CONTENT_AUTHORITY = "myapp.nearby.android.nearbyplaces";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.nearbyplaces.app/places/ is a valid path for

    public static final String PATH_PLACES = "places";
    public static final String PATH_PLACE_DETAILS = "placedetails";
    public static final String PATH_PLACE_REVIEWS = "reviews";


    public final static class PlaceEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;
        public static final String TABLE_NAME = "places";
        public static final String COLUMN_PLACE_ID = "place_id";
        public static final String COLUMN_PLACE_NAME = "place_name";
        public static final String COLUMN_PLACE_TYPE = "place_type";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_PHOTO_REFERENCE = "photo_reference";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_CURRENT_LAT = "current_latitude";
        public static final String COLUMN_CURRENT_LONG = "current_longitude";
        public static final String COLUMN_CURRENT_DATE = "current_date";


        // Define a function to build a URI to find a specific movie by it's identifier
        public static Uri buildPlacesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

    public final static class PlaceDetailEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE_DETAILS).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE_DETAILS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE_DETAILS;
        public static final String TABLE_NAME = "place_details";
        //Place id for place
        public static final String COLUMN_PLACE_ID = "place_id";
        //Phone number for place returned by API
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
        //Opening hours for place returned by API
        public static final String COLUMN_OPENING_HOURS = "opening_hours";
        //Website for place returned by API
        public static final String COLUMN_WEBSITE = "website";
        //Map url for place
        public static final String COLUMN_URL = "url";

        // Define a function to build a URI to find a specific movie by it's identifier
        public static Uri buildPlaceDetailsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
    public final static class PlaceReviewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE_REVIEWS).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACE_REVIEWS;

        public static final String TABLE_NAME = "place_reviews";
        // // Column with the foreign key into the place_details table.
        public static final String COLUMN_PLACE_DETAILS_KEY = "place_details_id";
        //Author name for reviews
        public static final String COLUMN_AUTHOR_NAME = "author_name";
        //Place ratings
        public static final String COLUMN_RATINGS = "ratings";
        //Place review time description
        public static final String COLUMN_REVIEW_TIME = "review_time";
        //Place review  description
        public static final String COLUMN_REVIEW_DESCRIPTION = "review_description";

        // Define a function to build a URI to find a specific place review by it's identifier
        public static Uri buildPlaceReviewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPlaceDetailsWithReviews(String Id) {
            return CONTENT_URI.buildUpon().appendPath(Id).build();
        }
        public static String getPlaceIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}

