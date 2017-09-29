package com.example.android.nearbyplaces.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PlacesDatabase extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "nearby_places.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public PlacesDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a places table.
        final String SQL_CREATE_PLACE_TABLE = "CREATE TABLE " + PlacesContract.PlaceEntry.TABLE_NAME + " (" +
                PlacesContract.PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PlacesContract.PlaceEntry.COLUMN_PLACE_ID + " INTEGER UNIQUE NOT NULL, " +
                PlacesContract.PlaceEntry.COLUMN_PLACE_NAME + " TEXT NOT NULL, " +
                PlacesContract.PlaceEntry.COLUMN_PLACE_TYPE + " TEXT NOT NULL, " +
                PlacesContract.PlaceEntry.COLUMN_ADDRESS + " TEXT NOT NULL, " +
                PlacesContract.PlaceEntry.COLUMN_RATING + " REAL NOT NULL, " +
                PlacesContract.PlaceEntry.COLUMN_PHOTO_REFERENCE + " TEXT NOT NULL, " +


                // To assure the application have just one place entry
                // per place id, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + PlacesContract.PlaceEntry.COLUMN_PLACE_ID + ") ON CONFLICT REPLACE);";

        // Create a place details table.
        final String SQL_CREATE_PLACE_DETAILS_TABLE = "CREATE TABLE " + PlacesContract.PlaceDetailEntry.TABLE_NAME + " (" +
                PlacesContract.PlaceDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PlacesContract.PlaceDetailEntry.COLUMN_PLACE_ID + " INTEGER UNIQUE NOT NULL, " +
                PlacesContract.PlaceDetailEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
                PlacesContract.PlaceDetailEntry.COLUMN_OPENING_HOURS + " TEXT NOT NULL, " +
                PlacesContract.PlaceDetailEntry.COLUMN_WEBSITE + " TEXT NOT NULL, " +
                PlacesContract.PlaceDetailEntry.COLUMN_URL + " TEXT NOT NULL, " +

                // To assure the application have just one movie entry
                // per place id, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + PlacesContract.PlaceDetailEntry.COLUMN_PLACE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_PLACE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PLACE_DETAILS_TABLE);
    }

    //This will calls only when database version change.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlacesContract.PlaceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlacesContract.PlaceDetailEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

}
