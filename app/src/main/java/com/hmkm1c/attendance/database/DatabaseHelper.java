package com.hmkm1c.attendance.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hmkm1c.attendance.R;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String TAG = DatabaseHelper.class.getSimpleName();
    public static final int DATABASE_VERSION = 4;

    protected Context context;

    public DatabaseHelper(Context context)
    {
        super(context, "attendance.db", null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String s;

        try
        {
            InputStream in = context.getResources().openRawResource(R.raw.sql);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(in, null);

            NodeList statements = doc.getElementsByTagName("statement");
            for (int i = 0; i < statements.getLength(); i++) {
                s = statements.item(i).getChildNodes().item(0).getNodeValue();
                //Logger.i(TAG, "SQL: " + s);

                db.execSQL(s);
            }
        }
        catch (Exception e)
        {
            Log.i(TAG, "dbHelper Error: " + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Logger.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

        onCreate(db);
    }

}
