package com.hmkm1c.attendance.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.hmkm1c.attendance.BuildConfig;
import com.hmkm1c.attendance.R;
import com.hmkm1c.attendance.Utils;
import com.hmkm1c.attendance.tasks.Loginer;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener, Loginer.LoginListener
{
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;
    private Button buttonLogin;

    private Loginer task;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle(R.string.login);

        editEmail       = findViewById(R.id.editEmail);
        editPassword    = findViewById(R.id.editPassword);

        buttonLogin     = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(this);

        if (BuildConfig.DEBUG)
        {
            editEmail.setText("admin@gmail.com");
            editPassword.setText("admin");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        unlogin();
    }

    @Override
    public void onClick(View view)
    {
        if(view == buttonLogin)
        {
            validate();
        }
    }

    private void validate()
    {
        String email    = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if(!Utils.isValidEmail(email))
        {
            Utils.toast(this, "Please enter your email address!");
        }
        else if(password.length() < 5)
        {
            Utils.toast(this, "Please enter your password!");
        }
        else
        {
            Utils.hideKeyboard(this, editEmail);


            login(email, password);
        }
    }

    private void unlogin()
    {
        if(task != null)
        {
            task.cancel(true);
            task = null;
        }
    }

    private void login(String email, String password)
    {
        unlogin();

        task = new Loginer(this, email, password, this);
        task.execute();
    }

    private void startDrop()
    {
        Intent intent = new Intent(this, ActivityDropdown.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void onLoginSuccess(String msg)
    {
        Utils.toast(this, msg);

        startDrop();
    }

    @Override
    public void onLoginError(String msg)
    {
        if(TextUtils.isEmpty(msg)) return;

        Utils.alert(this, "Login Error", msg);
    }
}