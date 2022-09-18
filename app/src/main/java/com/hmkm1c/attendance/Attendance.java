package com.hmkm1c.attendance;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.hmkm1c.attendance.database.AttendItem;
import com.hmkm1c.attendance.database.DatabaseHelper;
import com.hmkm1c.attendance.tasks.Addresser;
import com.hmkm1c.attendance.tasks.Uploader;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationManagerProvider;

public class Attendance extends Application implements OnLocationUpdatedListener, Uploader.UploaderListener, Addresser.AddresserListener
{
    public static final String TAG = Attendance.class.getSimpleName();
    public static final String EVENT_UPLOAD = "upload";
    public static final String EVENT_RELOAD = "reload";

    public static SmartLocation smartLocation = null;
    public static LocationManagerProvider provider;
    public static Attendance instance;

    public static String appName;
    public static SQLiteDatabase appDb;

    public static Uploader uploader = null;
    public static Addresser addresser = null;
    public static String locationAddress = "";
    public static int pending = 0;
    public static String currentUser = "";
    public static String currentTime = "";

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "Starting...");

        instance = this;

        provider = new LocationManagerProvider();
        smartLocation = new SmartLocation.Builder(this).logging(true).build();

        appName = getResources().getString(R.string.app_name);

        DatabaseHelper dbh = new DatabaseHelper(this);
        appDb = dbh.getWritableDatabase();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        Log.d(TAG, "Terminating...");

        uploadStop();
        locationStop();
    }

    public static void startLocation()
    {
        if(instance != null)
        {
            instance.locationStart();
        }
    }

    public void locationStart()
    {
        locationStop();

        locationAddress = "";
        smartLocation.location(provider).continuous().start(this);
    }

    public void locationStop()
    {
        if (smartLocation != null)
        {
            smartLocation.location().stop();
        }
    }

    @Override
    public void onLocationUpdated(Location location)
    {
        if(location != null && location.getLatitude() != 0 && location.getLongitude() != 0)
        {
            locationStop();

            if(addresser != null)
            {
                addresser.cancel(true);
                addresser = null;
            }

            addresser = new Addresser(this, location.getLatitude(), location.getLongitude(),this);
            addresser.execute();
        }
    }

    public static void newUpload(Context context, boolean show)
    {
        if(instance != null && instance.uploader == null)
        {
            instance.uploadStart(context, show);
        }
    }

    public static void stopUpload()
    {
        if(instance != null && instance.uploader != null)
        {
            instance.uploadStop();
        }
    }

    public void uploadStop()
    {
        if(uploader != null)
        {
            uploader.cancel(true);
            uploader = null;
        }
    }

    public void uploadStart(Context context, boolean show)
    {
        uploadStop();

        uploader = new Uploader(context, show, this);
        uploader.execute();
    }

    @Override
    public void onUploadDone(int count)
    {
        uploader = null;
        if(count > 0)
        {
            Utils.toast(this, String.format("%s\n%s\n\nUploads completed!", Attendance.currentTime, Attendance.currentUser).trim());
        }
    }

    @Override
    public void onUploadError(String err)
    {
        uploader = null;

        if(!TextUtils.isEmpty(err))
        {
            Utils.toast(this, err);
        }
    }

    @Override
    public void onAddressDone(String address)
    {
        locationAddress = address;
    }
}
