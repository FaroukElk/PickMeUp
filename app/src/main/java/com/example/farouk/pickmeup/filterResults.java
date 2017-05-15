package com.example.farouk.pickmeup;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class filterResults extends AppCompatActivity {

    private CheckBox soccerBox, basketballBox, hockeyBox, tennisBox, baseballBox, softballBox, lacrosseBox, volleyballBox, anyBox;
    private Spinner skillSpinner, distanceSpinner;
    private List<String> toRemove = new ArrayList<>();
    private EditText dateText;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        soccerBox = (CheckBox) findViewById(R.id.soccerBox);
        basketballBox = (CheckBox) findViewById(R.id.basketballBox);
        hockeyBox = (CheckBox) findViewById(R.id.hockeyBox);
        tennisBox = (CheckBox) findViewById(R.id.tennisBox);
        baseballBox = (CheckBox) findViewById(R.id.baseballBox);
        softballBox = (CheckBox) findViewById(R.id.softballBox);
        lacrosseBox = (CheckBox) findViewById(R.id.lacrosseBox);
        volleyballBox = (CheckBox) findViewById(R.id.volleyballBox);
        anyBox = (CheckBox) findViewById(R.id.anyBox);
        skillSpinner = (Spinner) findViewById(R.id.skillSpinner);
        distanceSpinner = (Spinner) findViewById(R.id.distanceSpinner);
        dateText = (EditText) findViewById(R.id.dateText);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateText.setText(String.valueOf(monthOfYear + 1) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year));
            }
        };
        dateText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                new DatePickerDialog(filterResults.this, R.style.DialogTheme, datePickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        setCheckListeners();
    }

    public void applyFilter(View view) { /* Determines which boxes are not checked and adds them to the "To be removed" list */
        if (!soccerBox.isChecked()) {
            toRemove.add("Soccer");
        }
        if (!basketballBox.isChecked()) {
            toRemove.add("Basketball");
        }
        if (!hockeyBox.isChecked()) {
            toRemove.add("Hockey");
        }
        if (!tennisBox.isChecked()) {
            toRemove.add("Tennis");
        }
        if (!baseballBox.isChecked()) {
            toRemove.add("Baseball");
        }
        if (!softballBox.isChecked()) {
            toRemove.add("Softball");
        }
        if (!lacrosseBox.isChecked()) {
            toRemove.add("Lacrosse");
        }
        if (!volleyballBox.isChecked()) {
            toRemove.add("Volleyball");
        }
        if (anyBox.isChecked() && toRemove.size() != 8){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error Notification");
            builder.setMessage("Cannot filter selected sports when the \"Any\" box is checked.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int id){
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        sendFilterInfo();
    }

    public void sendFilterInfo(){ /* Gets the filter information and sends it to MainActivity.java */
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("skillFilter", skillSpinner.getSelectedItem().toString().trim());
        intent.putExtra("distanceFilter", distanceSpinner.getSelectedItem().toString().trim());
        intent.putExtra("dateFilter", dateText.getText().toString().trim());
        if (toRemove.size() == 8) {
            intent.putExtra("sportFilter", "Any");
        }
        else{
            intent.putExtra("sportFilter", toRemove.toString());
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setCheckListeners(){ /* The "Any" box is checked by default and once another box is checked, the "Any" box becomes unchecked */
        soccerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                anyBox.setChecked(false);
            }
        });
        basketballBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                anyBox.setChecked(false);
            }
        });
        hockeyBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                anyBox.setChecked(false);
            }
        });
        tennisBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                anyBox.setChecked(false);
            }
        });
        baseballBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                anyBox.setChecked(false);
            }
        });
        softballBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                anyBox.setChecked(false);
            }
        });
        lacrosseBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                anyBox.setChecked(false);
            }
        });
        volleyballBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                anyBox.setChecked(false);
            }
        });
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
