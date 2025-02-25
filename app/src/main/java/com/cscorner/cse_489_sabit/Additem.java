package com.cscorner.cse_489_sabit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Additem extends AppCompatActivity {

    private EditText etItemName, etCost, etDate;
    private Button btnCancel, btnSave;
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        etItemName = findViewById(R.id.etItemName);
        etCost = findViewById(R.id.etCost);
        etDate = findViewById(R.id.etDate);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Additem.this, ReportlistActivity.class);
                startActivity(in);
            }
        });
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Intent i = getIntent();
        if (i != null && i.hasExtra("ID")) {
            id = i.getStringExtra("ID");
            String itemName = i.getStringExtra("ITEM-NAME");
            long dateInMilliSeconds = i.getLongExtra("DATE", 0);
            double cost = i.getDoubleExtra("COST", 0);


            String date = convertMillisecondsToDateString(dateInMilliSeconds);

            etItemName.setText(itemName);
            etCost.setText(String.valueOf(cost));
            etDate.setText(date);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String itemName = etItemName.getText().toString().trim();
                String cost = etCost.getText().toString().trim();
                String date = etDate.getText().toString().trim();


                if (!validateData(itemName, cost, date)) {
                    return;
                }


                double costValue = Double.parseDouble(cost);
                long dateValue = parseDateToMilliseconds(date);

                Calendar currentCal = Calendar.getInstance();
                long currentTime = currentCal.getTimeInMillis();


                EventDB db = new EventDB(Additem.this);
                if (id.isEmpty()) {
                    id= itemName + ":" + currentTime;
                    db.insertItem(id, itemName, dateValue, costValue);
                } else {
                    db.updateItem(id, itemName, dateValue, costValue);
                }
                db.close();

                // store record to remote database
                System.out.println("Storing to remote db...");
                String keys[] = {"action", "sid", "semester", "id", "itemName", "cost", "date"};
                String values[] = {"backup", "2020-1-60-046", "2024-3", id, itemName, String.valueOf(costValue), String.valueOf(dateValue)};
                httpRequest(keys, values);
                finish();
            }


        });
    }





    private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://www.muthosoft.com/univ/cse489/index.php";
                try {
                    String data = RemoteAccess.getInstance().makeHttpRequest(url,"POST", params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Additem.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Update etDate with selected date in yyyy-MM-dd format
                        selectedMonth += 1; // Calendar months are 0-based
                        String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay);
                        etDate.setText(formattedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }


    private boolean validateData(String itemName, String cost, String date) {
        if (itemName.isEmpty()) {
            etItemName.setError("Item name cannot be empty");
            return false;
        }

        if (cost.isEmpty()) {
            etCost.setError("Cost price cannot be empty");
            return false;
        }

        try {
            double costValue = Double.parseDouble(cost);
            if (costValue <= 0) {
                etCost.setError("Cost price must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            etCost.setError("Invalid cost price format");
            return false;
        }

        if (date.isEmpty()) {
            etDate.setError("Date cannot be empty");
            return false;
        }

        // Validate date format (yyyy-MM-dd)
        String[] dateParts = date.split("-");
        if (dateParts.length != 3) {
            etDate.setError("Invalid date format. Use yyyy-MM-dd");
            return false;
        }

        int year, month, day;
        try {
            year = Integer.parseInt(dateParts[0]);
            month = Integer.parseInt(dateParts[1]) - 1;
            day = Integer.parseInt(dateParts[2]);
        } catch (NumberFormatException e) {
            etDate.setError("Invalid date format. Use yyyy-MM-dd");
            return false;
        }

        Calendar enteredDate = Calendar.getInstance();
        enteredDate.setLenient(false);
        try {
            enteredDate.set(year, month, day);
            enteredDate.getTime();
        } catch (Exception e) {
            etDate.setError("Invalid date");
            return false;
        }

        return true;
    }


    private String convertMillisecondsToDateString(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(milliseconds));
    }


    private long parseDateToMilliseconds(String date) {
        String[] dateParts = date.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int day = Integer.parseInt(dateParts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }



}
