package org.driverr.abhisheknair.driverrapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avast.android.dialogs.fragment.ProgressDialogFragment;

import org.android.volley.AuthFailureError;
import org.android.volley.DefaultRetryPolicy;
import org.android.volley.Request;
import org.android.volley.RequestQueue;
import org.android.volley.Response;
import org.android.volley.VolleyError;
import org.android.volley.toolbox.JsonObjectRequest;
import org.android.volley.toolbox.Volley;
import org.driverr.abhisheknair.driverrapp.utils.TypefaceSpan;
import org.driverr.abhisheknair.driverrapp.utils.Utils;
import org.driverr.abhisheknair.driverrapp.utils.UtilsBundle;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences mSharedPreferences;
    private Toolbar mToolBar;
    private EditText mName;
    private EditText mEmail;
    private EditText mNumber;
    private Button mLoginButton;
    private DialogFragment mProgressFragment;
    private Typeface typeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolBar();
        initControls();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPreferences.getString(Utils.CUSTOMER_NAME, null) != null) {
            startMapsActivity();
        } else {
            if (savedInstanceState != null) {
                mName.setText(savedInstanceState.getString(UtilsBundle.CUSTOMER_NAME));
                mEmail.setText(savedInstanceState.getString(UtilsBundle.CUSTOMER_EMAIL));
                mNumber.setText(savedInstanceState.getString(UtilsBundle.CUSTOMER_NUMBER));
            }
        }
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
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/AvenirLT-55-Roman.ttf");
        mName = (EditText) findViewById(R.id.customer_name);
        mName.setTypeface(typeFace);
        mName.clearFocus();
        mEmail = (EditText) findViewById(R.id.customer_email);
        mEmail.setTypeface(typeFace);
        mEmail.clearFocus();
        mNumber = (EditText) findViewById(R.id.customer_number);
        mNumber.setTypeface(typeFace);
        mNumber.clearFocus();
        mLoginButton = (Button) findViewById(R.id.button);
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/AvenirLTStd-Heavy.otf");
        mLoginButton.setTypeface(typeFace);
        mLoginButton.setOnClickListener(this);
    }

    private void initProgressDialog() {
        mProgressFragment = ProgressDialogFragment.createBuilder(this, this.getSupportFragmentManager())
                .setMessage(getResources().getString(R.string.registration_progress_msg))
                .setCancelable(false)
                .setCancelableOnTouchOutside(false)
                .show();
    }

    public void dismissDialogFragment(DialogFragment dialog) {
        try {
            if (dialog != null && dialog.isVisible()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (validateFields()) {
                    updateSharedPreferences();
//                    new AsyncPostTask().execute();
                    postJSON();
                }
                break;
            default:
                break;
        }
    }

    private boolean validateFields() {
        boolean isOkay = true;
        if (TextUtils.isEmpty(getNameText())) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_LONG).show();
            isOkay = false;
        } else if (getNumber().length() != 10) {
            Toast.makeText(this, "Enter 10 digit Number", Toast.LENGTH_LONG).show();
            isOkay = false;
        } else if (!getEmail().contains("@") || !getEmail().contains(".")) {
            Toast.makeText(this, "Enter valid email", Toast.LENGTH_LONG).show();
            isOkay = false;
        }
        return isOkay;
    }

    public void postDataToServer() throws IOException {
        URL url = new URL("http:///api/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter(Utils.CUSTOMER_NAME, getNameText())
                .appendQueryParameter(Utils.CUSTOMER_NUMBER, getNumber())
                .appendQueryParameter(Utils.CUSTOMER_EMAIL, getEmail());
        String query = builder.build().getEncodedQuery();

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);
        writer.flush();
        writer.close();
        os.close();

        conn.connect();


    }

    public JSONObject giveJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Utils.CUSTOMER_NAME, getNameText());
            jsonObject.put(Utils.CUSTOMER_EMAIL, getEmail());
            jsonObject.put(Utils.CUSTOMER_NUMBER, getNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getNameText() {
        return mName.getText().toString();
    }

    public String getNumber() {
        return mNumber.getText().toString();
    }

    public String getEmail() {
        return mEmail.getText().toString();
    }

    private void updateSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Utils.CUSTOMER_NAME, getNameText());
        editor.putString(Utils.CUSTOMER_NUMBER, getNumber());
        editor.putString(Utils.CUSTOMER_EMAIL, getEmail());
        editor.apply();
    }

    private void startMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    private void openFareCard() {
        Intent intent = new Intent(this, FareCardActivity.class);
        startActivity(intent);
    }

    private void openFAQ() {
        Intent intent = new Intent(this, FAQActivity.class);
        startActivity(intent);
    }

    private class AsyncPostTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            postJSON();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            initProgressDialog();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dismissDialogFragment(mProgressFragment);
//            startMapsActivity();
        }
    }

    private void postJSON() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, Utils.LOGIN_API_URL, giveJson(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismissDialogFragment(mProgressFragment);
                        Log.d("Response: ", response.toString());
                        String cust_id;
                        try {
                            cust_id = response.getString(Utils.SUCCESS_RESPONSE);
                        } catch (JSONException e) {
                            showRegistrationError();
                            return;
                        }
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(Utils.CUSTOMER_ID, cust_id);
                        editor.apply();
                        startMapsActivity();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialogFragment(mProgressFragment);
                error.printStackTrace();
                showRegistrationError();
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        initProgressDialog();
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
    }

    private void showRegistrationError() {
        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.registration_error), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(UtilsBundle.CUSTOMER_NAME, mName.getText().toString());
        outState.putString(UtilsBundle.CUSTOMER_EMAIL, mEmail.getText().toString());
        outState.putString(UtilsBundle.CUSTOMER_NUMBER, mNumber.getText().toString());
    }
}
