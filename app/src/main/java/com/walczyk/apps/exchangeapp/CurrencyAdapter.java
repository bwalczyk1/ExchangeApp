package com.walczyk.apps.exchangeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class CurrencyAdapter extends ArrayAdapter {
    private final ArrayList<String> _list;
    private final Context _context;
    private final int _resource;
    JSONObject currencyNames, rates;
    RequestQueue requestQueue;
    String _base;
    Double _value;

    public CurrencyAdapter(@NonNull Context context, int resource, @NonNull List objects, String base, Double value) {
        super(context, resource, objects);
        this._list = (ArrayList<String>) objects;
        this._context = context;
        this._resource = resource;
        this._base = base;
        this._value = value;

        requestQueue = Volley.newRequestQueue(_context);
        JsonObjectRequest symbolsRequest = new JsonObjectRequest(Request.Method.GET, "https://api.exchangerate.host/symbols", null, response -> {
            try {
                currencyNames = response.getJSONObject("symbols");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(_context, "Something went wrong ;(", Toast.LENGTH_SHORT).show());

        requestQueue.add(symbolsRequest);

        JsonObjectRequest ratesRequest = new JsonObjectRequest(Request.Method.GET, "https://api.exchangerate.host/latest?base="+_base, null, response -> {
            try {
                rates = response.getJSONObject("rates");
                notifyDataSetChanged();
            } catch (Exception ignored) {

            }
        }, error -> Toast.makeText(_context, "Something went wrong ;(", Toast.LENGTH_SHORT).show());

        requestQueue.add(ratesRequest);
        requestQueue.start();
    }

    @SuppressLint({"ViewHolder", "SetTextI18n", "DefaultLocale"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(_resource, null);

        View finalConvertView = convertView;

        try {
            TextView currencySymbol = (TextView) finalConvertView.findViewById(R.id.currency_symbol);
            currencySymbol.setText(_list.get(position));
            // currency_name
            TextView currencyNameV = (TextView) finalConvertView.findViewById(R.id.currency_name);
            String currencyName = currencyNames.getJSONObject(_list.get(position)).getString("description");
            currencyNameV.setText(currencyName);
            // rate
            TextView convertedValue = (TextView) finalConvertView.findViewById(R.id.converted_value);
            double rate = rates.getDouble(_list.get(position));
            convertedValue.setText(Currency.getInstance(_list.get(position)).getSymbol() + String.format("%.4f",rate * _value));
            TextView reverseConvertedValue = (TextView) finalConvertView.findViewById(R.id.reverse_converted_value);
            reverseConvertedValue.setText("1 " + _list.get(position) + " = " + String.format("%.4f",1/rate) + " " + _base);
            //image
            ImageView image = (ImageView) finalConvertView.findViewById(R.id.flag_image);
            String imageUrl = "https://flagcdn.com/64x48/" + _list.get(position).substring(0, _list.get(position).length()-1).toLowerCase() + ".png";
            Picasso.get().load(imageUrl).into(image);
        }catch (Exception ignored){}

        return finalConvertView;
    }
}