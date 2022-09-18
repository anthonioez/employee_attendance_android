package com.hmkm1c.attendance;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs
{
    private static final String DEF_URL         = "";
    private static final String DEF_TOKEN       = "";

    private static final String KEY_URL         = "url";
    private static final String KEY_TOKEN       = "token";
    private static final String KEY_FLASH       = "flash";
    private static final String KEY_SOUND       = "sound";

    public static void setString(Context context, String mKey, String mValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mKey, mValue);
        editor.commit();
    }

    public static String getString(Context context, String mKey, String mDefValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(mKey, mDefValue);
    }

    public static void setBoolean(Context context, String mKey, boolean mValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(mKey, mValue);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String mKey, boolean mDefValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(mKey, mDefValue);
    }

    public static void setInt(Context context, String mKey, int mValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mKey, mValue);
        editor.commit();
    }

    public static int getInt(Context context, String mKey, int mDefValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(mKey, mDefValue);
    }

    public static void Dummy()
    {

    }

    public static void setUrl(Context ctx, String code)
    {
        setString(ctx, KEY_URL, code);
    }
    public static String getUrl(Context ctx)
    {
        return getString(ctx, KEY_URL, DEF_URL);
    }

    public static void setToken(Context ctx, String code)
    {
        setString(ctx, KEY_TOKEN, code);
    }
    public static String getToken(Context ctx)
    {
        return getString(ctx, KEY_TOKEN, DEF_TOKEN);
    }


    public static void setSound(Context ctx, boolean sound)
    {
        setBoolean(ctx, KEY_SOUND, sound);
    }
    public static boolean getSound(Context ctx)
    {
        return getBoolean(ctx, KEY_SOUND, true);
    }

    public static void setFlash(Context ctx, boolean flash)
    {
        setBoolean(ctx, KEY_FLASH, flash);
    }
    public static boolean getFlash(Context ctx)
    {
        return getBoolean(ctx, KEY_FLASH, false);
    }
}
