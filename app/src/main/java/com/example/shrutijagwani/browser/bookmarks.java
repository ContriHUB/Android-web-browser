package com.example.shrutijagwani.browser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.util.List;

public class bookmarks extends AppCompatActivity {
    myDbHandlerBook dbHandlerBook=new myDbHandlerBook(this,null,null,1);
    WebView mywebview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

    final List<String> books=dbHandlerBook.databaseToString();
    if(books.size()>0)
    {
        ArrayAdapter myadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,books);
        ListView mylist=(ListView)findViewById(R.id.listViewBook);
        mylist.setAdapter(myadapter);

        mylist.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String url=books.get(position);
                        Intent intent = new Intent(bookmarks.this,MainActivity.class);
                        intent.putExtra("url",url);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }
    }
}
