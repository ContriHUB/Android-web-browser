package com.example.shrutijagwani.browser;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    ProgressBar pbar;
    WebView mywebview;
    EditText myurl;
    Button mybutton;
    LinearLayout mylayout;
    SwipeRefreshLayout mainlayout;
    Intent intent;
    MyDbHandler dbHandler;
    myDbHandlerBook dbHandlerbook;
    String mycurrenturl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbar=(ProgressBar)findViewById(R.id.progressbar);
        mywebview=(WebView)findViewById(R.id.webview);
        myurl=(EditText)findViewById(R.id.myet);
        mybutton=(Button)findViewById(R.id.mybtn);
        mylayout=(LinearLayout)findViewById(R.id.linearLayout);
        mainlayout=(SwipeRefreshLayout)findViewById(R.id.mainlayout);
        dbHandler= new MyDbHandler(this,null,null,1);
        dbHandlerbook=new myDbHandlerBook(this,null,null,1);
        pbar.setMax(100);
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mylayout.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mylayout.setVisibility(View.GONE);
                mainlayout.setRefreshing(false);
                super.onPageFinished(view, url);
                mycurrenturl=url;
            }
        });
        mywebview.setWebChromeClient(new WebChromeClient(){
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

                DownloadManager.Request myrequest=new DownloadManager.Request(Uri.parse(url));
                myrequest.allowScanningByMediaScanner();
                myrequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager mymanager=(DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                mymanager.enqueue(myrequest);

                Toast.makeText(MainActivity.this,"Your File is downloading",Toast.LENGTH_SHORT).show();


            }
        });

        if(getIntent().getStringExtra("url")!=null){
            mywebview.loadUrl(getIntent().getStringExtra("url"));
        }
        mainlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mywebview.reload();

            }
        });
        }



    @Override
    public void onBackPressed() {
        if(mywebview.canGoBack()){
            mywebview.goBack();
            savedata();
        }
        else
        {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.backward:onBackPressed();
                    break;
            case R.id.forward:onForwardPressed();
                    break;
            case R.id.refresh:onRefreshPressed();
                    break;
            case R.id.history:onHistoryPressed();
                    break;
            case R.id.newtab:onNewTabPressed();
                    break;
            case R.id.tabs:onRecenttabsPressed();
                    break;
            case R.id.showbookmarks:onShowbookPressed();
                    break;
            case R.id.bookmarkthis:onBookPressed();
                        break;
            case R.id.shareurl:share();
                        break;
            case R.id.scan:scan();
                        break;
            case R.id.exit:finish();

        }
        return true;
    }

    public void scan()
    {
        Intent i=new Intent(this,ReaderActivity.class);
        startActivity(i);
    }

    public void share()
    {
        Intent shareintent= new Intent(Intent.ACTION_SEND);
        shareintent.setType("text/plain");
        shareintent.putExtra(Intent.EXTRA_TEXT,mycurrenturl);
        shareintent.putExtra(Intent.EXTRA_SUBJECT,"Copied url");
        startActivity(Intent.createChooser(shareintent,"Share url"));
    }

    public void onRecenttabsPressed(){

    }
    private void onBookPressed()
    {
        Websites web=new Websites(mywebview.getUrl());
        dbHandlerbook.addUrl(web);
    }
    public void onNewTabPressed(){
        //Intent intent=new Intent(getApplicationContext(),newtab.class);
        //startActivity(intent);
    }
    public void onForwardPressed()
    {
        if(mywebview.canGoForward()){
            mywebview.goForward();
            savedata();
        }
        else
        {
            Toast.makeText(this,"Cannot Go Forward",Toast.LENGTH_SHORT).show();
        }
    }
    private void onHistoryPressed()
    {intent =new Intent(this,history.class);
        startActivity(intent);
    }
    private void onShowbookPressed()
    {
        Intent intent=new Intent(this,bookmarks.class);
        startActivity(intent);
    }
    private void onRefreshPressed()
    {
        mywebview.reload();
    }

    public void goto1(View view){

        String website="https://www.";
        website+=myurl.getText().toString();
        mywebview.loadUrl(website);
        InputMethodManager imm=(InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myurl.getWindowToken(),0);
        savedata();
    }

    public void savedata(){
        Websites web=new Websites(mywebview.getUrl());
        dbHandler.addUrl(web);

    }
}
