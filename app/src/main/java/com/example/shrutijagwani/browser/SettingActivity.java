package com.example.shrutijagwani.browser;

import static com.example.shrutijagwani.browser.MainActivity.SETTING_PREFERENCE;
import static com.example.shrutijagwani.browser.MainActivity.SETTING_SAVE_HISTORY;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {

    private Switch saveHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setReferences();
        configure();
    }

    private void configure() {
        saveHistory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(SETTING_PREFERENCE, MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(SETTING_SAVE_HISTORY, isChecked).apply();
            }
        });
    }

    private void setReferences() {
        saveHistory = findViewById(R.id.switch_save_history);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(SETTING_PREFERENCE, MODE_PRIVATE);
        saveHistory.setChecked(sharedPreferences.getBoolean(SETTING_SAVE_HISTORY, true));
    }
}