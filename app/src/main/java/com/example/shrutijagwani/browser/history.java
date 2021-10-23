package com.example.shrutijagwani.browser;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

public class history extends AppCompatActivity {

    MyDbHandler dbHandler=new MyDbHandler(this,null,null,1);
    WebView mywebview;
    //android.app.ActionBar ab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setTitle("History");



        final List<String> sites=dbHandler.databaseToString();
        if(sites.size()>0){
            ArrayAdapter myadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,sites);
            ListView mylist=(ListView)findViewById(R.id.listview);
            mylist.setAdapter(myadapter);

            mylist.setOnItemClickListener(

            new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String url=sites.get(position);
                            Intent intent = new Intent(history.this,MainActivity.class);
                            intent.putExtra("url",url);
                            startActivity(intent);
                            finish();
                        }
                    }
            );
        }
    }
}
