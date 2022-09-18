package com.hmkm1c.attendance.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.hmkm1c.attendance.Attendance;
import com.hmkm1c.attendance.Prefs;
import com.hmkm1c.attendance.database.AttendItem;
import com.hmkm1c.attendance.http.HttpResponse;
import com.hmkm1c.attendance.http.HttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Uploader extends AsyncTask<Void, String, Boolean>
{
    public final String TAG = Uploader.class.getSimpleName();

    private Context mContext;

    private int mCount;
    private String mMsg;
    private String mError;
    private boolean mShow;
    private UploaderListener mListener;
    private ProgressDialog mDlg;

    public Uploader(Context c, boolean show, UploaderListener listener)
    {
        mContext = c;
        mCount = 0;
        mShow = show;
        mListener = listener;
    }

    protected void onProgressUpdate(String... msg)
    {

    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        if (mShow)
        {
            mDlg = new ProgressDialog(mContext);
            mDlg.setCancelable(true);
            mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDlg.setTitle("Uploading attendance");
            mDlg.setMessage("Please wait...");
            mDlg.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                public void onCancel(DialogInterface d)
                {
                    if (null != mListener)
                    {
                        mListener.onUploadError("");
                    }
                }
            });
            mDlg.show();
        }
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        if (mDlg != null)
        {
            mDlg.dismiss();
            mDlg = null;
        }

        if (result != null && result.booleanValue())
        {
            if (null != mListener)
            {
                mListener.onUploadDone(mCount);
            }
        }
        else
        {
            if (mError == null)
            {
                mError = "An error occurred!";
            }

            if (null != mListener)
            {
                mListener.onUploadError("Upload error: " + mError);
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

        url += "/main/attendance";

        Attendance.pending++;

        while(Attendance.pending > 0)
        {
            List<AttendItem> list = AttendItem.list(AttendItem.load());
            if (list != null)
            {
                ret = true;
                for (int i = 0; i < list.size(); i++)
                {
                    AttendItem item = list.get(i);
                    if (uploadItem(url, item))
                    {
                        mCount++;
                        EventBus.getDefault().post(item);
                        item.delete();
                    }
                    else
                    {
                        ret = false;
                    }
                }

                EventBus.getDefault().post(Attendance.EVENT_RELOAD);
            }
            else
            {
                mError = "Unable to load attendance list for upload";
            }

            if(Attendance.pending > 0)
                Attendance.pending--;
        }

        return ret;
    }

    private boolean uploadItem(String url, AttendItem item)
    {
        boolean ret = false;

        Date stamp = new Date(item.stamp);

        HashMap<String, String> params = new HashMap<>();
        params.put("key", Prefs.getToken(mContext));
        params.put("q", item.type);
        params.put("name", item.name);
        params.put("date", new SimpleDateFormat("yyyy-MM-dd").format(stamp));
        params.put(item.type + "_time", new SimpleDateFormat("HH:mm:ss").format(stamp));
        params.put("location", item.addr);

        params.put("user", item.user);
        params.put("start", item.start);

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

    public interface UploaderListener
    {
        void onUploadDone(int count);

        void onUploadError(String err);
    }
}