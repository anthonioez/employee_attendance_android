package com.hmkm1c.attendance.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hmkm1c.attendance.Prefs;
import com.hmkm1c.attendance.R;
import com.hmkm1c.attendance.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityAttendance extends AppCompatActivity
{
    private String TAG = ActivityAttendance.class.getSimpleName();

    private WebView webView;

    private ProgressBar progressBar;

    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        currentUrl = Prefs.getUrl(this);
        if(TextUtils.isEmpty(currentUrl))
        {
            Utils.toast(this, "App URL not set!");
            return;
        }

        currentUrl += "/main/login";

        setContentView(R.layout.activity_attendance);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Attendance");

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        // set javascript and zoom and some other settings
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setUseWideViewPort(true);

        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setInitialScale(100);
        webView.setBackgroundColor(0xFFFFFFFF);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);

        // Below required for geolocation
        //webView.setGeolocationEnabled(true);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setSaveEnabled(true);

        webView.setWebViewClient(new MainWebViewClient());
        webView.setWebChromeClient(new MainWebChromeClient());
        webView.requestFocus();

        CookieManager.getInstance().setAcceptCookie(true);

        loadWeb();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        webView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause()
    {
        super.onPause();
        Log.v(TAG, "PAUSE");

        webView.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //webView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.attendance, menu);
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
                loadWeb();
                return true;

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadWeb()
    {
        // load url (if connection available
        if (Utils.isOnline(this))
        {
            progressBar.setVisibility(View.VISIBLE);

            webView.loadUrl(currentUrl);
        }
        else
        {
            progressBar.setVisibility(View.GONE);

            Utils.toast(this, getString(R.string.no_internet));
        }
    }

    private void loadUI(boolean show)
    {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public class MainWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webview, String url)
        {
            Log.i(TAG, "Overiding " + url);

            if ((url != null) && (url.contains("whatsapp:")))
            {
                webview.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                return true;
            }
            if ((url != null) && (url.contains("mailto:")))
            {
                webview.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                return true;
            }

            return super.shouldOverrideUrlLoading(webview, url);
        }

        // handeling errors
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            Log.i(TAG, "onReceivedError: " + errorCode + " desc: " + description + " url: " + failingUrl);

            String errorHtml = "<html><body><div align=\"center\">" + "</div></body></html>";
            webView.loadData(errorHtml, "text/html", null);

            Utils.toast(ActivityAttendance.this, getString(R.string.no_internet));
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            loadUI(true);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);

            loadUI(false);
        }

    }

    public class MainWebChromeClient extends WebChromeClient
    {
        @Override
        public void onProgressChanged(WebView view, int progress)
        {
            if (progress == 100)
            {
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
