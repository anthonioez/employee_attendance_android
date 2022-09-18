package com.hmkm1c.attendance.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hmkm1c.attendance.Attendance;
import com.hmkm1c.attendance.R;
import com.hmkm1c.attendance.Utils;
import com.hmkm1c.attendance.adapters.AdapterList;
import com.hmkm1c.attendance.database.AttendItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ActivityList extends AppCompatActivity implements AdapterList.AdapterListListener, View.OnClickListener
{
    private static final int REQUEST_LOCATION = 232;
    private static final int REQUEST_CAMERA = 234;

    private RecyclerView recyclerList;
    private AdapterList adapterList;

    private String title;
    private String type;
    private Button buttonScan;
    private MenuItem menuUpload = null;
    private MenuItem menuClear = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");

        setContentView(R.layout.activity_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(title);

        adapterList = new AdapterList(this, this);

        recyclerList = findViewById(R.id.recyclerList);

        recyclerList.setLayoutManager(new LinearLayoutManager(this));
        recyclerList.setAdapter(adapterList);
        recyclerList.setItemAnimator(new DefaultItemAnimator());

        buttonScan = findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);

        requestLocation();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        reload();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menuUpload = menu.findItem(R.id.action_upload);
        menuClear = menu.findItem(R.id.action_clear);

        updateMenu();

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_clear:
                clear();
                return true;

            case R.id.action_upload:
                upload();
                return true;

            case R.id.action_reload:
                reload();
                return true;

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adp, View view, int position)
    {
//        HomeItem item = (HomeItem) adapterList.getItem(position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object data)
    {
        if (data instanceof String)
        {
            String status = (String)data;
            switch (status)
            {
                case Attendance.EVENT_UPLOAD:
                    upload();
                    break;

                case Attendance.EVENT_RELOAD:
                    reload();
                    break;
            }
        }
        else if(data instanceof AttendItem)
        {
            AttendItem item = (AttendItem)data;

            if(!adapterList.removeById(item.id))
            {
                reload();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startScanNow();
                }
                else
                {
                    Utils.alert(this, "Error", "Camera permission required!");
                }
                break;

            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Attendance.startLocation();
                }
                else
                {
                    Utils.toast(this, "Location permission required!");
                }
                break;
        }
    }


    @Override
    public void onClick(View view)
    {
        if (view == buttonScan)
        {
            requestCamera();
        }
    }

    private void requestLocation()
    {
        final String permission1 = android.Manifest.permission.ACCESS_FINE_LOCATION;
        final String permission2 = android.Manifest.permission.ACCESS_COARSE_LOCATION;

        if (ContextCompat.checkSelfPermission(this, permission1) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission1))
            {
                Utils.alert(this, "Location", "Location permission is required to log attendance!", new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ActivityCompat.requestPermissions(ActivityList.this, new String[]{permission1, permission2}, REQUEST_LOCATION);
                    }
                });
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{permission1, permission2}, REQUEST_LOCATION);
            }
        }
        else
        {
            Attendance.startLocation();
        }
    }

    private void requestCamera()
    {
        final String permission3 = Manifest.permission.CAMERA;

        if (ContextCompat.checkSelfPermission(this, permission3) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission3))
            {
                Utils.alert(this, "Camera", "Camera permission is required for scanning QR codes!", new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ActivityCompat.requestPermissions(ActivityList.this, new String[]{permission3}, REQUEST_CAMERA);
                    }
                });
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{permission3}, REQUEST_CAMERA);
            }
        }
        else
        {
            startScanNow();
        }
    }

    private void startScanNow()
    {
        Intent inIntent = new Intent(this, ActivityScan.class);
        inIntent.putExtra("type", type);
        startActivity(inIntent);
    }

    private void reload()
    {
        adapterList.update(type);

        updateMenu();
    }

    private void updateMenu()
    {
        if(menuClear != null) menuClear.setVisible(adapterList.getItemCount() > 0);
        if(menuUpload != null) menuUpload.setVisible(Attendance.uploader == null && adapterList.getItemCount() > 0);
    }

    private void clear()
    {
        AttendItem.deleteByType(type);

        reload();
    }

    private void upload()
    {
        Attendance.newUpload(this, true);
    }
}