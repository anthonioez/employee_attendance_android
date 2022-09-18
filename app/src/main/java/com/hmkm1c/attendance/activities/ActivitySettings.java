package com.hmkm1c.attendance.activities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.hmkm1c.attendance.Prefs;
import com.hmkm1c.attendance.R;
import com.hmkm1c.attendance.Utils;

public class ActivitySettings extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
    private TextInputEditText editUrl;
    private TextInputEditText editKey;
    private Switch switchSound;
    private Switch switchFlash;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.settings);

        editUrl     = findViewById(R.id.editUrl);
        editKey     = findViewById(R.id.editKey);

        switchSound = findViewById(R.id.switchSound);
        switchFlash = findViewById(R.id.switchFlash);

        buttonSave  = findViewById(R.id.buttonSave);



        editUrl.setText(Prefs.getUrl(this));
        editKey.setText(Prefs.getToken(this));

        switchFlash.setChecked(Prefs.getFlash(this));
        switchSound.setChecked(Prefs.getSound(this));

        switchSound.setOnCheckedChangeListener(this);
        switchFlash.setOnCheckedChangeListener(this);

        buttonSave.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        if (view == buttonSave)
        {
            save();
        }
    }

    private void save()
    {
        String url = editUrl.getText().toString().trim();
        String key = editKey.getText().toString().trim();

        if(url.endsWith("/"))
        {
            url = url.substring(1, url.length() - 1);
        }

        if(!Patterns.WEB_URL.matcher(url).matches())
        {
            Utils.toast(this, "Invalid URL!");
        }
        else if(TextUtils.isEmpty(key))
        {
            Utils.toast(this, "Invalid KEY!");
        }
        else
        {
            Prefs.setUrl(this, url);
            Prefs.setToken(this, key);

            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
    {
        if(compoundButton == switchFlash)
        {
            Prefs.setFlash(this, checked);
        }
        else if(compoundButton == switchSound)
        {
            Prefs.setSound(this, checked);
        }
    }
}
