package com.example.infinity.pixie;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler {

    //public static final String KEY_ROWID = "_id";
    public static final String KEY_IMG = "img";
    public static final String KEY_USEREMAIL = "email";
    public static final String KEY_USERNUMBER = "number";
    public static final String KEY_USERURL = "url";
    public static final String KEY_EVENTDATE = "eDate";
    public static final String KEY_EVENTSTARTTIME = "eSTime";
    public static final String KEY_EVENTENDTIME = "eETime";
    public static final String KEY_TEXT = "text";
    private static final String TAG = "DBAdapter";
    //private static final String TAG = DBAdapter.class.getSimpleName();

    private static final String DATABASE_NAME = "pixie";
    private static final String DATABASE_TABLE = "extractedData";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table extractedData (img varchar primary key,"+"email varchar ,"+ "number varchar,"+
                    "url varchar,"+"eDate varchar,"+"eSTime varchar,"+"eETime varchar,"+"text varchar);";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DatabaseHandler(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS extractedData");
            onCreate(db);
        }
    }

    //---opens the database---
    public DatabaseHandler open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a title into the database---
    public boolean insertTitle(String img, String email,String number, String url, String eDate, String eSTime,String eETime, String text)
    {
        db = DBHelper.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_IMG, img);
        initialValues.put(KEY_USEREMAIL, email);
        initialValues.put(KEY_USERNUMBER, number);
        initialValues.put(KEY_USERURL, url);
        initialValues.put(KEY_EVENTDATE, eDate);
        initialValues.put(KEY_EVENTSTARTTIME, eSTime);
        initialValues.put(KEY_EVENTENDTIME, eETime);
        initialValues.put(KEY_TEXT, text);
        db.insert(DATABASE_TABLE, null, initialValues);
        return true;
    }

    //---deletes a particular title---
    public boolean deleteTitle(String img)
    {
        return db.delete(DATABASE_TABLE, KEY_IMG +
                "=" + img, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllTitles()
    {
        return db.query( DATABASE_TABLE, new String[] {

                    KEY_USEREMAIL,
                    KEY_USERNUMBER,
                    KEY_USERURL,
                    KEY_EVENTDATE,
                    KEY_EVENTSTARTTIME,
                    KEY_EVENTENDTIME

                },
                null,
                null,
                null, null, null);
    }


    //---retrieves a particular title---
    public Cursor getTitle(String img) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {
                                KEY_USEREMAIL,
                                KEY_USERNUMBER,
                                KEY_USERURL,
                                KEY_EVENTDATE,
                                KEY_EVENTSTARTTIME,
                                KEY_EVENTENDTIME

                        },
                        KEY_IMG + "=" + img,
                        null,
                        null,
                        null, null, null
                );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a title---
    public boolean updateTitle(String img, String email,
                               String number)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_USEREMAIL, email);
        args.put(KEY_USERNUMBER, number);
        return db.update(DATABASE_TABLE, args,
                KEY_IMG + "=" + img , null) > 0;
    }


}
