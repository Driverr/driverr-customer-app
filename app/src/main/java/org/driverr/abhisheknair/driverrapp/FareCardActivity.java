package org.driverr.abhisheknair.driverrapp;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.driverr.abhisheknair.driverrapp.utils.TypefaceSpan;

public class FareCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_card);
        initToolBar();
        initControls();
    }

    private void initToolBar() {
        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        if (Build.VERSION.SDK_INT > 20) {
            mToolBar.setElevation(0);
        }
        SpannableString s = new SpannableString(getResources().getString(R.string.title_activity_fare_card));
        s.setSpan(new TypefaceSpan(this, "AvenirLT-55-Roman.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mToolBar.setTitle(s);
        setSupportActionBar(mToolBar);
    }

    private void initControls() {
        TextView mFareTv = (TextView) findViewById(R.id.fareCardTv);
        TextView mFareNightTv = (TextView) findViewById(R.id.fareCardNightTv);
        Typeface hel_light = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue-Light.otf");
        mFareTv.setTypeface(hel_light);
        mFareNightTv.setTypeface(hel_light);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_fare_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fareCard) {
            return true;
        } else if (id == R.id.action_faq) {

        } else if (id == R.id.action_aboutUs) {

        } else if (id == R.id.action_myRides) {

        }

        return super.onOptionsItemSelected(item);
    }
}
