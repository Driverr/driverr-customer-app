package org.driverr.abhisheknair.driverrapp;

import android.content.Intent;
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

public class SuccessActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TextView mSuccessText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_page);
        initToolBar();
        initControls();
    }

    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        if (Build.VERSION.SDK_INT > 20) {
            mToolBar.setElevation(0);
        }
        SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
        s.setSpan(new TypefaceSpan(this, "AvenirLT-55-Roman.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mToolBar.setTitle(s);
        setSupportActionBar(mToolBar);
    }

    private void initControls() {
        mSuccessText = (TextView) findViewById(R.id.successTv);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue-Light.otf");
        mSuccessText.setTypeface(typeFace);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_success_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fareCard) {
            openFareCard();
            return true;
        } else if (id == R.id.action_faq) {
            openFAQ();
            return true;
        } else if (id == R.id.action_aboutUs) {

        } else if (id == R.id.action_myRides) {

        }

        return super.onOptionsItemSelected(item);
    }

    private void openFAQ() {
        Intent intent = new Intent(this, FAQActivity.class);
        startActivity(intent);
    }

    private void openFareCard() {
        Intent intent = new Intent(this, FareCardActivity.class);
        startActivity(intent);
    }
}
