/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The main activity of the API library demo gallery.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class MainActivity extends Activity {

    private SharedPreferences sharedPreferences;
    public static final String PREFERENCES = "IPreferMyNipplesToBeFlaming";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        sharedPreferences = getSharedPreferences(PREFERENCES, 0);

        new Timer().schedule(new TimerTask() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (sharedPreferences.getBoolean("isLanguageSelected", false)) {
                            startActivity(new Intent(MainActivity.this, MapActivity.class));
                            finish();
                            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                        } else {
                            setContentView(R.layout.language_selection_page);
                        }
                    }
                });
            }
        }, 2000);
    }

    public void saveLanguage(View view) {
        Locale locale;
        TextView tv = (TextView) view;
        if(tv.getText().toString().equals("English")) {
            locale = new Locale("en", "US");
            sharedPreferences.edit().putString("language", "en").putString("locale", "US").apply();
        } else {
            locale = new Locale("es", "ES");
            sharedPreferences.edit().putString("language", "es").putString("locale", "ES").apply();
        }
        Locale.setDefault(locale);
        Configuration appConfig = new Configuration();
        appConfig.locale = locale;
        getBaseContext().getResources().updateConfiguration(appConfig,
              getBaseContext().getResources().getDisplayMetrics());

        sharedPreferences.edit().putBoolean("isLanguageSelected", true).apply();
        startActivity(new Intent(MainActivity.this, MapActivity.class));
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }
}
