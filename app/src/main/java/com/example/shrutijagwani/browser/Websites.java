package com.example.shrutijagwani.browser;

import android.webkit.WebView;
import android.widget.ImageView;

public class Websites {

    private int _id;
    private String _url;
    private ImageView image;
    private String title;

    public Websites() {
        //An empty constructor
    }

    public Websites(String url) {
        this._url = url;
    }

    public Websites(String url,String title){
        this._url=url;
        this.title=title;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public void set_url(String _url) {
        this._url = _url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int get_id() {
        return _id;
    }

    public String get_url() {
        return _url;
    }

    public String getTitle() {
        return title;
    }

    public ImageView getImage() {
        return image;
    }
}
