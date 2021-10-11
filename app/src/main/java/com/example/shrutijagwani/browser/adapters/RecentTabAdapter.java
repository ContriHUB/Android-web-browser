package com.example.shrutijagwani.browser.adapters;

import static android.app.Activity.RESULT_OK;
import static com.example.shrutijagwani.browser.MainActivity.CURRENT;
import static com.example.shrutijagwani.browser.MainActivity.webViews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.shrutijagwani.browser.R;


import de.mrapp.android.util.view.ViewHolder;

public class RecentTabAdapter extends RecyclerView.Adapter<RecentTabAdapter.CustomVH> {
    private Context context;
    private int current;

    public RecentTabAdapter(Context context, int current) {
        this.context = context;
        this.current = current;
    }

    @NonNull
    @Override
    public CustomVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CustomVH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_tab, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomVH customVH, int i) {
        customVH.setTab(webViews.get(i));
    }

    @Override
    public int getItemCount() {
        return webViews.size();
    }

    public class CustomVH extends ViewHolder {

        private final TextView url;
        private final TextView title;
        private final ImageView favicon;

        public CustomVH(@NonNull View parentView) {
            super(parentView);
            url = parentView.findViewById(R.id.url);
            title = parentView.findViewById(R.id.title);
            favicon = parentView.findViewById(R.id.favicon);
            ImageView close = parentView.findViewById(R.id.close);
            LinearLayout root = parentView.findViewById(R.id.root);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == current)
                        current = 0;
                    webViews.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = getAdapterPosition();
                    Intent intent = new Intent();
                    intent.putExtra(CURRENT, current);
                    ((Activity) context).setResult(RESULT_OK, intent);
                    ((Activity) context).finish();
                }
            });
        }

        public void setTab(WebView webView) {
            if (webView.getFavicon() != null)
                favicon.setImageBitmap(webView.getFavicon());
            else
                favicon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_tab_24, null));
            if (webView.getUrl() != null)
                url.setText(webView.getUrl());
            else
                url.setText(context.getString(R.string.new_tab));
            if (webView.getTitle() != null && !webView.getTitle().equals(""))
                title.setText(webView.getTitle());
            else
                title.setText(context.getString(R.string.new_tab));
        }
    }
}
