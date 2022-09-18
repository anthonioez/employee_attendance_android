package com.hmkm1c.attendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.hmkm1c.attendance.BuildConfig;
import com.hmkm1c.attendance.R;

public class ActivitySplash extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        new CountDownTimer(BuildConfig.DEBUG ? 200 : 2000, 100)
        {

            @Override
            public void onTick(long millisUntilFinished)
            {

            }

            @Override
            public void onFinish()
            {
                startApp();
            }
        }.start();
    }

    private void startApp()
    {
        startActivity(new Intent(this, ActivityLogin.class));
        finish();
    }
}
