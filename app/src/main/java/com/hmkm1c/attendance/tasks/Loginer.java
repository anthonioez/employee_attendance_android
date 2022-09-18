package com.hmkm1c.attendance.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.hmkm1c.attendance.Prefs;
import com.hmkm1c.attendance.http.HttpResponse;
import com.hmkm1c.attendance.http.HttpUtils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Loginer extends AsyncTask<Void, String, Boolean>
{
    public final String TAG = Loginer.class.getSimpleName();

    private Context mContext;

    private String mMsg;
    private String mError;
    private String mEmail;
    private String mPassword;

    private LoginListener mListener;
    private ProgressDialog mDlg;

    public Loginer(Context c, String email, String password, LoginListener listener)
    {
        mContext = c;
        mListener = listener;

        mEmail = email;
        mPassword = password;
    }

    protected void onProgressUpdate(String... msg)
    {

    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        mDlg = new ProgressDialog(mContext);
        mDlg.setCancelable(true);
        mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDlg.setTitle("Logging in");
        mDlg.setMessage("Please wait...");
        mDlg.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            public void onCancel(DialogInterface d)
            {
                if (null != mListener)
                {
                    mListener.onLoginError("");
                }
            }
        });
        mDlg.show();

    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        if (mDlg != null)
        {
            mDlg.dismiss();
            mDlg = null;
        }

        if (null != mListener)
        {
            if(result != null && result.booleanValue())
            {
                mListener.onLoginSuccess(mMsg);
            }
            else
            {
                mListener.onLoginError(mError);
            }
        }
    }

    @Override
    protected Boolean doInBackground(Void... bparams)
    {
        boolean ret = false;

        mMsg = null;
        mError = null;

        String url = Prefs.getUrl(mContext);
        if (TextUtils.isEmpty(url))
        {
            mError = "No URL setting!";
            return false;
        }

        url += "/main/auth";

        HashMap<String, String> params = new HashMap<>();
        params.put("key",       Prefs.getToken(mContext));
        params.put("email",     mEmail);
        params.put("password",  mPassword);
        params.put("stamp",     new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date()).toUpperCase() );

        HttpResponse response = HttpUtils.post(url, params);
        if (response != null && response.isOK())
        {
            try
            {
                String data = new String(response.data);
                JSONObject json = new JSONObject(new JSONTokener(data));
                if (json.has("status"))
                {
                    ret = json.getBoolean("status");
                    if(ret)
                        mMsg = json.optString("message");
                    else
                        mError = json.optString("message");
                }
                else
                {
                    mError = "Incorrect response from server!";
                }
            }
            catch (Exception e)
            {
                mError = "Incorrect data from server!";
            }
        }
        else
        {
            mError = "Unable to receive correct response from the server!";
        }

        return ret;
    }

    public interface LoginListener
    {
        void onLoginSuccess(String address);
        void onLoginError(String address);
    }
}