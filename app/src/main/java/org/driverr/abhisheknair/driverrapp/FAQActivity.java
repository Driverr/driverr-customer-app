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

public class FAQActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TextView mFaq1;
    private TextView mFaq1_ans;
    private TextView mFaq2;
    private TextView mFaq2_ans;
    private TextView mFaq3;
    private TextView mFaq3_ans;
    private TextView mFaq4;
    private TextView mFaq4_ans;
    private TextView mFaq5;
    private TextView mFaq5_ans;
    private TextView mFaq6;
    private TextView mFaq6_ans;
    private TextView mFaq7;
    private TextView mFaq7_ans;
    private TextView mFaq8;
    private TextView mFaq8_ans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
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
        Typeface hel_bold = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue-Bold.otf");
        Typeface hel_light = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue-Light.otf");
        mFaq1 = (TextView) findViewById(R.id.faq1tv);
        mFaq2 = (TextView) findViewById(R.id.faq2tv);
        mFaq3 = (TextView) findViewById(R.id.faq3tv);
        mFaq4 = (TextView) findViewById(R.id.faq4tv);
        mFaq5 = (TextView) findViewById(R.id.faq5tv);
        mFaq6 = (TextView) findViewById(R.id.faq6tv);
        mFaq7 = (TextView) findViewById(R.id.faq7tv);
        mFaq8 = (TextView) findViewById(R.id.faq8tv);
        mFaq1.setTypeface(hel_bold);
        mFaq2.setTypeface(hel_bold);
        mFaq3.setTypeface(hel_bold);
        mFaq4.setTypeface(hel_bold);
        mFaq5.setTypeface(hel_bold);
        mFaq6.setTypeface(hel_bold);
        mFaq7.setTypeface(hel_bold);
        mFaq8.setTypeface(hel_bold);

        mFaq1_ans = (TextView) findViewById(R.id.faq1_anstv);
        mFaq2_ans = (TextView) findViewById(R.id.faq2_anstv);
        mFaq3_ans = (TextView) findViewById(R.id.faq3_anstv);
        mFaq4_ans = (TextView) findViewById(R.id.faq4_anstv);
        mFaq5_ans = (TextView) findViewById(R.id.faq5_anstv);
        mFaq6_ans = (TextView) findViewById(R.id.faq6_anstv);
        mFaq7_ans = (TextView) findViewById(R.id.faq7_anstv);
        mFaq8_ans = (TextView) findViewById(R.id.faq8_anstv);
        mFaq1_ans.setTypeface(hel_light);
        mFaq2_ans.setTypeface(hel_light);
        mFaq3_ans.setTypeface(hel_light);
        mFaq4_ans.setTypeface(hel_light);
        mFaq5_ans.setTypeface(hel_light);
        mFaq6_ans.setTypeface(hel_light);
        mFaq7_ans.setTypeface(hel_light);
        mFaq8_ans.setTypeface(hel_light);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_faq, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
