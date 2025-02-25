package com.cscorner.cse_489_sabit;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;

public class CustomItemAdapter extends ArrayAdapter<Item> {
    private LayoutInflater inflater;
    private ArrayList<Item> records;
    public CustomItemAdapter(Context context, ArrayList<Item> records){
        super(context, -1, records);
        this.records = records;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public View getView(int position, View convertView, ViewGroup parent){
        View template = inflater.inflate(R.layout.activity_row_item, parent, false);
        TextView tvSN = template.findViewById(R.id.tvSN);
        TextView tvItemName = template.findViewById(R.id.tvItemName);
        TextView tvDate = template.findViewById(R.id.tvDate);
        TextView tvCost = template.findViewById(R.id.tvCost);

        tvSN.setText(String.valueOf(position+1));
        tvItemName.setText(records.get(position).itemName);
        tvDate.setText(getFormattedDate(records.get(position).date));
        tvCost.setText(String.valueOf(records.get(position).cost));

        return template;
    }
    private String getFormattedDate(long milliseconds) {

        Date date = new Date(milliseconds);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


        return formatter.format(date);
    }
}