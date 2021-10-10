package com.example.shrutijagwani.browser;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private ProgressBar pbar;
    private WebView mywebview;
    private EditText myurl;
    private Button mybutton;
    private LinearLayout mylayout;
    private SwipeRefreshLayout mainlayout;
    private Intent intent;
    private MyDbHandler dbHandler;
    private myDbHandlerBook dbHandlerbook;
    private String mycurrenturl;
    private boolean saveHistory = true;
    public static final String SETTING_PREFERENCE = "com.example.shrutijagwani.browser.setting";
    public static final String SETTING_SAVE_HISTORY = "SETTING_SAVE_HISTORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setReferences();
        pbar.setMax(100);
        configureWebView();
        mainlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mywebview.reload();

            }
        });
        myurl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    openUrl();
                }
                return false;
            }
        });
        checkIntent();
    }

    private void checkIntent() {
        if (getIntent() != null &&
                Intent.ACTION_VIEW.equals(getIntent().getAction()) &&
                getIntent().getData() != null) {
            myurl.setText(getIntent().getData().toString());
            openUrl();
        }
    }

    private void configureWebView() {
        mywebview.getSettings().setJavaScriptEnabled(true);

        mywebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mylayout.setVisibility(View.VISIBLE);
                myurl.setText(url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mylayout.setVisibility(View.GONE);
                mainlayout.setRefreshing(false);
                super.onPageFinished(view, url);
                mycurrenturl = url;
            }
        });
        mywebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pbar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

        });


        mywebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                DownloadManager.Request myrequest = new DownloadManager.Request(Uri.parse(url));
                myrequest.allowScanningByMediaScanner();
                myrequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager mymanager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                mymanager.enqueue(myrequest);

                Toast.makeText(MainActivity.this, "Your File is downloading", Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void setReferences() {
        pbar = findViewById(R.id.progressbar);
        mywebview = findViewById(R.id.webview);
        myurl = findViewById(R.id.myet);
        mybutton = findViewById(R.id.mybtn);
        mylayout = findViewById(R.id.linearLayout);
        mainlayout = findViewById(R.id.mainlayout);
        dbHandler = new MyDbHandler(this, null, null, 1);
        dbHandlerbook = new myDbHandlerBook(this, null, null, 1);
    }


    @Override
    public void onBackPressed() {
        if (mywebview.canGoBack()) {
            mywebview.goBack();
            savedata();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.backward:
                onBackPressed();
                break;
            case R.id.forward:
                onForwardPressed();
                break;
            case R.id.refresh:
                onRefreshPressed();
                break;
            case R.id.history:
                onHistoryPressed();
                break;
            case R.id.newtab:
                onNewTabPressed();
                break;
            case R.id.tabs:
                onRecenttabsPressed();
                break;
            case R.id.showbookmarks:
                onShowbookPressed();
                break;
            case R.id.bookmarkthis:
                onBookPressed();
                break;
            case R.id.shareurl:
                share();
                break;
            case R.id.scan:
                scan();
                break;
            case R.id.exit:
                finish();

        }
        return true;
    }

    public void scan() {
        Intent i = new Intent(this, ReaderActivity.class);
        startActivity(i);
    }

    public void share() {
        Intent shareintent = new Intent(Intent.ACTION_SEND);
        shareintent.setType("text/plain");
        shareintent.putExtra(Intent.EXTRA_TEXT, mycurrenturl);
        shareintent.putExtra(Intent.EXTRA_SUBJECT, "Copied url");
        startActivity(Intent.createChooser(shareintent, "Share url"));
    }

    public void onRecenttabsPressed() {

    }

    private void onBookPressed() {
        Websites web = new Websites(mywebview.getUrl());
        dbHandlerbook.addUrl(web);
    }

    public void onNewTabPressed() {
        //Intent intent=new Intent(getApplicationContext(),newtab.class);
        //startActivity(intent);
    }

    public void onForwardPressed() {
        if (mywebview.canGoForward()) {
            mywebview.goForward();
            savedata();
        } else {
            Toast.makeText(this, "Cannot Go Forward", Toast.LENGTH_SHORT).show();
        }
    }

    private void onHistoryPressed() {
        intent = new Intent(this, history.class);
        startActivity(intent);
    }

    private void onShowbookPressed() {
        Intent intent = new Intent(this, bookmarks.class);
        startActivity(intent);
    }

    private void onRefreshPressed() {
        mywebview.reload();
    }

    public void goto1(View view) {
        openUrl();
    }

    private void openUrl() {
        if (myurl.getText().toString().equals("")) {
            return;
        }
        String website = myurl.getText().toString();
        mywebview.loadUrl(website);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(myurl.getWindowToken(), 0);
        if (saveHistory)
            savedata();
    }

    public void savedata() {
        Websites web = new Websites(mywebview.getUrl());
        dbHandler.addUrl(web);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mywebview.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mywebview.restoreState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(SETTING_PREFERENCE, MODE_PRIVATE);
        saveHistory = sharedPreferences.getBoolean(SETTING_SAVE_HISTORY, true);
    }
}
