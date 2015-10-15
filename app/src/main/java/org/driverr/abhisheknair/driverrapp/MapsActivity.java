package org.driverr.abhisheknair.driverrapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avast.android.dialogs.fragment.ProgressDialogFragment;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.android.volley.AuthFailureError;
import org.android.volley.DefaultRetryPolicy;
import org.android.volley.Request;
import org.android.volley.RequestQueue;
import org.android.volley.Response;
import org.android.volley.VolleyError;
import org.android.volley.toolbox.JsonObjectRequest;
import org.android.volley.toolbox.Volley;
import org.driverr.abhisheknair.driverrapp.utils.CustomMapFragment;
import org.driverr.abhisheknair.driverrapp.utils.MapWrapperLayout;
import org.driverr.abhisheknair.driverrapp.utils.TypefaceSpan;
import org.driverr.abhisheknair.driverrapp.utils.Utils;
import org.driverr.abhisheknair.driverrapp.utils.UtilsBundle;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends AppCompatActivity
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        MapWrapperLayout.OnDragListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private CustomMapFragment mCustomMapFragment;
    private Button mSelectButton;
    private TextView mCurrentAddress;
    private TextView mSetPickupTv;
    private Toolbar mToolBar;
    private String rideDate;
    private String rideTime;
    private double latitude;
    private double longitude;
    private DatePickerDialog dpd;
    private TimePickerDialog tpd;
    private LocationManager mLocationManager;
    private SharedPreferences mSharedPreferences;
    private DialogFragment mProgressFragment;
    private Calendar calendar;
    private boolean isFutureOrCurrentDate = false;
    private boolean isServiceAvailableInCity = true;

    private View mMarkerParentView;
    private ImageView mMarkerImageView;

    private int imageParentWidth = -1;
    private int imageParentHeight = -1;
    private int imageHeight = -1;
    private int centerX = -1;
    private int centerY = -1;

    public static final String TIME_PICKER_TAG = "Timepickerdialog";
    public static final String DATE_PICKER_TAG = "Datepickerdialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initToolBar();
        initControls();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

    private void openFareCard() {
        Intent intent = new Intent(this, FareCardActivity.class);
        startActivity(intent);
    }

    private void openFAQ() {
        Intent intent = new Intent(this, FAQActivity.class);
        startActivity(intent);
    }

    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        if (Build.VERSION.SDK_INT > 20) {
            mToolBar.setElevation(4);
        }
        SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
        s.setSpan(new TypefaceSpan(this, "AvenirLT-55-Roman.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mToolBar.setTitle(s);
        setSupportActionBar(mToolBar);
    }

    private void initControls() {
        mSelectButton = (Button) findViewById(R.id.selectTv);
        mSelectButton.setOnClickListener(this);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mapsLocationLl);
        mCurrentAddress = (TextView) linearLayout.findViewById(R.id.currentLocationTv);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/AvenirLT-55-Roman.ttf");
        mSelectButton.setTypeface(typeFace);
        mCurrentAddress.setTypeface(typeFace);
        mSetPickupTv = (TextView) linearLayout.findViewById(R.id.setLocationTv);
        mSetPickupTv.setTypeface(typeFace);
        mMarkerParentView = findViewById(R.id.marker_view_incl);
        mMarkerImageView = (ImageView) findViewById(R.id.marker_icon_view);
        calendar = Calendar.getInstance();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        imageParentWidth = mMarkerParentView.getWidth();
        imageParentHeight = mMarkerParentView.getHeight();
        imageHeight = mMarkerImageView.getHeight();

        centerX = imageParentWidth / 2;
        centerY = (imageParentHeight / 2) + (imageHeight / 2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectTv:
                if (isServiceAvailableInCity) {
                    if (mSelectButton.getText().toString().equals(getResources().getString(R.string.select_ride_time))) {
                        openDateTimePicker();
//                        openDatePicker();
                        break;
                    } else if (mSelectButton.getText().toString().equals(getResources().getString(R.string.select_driver))) {
                        postJSON();
                        break;
                    }
                } else {
                    showNoServiceInCityError();
                }
            default:
                break;
        }
    }

    private void openDatePicker() {
        dpd = DatePickerDialog.newInstance(
                MapsActivity.this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(calendar);
        dpd.show(getFragmentManager(), DATE_PICKER_TAG);
    }

    private void openTimePicker() {
        tpd = TimePickerDialog.newInstance(
                MapsActivity.this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        tpd.show(getFragmentManager(), TIME_PICKER_TAG);
    }

    private void closeDatePicker() {
        dpd.dismiss();
    }

    private void openDateTimePicker() {
        SimpleDateFormat mFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        String minDateTime = "" + calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH)
                + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY) + 1) + calendar.get(Calendar.MINUTE);
        Date minDate;
        try {
            minDate = mFormatter.parse(minDateTime.trim());
        } catch (ParseException e) {
            minDate = new Date();
        }
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .setMinDate(minDate)
                        //.setMaxDate(maxDate)
                        //.setIs24HourTime(true)
                        //.setTheme(SlideDateTimePicker.HOLO_DARK)
                        //.setIndicatorColor(Color.parseColor("#990000"))
                .build()
                .show();
    }

    final SimpleDateFormat mFormatter = new SimpleDateFormat("dd-MM-yyyy;hh:mm aa", Locale.getDefault());

    SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            String dateObject = mFormatter.format(date);
            String[] da = dateObject.split(";");
            rideDate = da[0];
            rideTime = da[1];
            int curYear = calendar.get(Calendar.YEAR);
            int curMonth = calendar.get(Calendar.MONTH) + 1;
            int curDay = calendar.get(Calendar.DAY_OF_MONTH);
            String[] date_split = rideDate.split("-");
            if (curYear == Integer.parseInt(date_split[2])) {
                if (curMonth == Integer.parseInt(date_split[1])) {
                    if (curDay == Integer.parseInt(date_split[0])) {
                        isFutureOrCurrentDate = true;
                    }
                }
            }
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinute = calendar.get(Calendar.MINUTE);
            String[] time_split = rideTime.split(":");
            if (time_split[1].toUpperCase().contains("PM")) {
                currentHour -= 12;
            }
            int selectedHour = Integer.parseInt(time_split[0]);
            if (selectedHour == 12) {
                selectedHour -= 12;
            }
            int selectedMinute = Integer.parseInt(time_split[1].substring(0, 2));
            if (isFutureOrCurrentDate) {
                if (selectedHour < currentHour) {
                    previousTimeSelected();
                    return;
                } else if (selectedHour == currentHour) {
                    if (selectedMinute < currentMinute) {
                        previousTimeSelected();
                        return;
                    } else {
                        oneHourErrorDisplay();
                        return;
                    }
                } else if ((currentHour + 1 == selectedHour && selectedMinute <= currentMinute)) {
                    oneHourErrorDisplay();
                    return;
                }
            }
            confirmationDialog();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {

        }
    };

    private String getSelectTvText() {
        String text;
        try {
            text = mSelectButton.getText().toString();
        } catch (NullPointerException e) {
            text = "";
        }
        return text;
    }

    private void openSuccessIntent() {
        Intent intent = new Intent(this, SuccessActivity.class);
        startActivity(intent);
    }

    private void confirmationDialog() {
        String location = "Location: ";
        String date = "Date: ";
        String time = "Time: ";
        String location_string = location + mCurrentAddress.getText();
        String date_string = date + rideDate;
        String time_string = time + rideTime;
        String display_text = location_string + "\n \n" + date_string + "\n \n" + time_string;
        SpannableStringBuilder text = new SpannableStringBuilder(display_text);
        final StyleSpan bold_style = new StyleSpan(Typeface.BOLD);
        text.setSpan(bold_style, 0, location.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        text.setSpan(bold_style, location_string.length() + 1, (location_string.length() + 1 + date.length()), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        text.setSpan(bold_style, (display_text.length() - time_string.length() + 1), display_text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        text.setSpan(new TypefaceSpan(this, "AvenirLT-55-Roman.ttf"), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        new MaterialDialog.Builder(this)
                .title(R.string.confirmation_details)
                .content(text)
                .cancelable(false)
                .positiveText(R.string.ok)
                .negativeText(R.string.change_details)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                        mSelectButton.setText(getResources().getString(R.string.select_ride_time));
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        mSelectButton.setText(getResources().getString(R.string.select_driver));
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mCustomMapFragment = (CustomMapFragment) getFragmentManager().findFragmentById(R.id.map);
            mCustomMapFragment.setOnDragListener(this);
            mMap = mCustomMapFragment.getMap();
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
//                    }
//                }, 500);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setPadding(0, 0, 70, 70);
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(0, 0))
//                .draggable(true));

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = getLastKnownLocation();
        new AsyncGetAddressTask().execute();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        LatLng newLatLng = mMap.getCameraPosition().target;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
//        mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title("Pin")
//                .snippet("My Location")
//                .draggable(true));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        fixMyLocationButton();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH) + 1;
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        rideDate = dayOfMonth + "-" + (++monthOfYear) + "-" + year;
        if (curYear < year) {
            isFutureOrCurrentDate = true;
        } else if (curMonth < monthOfYear) {
            isFutureOrCurrentDate = true;
        } else if (curDay < dayOfMonth) {
            isFutureOrCurrentDate = true;
        } else {
            isFutureOrCurrentDate = false;
        }
        closeDatePicker();
        openTimePicker();
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        if (!isFutureOrCurrentDate) {
            if (hourOfDay < currentHour) {
                previousTimeSelected();
                return;
            } else if (hourOfDay == currentHour) {
                if (minute < currentMinute) {
                    previousTimeSelected();
                    return;
                } else {
                    oneHourErrorDisplay();
                    return;
                }
            } else if ((currentHour + 1 == hourOfDay) && minute <= currentMinute) {
                oneHourErrorDisplay();
                return;
            }
        }
//        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
//        String minuteString = minute < 10 ? "0" + minute : "" + minute;

        String am_pm = "";
        String min;

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";

        if (datetime.get(Calendar.MINUTE) < 10) {
            min = "0" + datetime.get(Calendar.MINUTE);
        } else {
            min = String.valueOf(datetime.get(Calendar.MINUTE));
        }
        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0)
                ? "12" : datetime.get(Calendar.HOUR) + "";
        rideTime = strHrsToShow + ":" + min + " " + am_pm;

//        rideTime = hourString + ":" + minuteString;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
        mSelectButton.setText(getResources().getString(R.string.select_driver));
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        if (bestLocation != null) {
            latitude = bestLocation.getLatitude();
            longitude = bestLocation.getLongitude();
        } else {
            latitude = 12.9667;
            longitude = 77.5667;
        }
        return bestLocation;
    }

    private void fixMyLocationButton() {
        View myLocationButton = findViewById(R.id.mapsContainer).findViewById(Integer.parseInt("2"));
        if (myLocationButton != null) {
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(myLocationButton.getLayoutParams());
            marginParams.setMargins(0, 0, 0, 90);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
            layoutParams.addRule(RelativeLayout.ABOVE, mSelectButton.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            myLocationButton.setLayoutParams(layoutParams);
            myLocationButton.performClick();
        }
    }

    private void previousTimeSelected() {
//        openTimePicker();
        Toast.makeText(this, getResources().getString(R.string.previous_time_error_message), Toast.LENGTH_LONG).show();
    }

    private void handleSomethingWentWrong() {
        Toast.makeText(this, getResources().getString(R.string.connection_could_not_be_made), Toast.LENGTH_LONG).show();
    }

    private void oneHourErrorDisplay() {
        Toast.makeText(this, getResources().getString(R.string.one_hour_error), Toast.LENGTH_LONG).show();
    }

    private void postJSON() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, Utils.BOOKING_API_URL, giveJson(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismissDialogFragment(mProgressFragment);
                        Log.d("Response: ", response.toString());
                        try {
                            if (response.getString(Utils.SUCCESS_RESPONSE) != null) {
                                openSuccessIntent();
                            }
                        } catch (JSONException e) {
                            showBookingErrorMessage();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialogFragment(mProgressFragment);
                error.printStackTrace();
                Log.d("", "Error: " + error.toString());
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

    public JSONObject giveJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Utils.CUSTOMER_ID, mSharedPreferences.getString(Utils.CUSTOMER_ID, null));
            jsonObject.put(Utils.LATITUDE, latitude);
            jsonObject.put(Utils.LONGITUDE, longitude);
            jsonObject.put(Utils.DATE_OF_TRIP, rideDate);
            jsonObject.put(Utils.TIME_OF_TRIP, rideTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void initProgressDialog() {
        mProgressFragment = ProgressDialogFragment.createBuilder(this, this.getSupportFragmentManager())
                .setMessage(getResources().getString(R.string.finding_driver_message))
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
    public void onDrag(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Projection projection = (mMap != null && mMap
                    .getProjection() != null) ? mMap.getProjection()
                    : null;
            //
            if (projection != null) {
                LatLng centerLatLng = projection.fromScreenLocation(new Point(
                        centerX, centerY));
                latitude = centerLatLng.latitude;
                longitude = centerLatLng.longitude;
                new AsyncGetAddressTask().execute();
            }
        }
    }

    private class AsyncGetAddressTask extends AsyncTask<Void, Void, Void> {

        private String address;

        @Override
        protected Void doInBackground(Void... params) {
            Geocoder geocoder;
            List<Address> addresses;

            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null) {
                    address = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1);
                    String city = addresses.get(0).getLocality();
                    if (city != null) {
//                        if (city.contains("Mumbai") || city.contains("Bengaluru")) {
//                            isServiceAvailableInCity = true;
//                        } else {
//                            isServiceAvailableInCity = false;
//                        }
                    }
                }
            } catch (IOException e) {
                address = "Unknown location";
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setmCurrentAddress("Getting location");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setmCurrentAddress(address);
        }
    }

    private void showBookingErrorMessage() {
        Toast.makeText(this, getResources().getString(R.string.booking_error), Toast.LENGTH_LONG).show();
    }

    private void showNoServiceInCityError() {
        Toast.makeText(this, getResources().getString(R.string.city_error), Toast.LENGTH_LONG).show();
    }

    private void setmCurrentAddress(String address) {
        mCurrentAddress.setText(address);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(UtilsBundle.LAST_ADDRESS, mCurrentAddress.getText().toString());
    }
}
