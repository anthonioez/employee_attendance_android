package com.hmkm1c.attendance.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hmkm1c.attendance.Attendance;
import com.hmkm1c.attendance.objects.HomeItem;
import com.hmkm1c.attendance.R;
import com.hmkm1c.attendance.adapters.AdapterHome;

public class ActivityMain extends AppCompatActivity implements AdapterHome.AdapterHomeListener
{
    private RecyclerView recyclerList;
    private AdapterHome adapterHome;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.app_name);

        adapterHome = new AdapterHome(this, this);

        recyclerList = findViewById(R.id.recyclerList);

        recyclerList.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerList.setAdapter(adapterHome);
        recyclerList.setItemAnimator(new DefaultItemAnimator());

        recyclerList.post(new Runnable()
        {
            @Override
            public void run()
            {
                int width = recyclerList.getMeasuredWidth();
                int height = recyclerList.getMeasuredHeight();

                adapterHome.resize(width, height - 100);
            }
        });

        Attendance.newUpload(this,true);
    }

    @Override
    public void onBackPressed()
    {
        back();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_logout:
                logout();
                return true;

            case android.R.id.home:
                back();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adp, View view, int position)
    {
        HomeItem item = (HomeItem) adapterHome.getItem(position);
        switch (item.id)
        {
            case CHECK_IN:
                startScan("In Time", "in");
                break;

            case CHECK_OUT:
                startScan("Out Time", "out");
                break;

            case SETTINGS:
                Intent settingsIntent = new Intent(this, ActivitySettings.class);
                startActivity(settingsIntent);
                break;

            case ATTENDANCE:
                Intent attendIntent = new Intent(this, ActivityAttendance.class);
                startActivity(attendIntent);
                break;
        }
    }

    private void startScan(String title, String type)
    {
        Intent inIntent = new Intent(this, ActivityList.class);
        inIntent.putExtra("title", title);
        inIntent.putExtra("type", type);
        startActivity(inIntent);
    }

    private void logout()
    {
        Intent inIntent = new Intent(this, ActivityLogin.class);
        startActivity(inIntent);

        setResult(RESULT_CANCELED);
        finish();
    }

    private void back()
    {
        setResult(RESULT_OK);

        finish();
    }
}