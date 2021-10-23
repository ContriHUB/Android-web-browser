package com.example.shrutijagwani.browser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;

public class bookmarks extends AppCompatActivity {
    myDbHandlerBook dbHandlerBook=new myDbHandlerBook(this,null,null,1);
    WebView mywebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setTitle("Bookmarks");

        ListView mylist=(ListView)findViewById(R.id.listViewBook);
        final List<String> books=dbHandlerBook.databaseToString();
        final ArrayAdapter myadapter = new ArrayAdapter<String>(this,R.layout.custom_view,R.id.txt
                ,books);



        if(books.size()>0)
        {
//        ArrayAdapter myadapter = new ArrayAdapter<String>(this,R.layout.custom_view,books);

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

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int position = i;

                AlertDialog.Builder builder = new AlertDialog.Builder(bookmarks.this);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dbHandlerBook.deleteUrl(books.get(position).toString());
                        books.remove(position);
                        myadapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.setMessage("Do you want to delete this bookmark?");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }
}
