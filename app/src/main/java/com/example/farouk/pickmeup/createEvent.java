package com.example.farouk.pickmeup;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;


public class createEvent extends AppCompatActivity {
    Spinner sportSpinner;
    Spinner skillSpinner;
    PlaceAutocompleteFragment location;
    CharSequence address;
    LatLng latLng;
    double lat;
    double lng;
    TimePicker eventTimePicker;
    EditText eventDateText;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sportSpinner = (Spinner) findViewById(R.id.sportSpinner);
        skillSpinner = (Spinner) findViewById(R.id.skillSpinner);
        eventTimePicker = (TimePicker) findViewById(R.id.eventTime);
        eventDateText = (EditText) findViewById(R.id.eventDateText);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        eventDateText.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.YEAR)));
        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                eventDateText.setText(String.valueOf(monthOfYear + 1) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year));
            }
        };
        eventDateText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                new DatePickerDialog(createEvent.this, R.style.DialogTheme, datePickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        location = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.autocompleteFragment);
        location.setOnPlaceSelectedListener(new PlaceSelectionListener(){ /* Gets address information from the place selected */
            public void onPlaceSelected(Place place){
                address = place.getAddress();
                latLng = place.getLatLng();
                lat = latLng.latitude;
                lng = latLng.longitude;
            }
            public void onError(Status status){

            }
        });
    }

    public void sendEventInfo(View view){ /* Sends event info to MainActivity */
        Intent intent = new Intent(this, MainActivity.class);
        if (address != null) {
            intent.putExtra("location", address.toString());
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error Notification");
            builder.setMessage("Location is a required field.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        intent.putExtra("sport", sportSpinner.getSelectedItem().toString());
        intent.putExtra("skill", skillSpinner.getSelectedItem().toString());
        intent.putExtra("date", eventDateText.getText().toString());
        if (Build.VERSION.SDK_INT >= 23) {
            intent.putExtra("hour", String.valueOf(eventTimePicker.getHour()));
            intent.putExtra("min", String.valueOf(eventTimePicker.getMinute()));
        }
        else{
            intent.putExtra("hour", String.valueOf(eventTimePicker.getCurrentHour()));
            intent.putExtra("min", String.valueOf(eventTimePicker.getCurrentMinute()));
        }
        intent.putExtra("lat", Double.toString(lat));
        intent.putExtra("lng", Double.toString(lng));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
