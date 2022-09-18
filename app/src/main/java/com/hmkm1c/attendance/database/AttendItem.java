package com.hmkm1c.attendance.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.hmkm1c.attendance.Attendance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AttendItem
{
    public static final String TAG = AttendItem.class.getSimpleName();

    public static final String DB_TABLE = "attendance";

    public static final String KEY_ID       = "_id";

    public static final String KEY_NAME     = "name";
    public static final String KEY_TYPE     = "type";
    public static final String KEY_ADDR     = "addr";
    public static final String KEY_STAMP    = "stamp";

    public static final String KEY_USER     = "user";
    public static final String KEY_START    = "start";

    public long     id;
    public String   name;
    public String   type;
    public String   addr;
    public long     stamp;

    public String   user;
    public String   start;

    public AttendItem()
    {
        id = 0;

        name = "";
        type = "";
        addr = "";

        stamp = 0;

        user = "";
        start = "";
    }

    public boolean copy(Cursor cursor)
    {
        try
        {
            id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
            name    = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            type    = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
            addr    = cursor.getString(cursor.getColumnIndex(KEY_ADDR));
            stamp   = cursor.getLong(cursor.getColumnIndex(KEY_STAMP));

            user    = cursor.getString(cursor.getColumnIndex(KEY_USER));
            start   = cursor.getString(cursor.getColumnIndex(KEY_START));
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public ContentValues getValues()
    {
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, name);
        values.put(KEY_TYPE, type);
        values.put(KEY_ADDR, addr);
        values.put(KEY_STAMP, stamp);

        values.put(KEY_USER, user);
        values.put(KEY_START, start);
        return values;
    }

    public boolean get(long id)
    {
        boolean ret = false;
        Cursor mCursor = null;
        try
        {
            String where = "(" + KEY_ID + "=" + id + " )";
            mCursor = Attendance.appDb.query(true, DB_TABLE, null, where, null, null, null, null, null);
            if (mCursor != null)
            {
                if (mCursor.moveToFirst())
                {
                    ret = copy(mCursor);
                }
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            if (mCursor != null)
            {
                mCursor.close();
            }
        }
        return ret;
    }


    public static Cursor loadByType(String typ)
    {
        return Attendance.appDb.query(DB_TABLE, null, KEY_TYPE + "= '" + typ + "'", null, null, null, "stamp desc");
    }

    public static Cursor load()
    {
        return Attendance.appDb.query(DB_TABLE, null, null, null, null, null, "_id desc");
    }

    public long insert()
    {
        return Attendance.appDb.insert(DB_TABLE, null, getValues());
    }

    public boolean update()
    {
        return Attendance.appDb.update(DB_TABLE, getValues(), KEY_ID + "=" + id, null) > 0;
    }

    public boolean delete()
    {
        return Attendance.appDb.delete(DB_TABLE, KEY_ID + "=" + id, null) > 0;
    }

    public static boolean deleteByType(String typ)
    {
        return Attendance.appDb.delete(DB_TABLE, KEY_TYPE + "= '" + typ + "'", null) > 0;
    }

    public static int getCount()
    {
        int count = 0;
        Cursor mCursor = null;
        try
        {
            String where = "1";
            mCursor = Attendance.appDb.query(true, DB_TABLE, null, where, null, null, null, null, null);
            if (mCursor != null)
            {
                count = mCursor.getCount();
            }
        }
        catch (Exception e)
        {
            Log.i(TAG, e.toString());
        }
        finally
        {
            if (mCursor != null)
            {
                mCursor.close();
            }
        }
        return count;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static List<AttendItem> list(Cursor cursor)
    {
        List<AttendItem> list = null;
        if(cursor != null)
        {
            list = new ArrayList<>();
            while (cursor.moveToNext())
            {
                AttendItem item = new AttendItem();
                if(item.copy(cursor))
                {
                    list.add(item);
                }
            }
            cursor.close();
        }
        return list;
    }
}
