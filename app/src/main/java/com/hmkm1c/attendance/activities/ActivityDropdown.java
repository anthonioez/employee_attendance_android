package com.hmkm1c.attendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.hmkm1c.attendance.Attendance;
import com.hmkm1c.attendance.R;
import com.hmkm1c.attendance.Utils;
import com.hmkm1c.attendance.objects.UserItem;
import com.hmkm1c.attendance.tasks.Dropdowner;

import java.util.ArrayList;
import java.util.List;

public class ActivityDropdown extends AppCompatActivity implements View.OnClickListener, Dropdowner.DropdownListener, AdapterView.OnItemSelectedListener
{
    private static final int REQUEST_MAIN = 32;
    private Button buttonScan;

    private Dropdowner task;
    private Spinner spinnerUsers;
    private Spinner spinnerTimes;

    private ArrayAdapter<String> adapterUsers;
    private ArrayAdapter<String> adapterTimes;
    private List<UserItem> listUsers;
    private List<String> listNames;
    private List<String> listTimes;

    private int selectedTime;
    private int selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        listUsers = new ArrayList<>();
        listNames = new ArrayList<>();
        listTimes = new ArrayList<>();

        setContentView(R.layout.activity_dropdown);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.select);

        spinnerUsers     = findViewById(R.id.spinnerUsers);
        spinnerTimes     = findViewById(R.id.spinnerTimes);
        buttonScan     = findViewById(R.id.buttonScan);

        buttonScan.setOnClickListener(this);

        adapterUsers = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listNames);
        adapterUsers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(adapterUsers);

        adapterTimes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listTimes);
        adapterTimes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimes.setAdapter(adapterTimes);

        spinnerTimes.setOnItemSelectedListener(this);
        spinnerUsers.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(listUsers.size() == 0 && listTimes.size() == 0)
        {
            load();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        unload();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_MAIN)
        {
            if(resultCode == RESULT_CANCELED)
            {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.dropdown, menu);
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
            case R.id.action_reload:
                load();
                return true;

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view)
    {
        if(view == buttonScan)
        {
            validate();
        }
    }

    private void update()
    {
        adapterTimes.notifyDataSetChanged();
        adapterUsers.notifyDataSetChanged();
    }

    private void unload()
    {
        if(task != null)
        {
            task.cancel(true);
            task = null;
        }
    }

    private void load()
    {
        unload();

        task = new Dropdowner(this, this);
        task.execute();
    }

    private void validate()
    {
        if(selectedTime == 0)
        {
            Utils.toast(this, "Please select start time!");
        }
        else if(selectedUser == 0)
        {
            Utils.toast(this, "Please select user!");
        }
        else
        {
            UserItem user = listUsers.get(selectedUser);
            if(user.banned)
            {
                Utils.alert(this, "Error", "Selecte user is banned, cannot scan!");
                return;
            }
            else
            {
                Attendance.currentUser = user.name;
                Attendance.currentTime = listTimes.get(selectedTime);

                startMain();
            }
        }
    }

    private void startMain()
    {
        Intent intent = new Intent(this, ActivityMain.class);
        startActivityForResult(intent, REQUEST_MAIN);
    }

    @Override
    public void onDropdownSuccess(List<UserItem> users, List<String> times)
    {
        listTimes.clear();
        listTimes.add("Select start");
        listTimes.addAll(times);

        listUsers.clear();
        listUsers.add(new UserItem());
        listUsers.addAll(users);

        listNames.clear();
        listNames.add("Select user");
        for(UserItem user : users)
        {
            listNames.add(user.name);
        }

        update();
    }

    @Override
    public void onDropdownError(String msg)
    {
        if(TextUtils.isEmpty(msg)) return;

        Utils.alert(this, "Error", msg);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        if(adapterView == spinnerTimes)
        {
            selectedTime = i;
        }
        else if(adapterView == spinnerUsers)
        {
            selectedUser = i;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }
}