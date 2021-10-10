package com.example.shrutijagwani.browser;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItem;
import android.support.v7.view.menu.ActionMenuItemView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String CURRENT = "CURRENT";
    private static final int RECENT_TAB_REQUEST = 1;
    private int currentIndex = -1;
    private ProgressBar pbar;
    private WebView currentWebView;
    private EditText myurl;
    private Button mybutton;
    private LinearLayout mylayout;
    private SwipeRefreshLayout mainlayout;
    private TextView noTab;
    private FrameLayout frameLayout;
    private Intent intent;
    private MyDbHandler dbHandler;
    private myDbHandlerBook dbHandlerbook;
    private String mycurrenturl;
    private boolean saveHistory = true;
    public static final String SETTING_PREFERENCE = "com.example.shrutijagwani.browser.setting";
    public static final String SETTING_SAVE_HISTORY = "SETTING_SAVE_HISTORY";
    public static final List<WebView> webViews = new ArrayList<>();
    private List<String> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setReferences();
        pbar.setMax(100);
        mainlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentWebView.reload();
            }
        });
        myurl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO) {
                    openUrl(myurl.getText().toString());
                }
                return false;
            }
        });
        checkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    private void setReferences() {
        noTab = findViewById(R.id.no_tab);
        pbar = findViewById(R.id.progressbar);
        frameLayout = findViewById(R.id.frame);
        myurl = findViewById(R.id.myet);
        mybutton = findViewById(R.id.mybtn);
        mylayout = findViewById(R.id.linearLayout);
        mainlayout = findViewById(R.id.mainlayout);
        dbHandler = new MyDbHandler(this, null, null, 1);
        dbHandlerbook = new myDbHandlerBook(this, null, null, 1);
    }

    private void checkIntent(Intent intent) {
        if (intent != null &&
                Intent.ACTION_VIEW.equals(intent.getAction()) &&
                intent.getData() != null) {
            onNewTabPressed();
            myurl.setText(intent.getData().toString());
            openUrl(intent.getData().toString());
        }
    }

    private void configureWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
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
        webView.setWebChromeClient(new WebChromeClient() {
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
        webView.setDownloadListener(new DownloadListener() {
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

    @Override
    public void onBackPressed() {
        if (currentWebView.canGoBack()) {
            currentWebView.goBack();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.bookmarkthis);

        if (currentWebView == null) {
            item.setIcon(R.drawable.ic_bookmark_grey_24dp);
            return true;
        }
        if (currentWebView.getUrl() == null) {
            item.setIcon(R.drawable.ic_bookmark_grey_24dp);
        } else if (!isBookMark(new Websites(currentWebView.getUrl()))) {
            item.setIcon(R.drawable.ic_bookmark_yellow_24dp);
        } else {
            item.setIcon(R.drawable.ic_bookmark_black_24dp);
            item.setEnabled(true);
        }

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
        Intent intent = new Intent(this, RecentTabsActivity.class);
        intent.putExtra(CURRENT, currentIndex);
        startActivityForResult(intent, RECENT_TAB_REQUEST);
    }

    private void onBookPressed() {
        Websites web = new Websites(currentWebView.getUrl());
        dbHandlerbook.addUrl(web);
        if (currentWebView.getUrl() == null) {
            return;
        }
        insertBookmark();
        invalidateOptionsMenu();
    }

    private void insertBookmark() {

        Websites web = new Websites(currentWebView.getUrl());

        if (isBookMark(web)) {
            dbHandlerbook.addUrl(web);
        }

    }

    private boolean isBookMark(Websites web) {
        books = dbHandlerbook.databaseToString();
        String bookMark = web.get_url();

        int counter = 0;
        for (int i = 0; i < books.size(); i++) {
            if (bookMark.equals(books.get(i))) {
                counter++;
            }
        }
        if (counter == 0)
            return true;
        else
            return false;
    }

    public void onNewTabPressed() {
        mainlayout.setVisibility(View.VISIBLE);
        noTab.setVisibility(View.GONE);
        WebView webView = new WebView(this);
        configureWebView(webView);
        webViews.add(webView);
        frameLayout.removeAllViews();
        frameLayout.addView(webView);
        currentWebView = webView;
        currentIndex = webViews.size() - 1;
        myurl.setText("");
    }

    public void onForwardPressed() {
        if (currentWebView.canGoForward()) {
            currentWebView.goForward();
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
        currentWebView.reload();
    }

    public void goto1(View view) {
        openUrl(myurl.getText().toString());
    }

    private void openUrl(String website) {
        if (myurl.getText().toString().equals("")) {
            return;
        }
        currentWebView.loadUrl(website);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(myurl.getWindowToken(), 0);
        if (saveHistory)
            savedata();
    }

    public void savedata() {
        Websites web = new Websites(currentWebView.getUrl());
        dbHandler.addUrl(web);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentWebView != null)
            currentWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (currentWebView != null)
            currentWebView.restoreState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(SETTING_PREFERENCE, MODE_PRIVATE);
        saveHistory = sharedPreferences.getBoolean(SETTING_SAVE_HISTORY, true);
        if (webViews.size() > 0) {
            boolean deleted = true;
            for (WebView w : webViews) {
                if (w.equals(currentWebView)) {
                    deleted = false;
                    break;
                }
            }
            if (deleted) {
                frameLayout.removeAllViews();
                currentWebView = webViews.get(0);
                frameLayout.addView(currentWebView);
                currentIndex = 0;
                myurl.setText(currentWebView.getUrl());
            }
        } else {
            frameLayout.removeAllViews();
            mainlayout.setVisibility(View.GONE);
            noTab.setVisibility(View.VISIBLE);
            myurl.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECENT_TAB_REQUEST && resultCode == RESULT_OK) {
            frameLayout.removeAllViews();
            if (webViews.size() > 0) {
                currentIndex = data.getIntExtra(CURRENT, 0);
                WebView webView = webViews.get(data.getIntExtra(CURRENT, 0));
                currentWebView = webView;
                frameLayout.addView(currentWebView);
                myurl.setText(currentWebView.getUrl());
            } else {
                frameLayout.removeAllViews();
                mainlayout.setVisibility(View.GONE);
                noTab.setVisibility(View.VISIBLE);
                myurl.setText("");
            }
        }
    }
}
