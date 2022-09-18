package com.hmkm1c.attendance.tasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Addresser extends AsyncTask<Void, String, Boolean>
{
    public final String TAG = Addresser.class.getSimpleName();

    private Context mContext;

    private String mMsg;
    private String mAddress = "";
    private double mLatitude;
    private double mLongitude;
    private AddresserListener mListener;

    public Addresser(Context c, double latitude, double longitude, AddresserListener listener)
    {
        mContext = c;
        mLatitude = latitude;
        mLongitude = longitude;
        mListener = listener;
    }

    protected void onProgressUpdate(String... msg)
    {

    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        if (null != mListener)
        {
            mListener.onAddressDone(mAddress);
        }
    }

    @Override
    protected Boolean doInBackground(Void... bparams)
    {
        boolean ret = false;

        mMsg = null;

        try
        {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);

            if(addresses != null && addresses.size() > 0)
            {
                String line = addresses.get(0).getAddressLine(0);
                mAddress = line + ".";
                return true;
            }
        }
        catch (IOException localIOException)
        {
            localIOException.printStackTrace();
        }

        mAddress = String.format("%.4f, %.4f", mLatitude, mLongitude);

        return ret;
    }

    public interface AddresserListener
    {
        void onAddressDone(String address);
    }
}