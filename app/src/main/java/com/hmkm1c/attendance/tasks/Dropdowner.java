package com.hmkm1c.attendance.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.hmkm1c.attendance.Prefs;
import com.hmkm1c.attendance.http.HttpResponse;
import com.hmkm1c.attendance.http.HttpUtils;
import com.hmkm1c.attendance.objects.UserItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Dropdowner extends AsyncTask<Void, String, Boolean>
{
    public final String TAG = Dropdowner.class.getSimpleName();

    private Context mContext;

    private String mError;

    private DropdownListener mListener;
    private ProgressDialog mDlg;
    private List<UserItem> users;
    private List<String> times;

    public Dropdowner(Context c, DropdownListener listener)
    {
        mContext = c;
        mListener = listener;

        users = new ArrayList<>();
        times = new ArrayList<>();
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
        mDlg.setMessage("Please wait...");
        mDlg.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            public void onCancel(DialogInterface d)
            {
                if (null != mListener)
                {
                    mListener.onDropdownError("");
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
                mListener.onDropdownSuccess(users, times);
            }
            else
            {
                mListener.onDropdownError(mError);
            }
        }
    }

    @Override
    protected Boolean doInBackground(Void... bparams)
    {
        boolean ret = false;

        mError = null;

        String url = Prefs.getUrl(mContext);
        if (TextUtils.isEmpty(url))
        {
            mError = "No URL setting!";
            return false;
        }

        url += "/main/dropdowns";

        HashMap<String, String> params = new HashMap<>();
        params.put("key",       Prefs.getToken(mContext));

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
                    {
                        JSONArray jsonUsers = json.getJSONArray("users");
                        if(jsonUsers != null && jsonUsers.length() > 0)
                        {
                            for(int i = 0; i < jsonUsers.length(); i++)
                            {
                                JSONObject jsonUser = jsonUsers.getJSONObject(i);
                                UserItem user = UserItem.copy(jsonUser);
                                if(user != null)
                                {
                                    users.add(user);
                                }
                            }
                        }

                        JSONArray jsonTimes = json.getJSONArray("times");
                        if(jsonTimes != null && jsonTimes.length() > 0)
                        {
                            for(int i = 0; i < jsonTimes.length(); i++)
                            {
                                String time = jsonTimes.getString(i);
                                if(time != null)
                                {
                                    times.add(time);
                                }
                            }
                        }
                    }
                    else
                    {
                        mError = json.optString("message");
                    }
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

    public interface DropdownListener
    {
        void onDropdownSuccess(List<UserItem> users, List<String> times);
        void onDropdownError(String address);
    }
}