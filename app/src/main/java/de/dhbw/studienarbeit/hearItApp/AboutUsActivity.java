package de.dhbw.studienarbeit.hearItApp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * AboutUsActivity is an Activity that shows informations about the
 * app-developers, their mail-address for contact and notes of thanks
 * to their person in charge
 *
 * created by Andreas
 */

public class AboutUsActivity extends AppCompatActivity {

    /**
     * onCreate()
     * changes the default actionbar to the customized one
     * changes the actionbars header-label
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background_gradient));

        TextView maintext = (TextView)findViewById(R.id.actionbar_maintext);
        maintext.setText("About Us");
        findViewById(R.id.about_us_button).setVisibility(View.INVISIBLE);
    }

    /**
     * onOptionsItemSelected()
     * switches back to MainActivity with a transition-effect
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in_2, R.anim.fade_out_2);

        }
        return super.onOptionsItemSelected(item);
    }
}
