package de.dhbw.studienarbeit.hearItApp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * HomeActivity is a time limited Activity, that shows a splashscreen to increase
 * the professionalism of the applications optical appearance
 *
 * created by Andreas
 */

public class HomeActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    /**
     * onCreate()
     * shows the HomeActivities layout for 3 seconds and then
     * switches to the MainActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                Intent homeIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
