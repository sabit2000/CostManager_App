package com.cscorner.cse_489_sabit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportlistActivity extends AppCompatActivity {

    private ListView lvReportlist;
    private Button btn_back, btn_add_new, btnSearch;
    private TextView tvTotalCost;
    private ArrayList<Item> items = new ArrayList<>();
    private CustomItemAdapter adapter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report_list);

        lvReportlist = findViewById(R.id.lvReportlist);
        tvTotalCost = findViewById(R.id.tvTotalCost);
        btn_back = findViewById(R.id.btn_back);
        btn_add_new = findViewById(R.id.btn_add_new);
        btnSearch = findViewById(R.id.btnSearch);
        adapter = new CustomItemAdapter(this, items);
        lvReportlist.setAdapter(adapter);

        lvReportlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = items.get(position);
                Intent intent = new Intent(ReportlistActivity.this, Additem.class);
                intent.putExtra("ID", selectedItem.id);
                intent.putExtra("ITEM-NAME", selectedItem.itemName);
                intent.putExtra("DATE", selectedItem.date);
                intent.putExtra("COST", selectedItem.cost);
                startActivity(intent);
            }
        });

        // Long item click listener to delete
        lvReportlist.setOnItemLongClickListener((parent, view, position, id) -> {
            Item selectedItem = items.get(position);

            // Show confirmation dialog before deleting
            new AlertDialog.Builder(ReportlistActivity.this)
                    .setMessage("Are you sure you want to delete this item?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete from local database
                        EventDB db = new EventDB (ReportlistActivity.this);
                        db.deleteItem(selectedItem.id);

                        // Remove the item from the list and update the UI
                        items.remove(position);
                        adapter.notifyDataSetChanged();

                        // Update the total cost
                        updateTotalCost();

                        // Delete from remote server
                        deleteFromRemoteServer(selectedItem.id);

                        // Show a Toast message confirming deletion
                        Toast.makeText(ReportlistActivity.this, "Item deleted from local and server", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss(); // Do nothing if No is clicked
                    })
                    .show();

            return true; // Indicates that the long click event was handled
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReportlistActivity.this, Login.class);
                startActivity(i);
            }
        });

        btn_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReportlistActivity.this, Additem.class);
                startActivity(i);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportlistActivity.this);
                builder.setTitle("Search");

                final android.widget.EditText input = new android.widget.EditText(ReportlistActivity.this);
                input.setHint("Enter item name or date (yyyy-MM-dd)");
                builder.setView(input);

                builder.setPositiveButton("Search", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        String searchQuery = input.getText().toString().trim();
                        loadLocalData(searchQuery);
                    }
                });

                builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        loadLocalData("");
        loadRemoteData();
    }

    private void loadLocalData(String searchBy) {
        items.clear();
        double totalCost = 0;
        EventDB db = new EventDB(this);
        String q = "SELECT * FROM items";
        if (!searchBy.isEmpty()) {
            try {
                long dateInMilliSecond = getDateInMilliSecond(searchBy);
                q += " WHERE itemName LIKE '%" + searchBy + "%' OR date = " + dateInMilliSecond;
            } catch (Exception e) {
                q += " WHERE itemName LIKE '%" + searchBy + "%'";
                Log.e("ReportlistActivity", "Failed to parse date: " + e.getMessage());
            }
        }
        Cursor c = db.selectItems(q);
        while (c.moveToNext()) {
            String id = c.getString(0);
            String itemName = c.getString(1);
            long date = c.getLong(2);
            double cost = c.getDouble(3);
            Item i = new Item(id, itemName, cost, date);
            items.add(i);
            totalCost += cost;
        }
        adapter.notifyDataSetChanged();
        tvTotalCost.setText(String.format("Total Cost: %.2f", totalCost));
    }

    private void loadRemoteData() {
        String keys[] = {"action", "sid", "semester"};
        String values[] = {"restore", "2020-1-60-046", "2024-3"};
        httpRequest(keys, values);
    }

    private void httpRequest(final String keys[], final String values[]) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params = new ArrayList<>();
                for (int i = 0; i < keys.length; i++) {
                    params.add(new BasicNameValuePair(keys[i], values[i]));
                }
                String url = "https://www.muthosoft.com/univ/cse489/index.php";
                try {
                    String data = RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
                    return data;
                } catch (Exception e) {
                    Log.e("ReportlistActivity", "HTTP Request failed: " + e.getMessage(), e);
                }
                return null;
            }

            protected void onPostExecute(String data) {
                if (data != null) {
                    updateLocalDBByServerData(data);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to load remote data.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void updateLocalDBByServerData(String data) {
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("classes")) {
                items.clear();
                double totalCost = 0;
                JSONArray ja = jo.getJSONArray("classes");
                EventDB db = new EventDB(ReportlistActivity.this);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject item = ja.getJSONObject(i);
                    String id = item.getString("id");
                    String itemName = item.getString("itemName");
                    double cost = item.getDouble("cost");
                    long date = item.getLong("date");
                    Item item1 = new Item(id, itemName, cost, date);
                    items.add(item1);
                    totalCost += cost;
                    db.updateItem(id, itemName, date, cost);
                }
                db.close();
                adapter.notifyDataSetChanged();
                tvTotalCost.setText(String.format("Total Cost: %.2f", totalCost));
            }
        } catch (Exception e) {
            Log.e("ReportlistActivity", "Error parsing server data: " + e.getMessage(), e);
        }
    }




    private void deleteFromRemoteServer(String itemId) {
        String keys[] = {"action", "sid", "semester", "id"};
        String values[] = {"remove", "2020-1-60-046", "2024-3", itemId};
        httpRequest(keys, values);
    }

    private void updateTotalCost() {
        double totalCost = 0;
        for (Item item : items) {
            totalCost += item.cost;
        }
        tvTotalCost.setText(String.format("%.2f", totalCost));
    }



    private long getDateInMilliSecond(String date) {
        try {
            String[] dateParts = date.split("-");
            if (dateParts.length == 3) {
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]) - 1;
                int day = Integer.parseInt(dateParts[2]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return calendar.getTimeInMillis();
            }
        } catch (Exception e) {
            Log.e("ReportlistActivity", "Invalid date format: " + date, e);
        }
        return 0;
    }
}