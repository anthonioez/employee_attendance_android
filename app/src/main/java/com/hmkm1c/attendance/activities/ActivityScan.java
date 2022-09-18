package com.hmkm1c.attendance.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.hmkm1c.attendance.Attendance;
import com.hmkm1c.attendance.Prefs;
import com.hmkm1c.attendance.R;
import com.hmkm1c.attendance.Utils;
import com.hmkm1c.attendance.database.AttendItem;
import com.hmkm1c.attendance.tasks.Uploader;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ActivityScan extends AppCompatActivity
{
    private static final String TAG = ActivityScan.class.getSimpleName();

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;

    private String lastScan;
    private String type;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        type = getIntent().getStringExtra("type");

        setContentView(R.layout.activity_scan);

        beepManager = new BeepManager(this);

        barcodeView = findViewById(R.id.barcode_scanner);
        barcodeView.setStatusText("");

        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);

        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats, null, null));
        barcodeView.initializeFromIntent(getIntent());

        if(Prefs.getFlash(this))
        {
            barcodeView.setTorchOn();
        }

        barcodeView.decodeContinuous(callback);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        barcodeView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private BarcodeCallback callback = new BarcodeCallback()
    {
        @Override
        public void barcodeResult(BarcodeResult result)
        {
            String code = result.getText();

            if (code == null || code.equals(lastScan))
            {
                // Prevent duplicate scans
                return;
            }

            lastScan = code;

            if(Prefs.getSound(ActivityScan.this))
            {
                beepManager.playBeepSoundAndVibrate();
            }

            try
            {
                Date now = new Date();

                JSONObject json = new JSONObject(lastScan);

                AttendItem item = new AttendItem();
                item.name   = json.getString("name");
                item.type   = type;
                item.addr   = Attendance.locationAddress;
                item.stamp  = now.getTime();

                item.user   = Attendance.currentUser;
                item.start  = Attendance.currentTime;

                if(item.insert() > 0)
                {
                    //Utils.toast(ActivityScan.this, "Attendance recorded!");

                    Attendance.pending++;
                    Attendance.newUpload(ActivityScan.this, false);
                }
                else
                {
                    Utils.toast(ActivityScan.this, "Unable to record attendance!");
                }

                //lastScan = "";
            }
            catch (JSONException e)
            {
                error("Invalid QR data");
            }
            catch (Exception e)
            {
                error("An error occurred!");

            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints)
        {
        }
    };

    private void error(String msg)
    {
        Utils.toast(ActivityScan.this, msg);

        lastScan = "";
    }
}
