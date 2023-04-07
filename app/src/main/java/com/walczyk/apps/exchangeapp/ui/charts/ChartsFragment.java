package com.walczyk.apps.exchangeapp.ui.charts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.BaseWithMarkers;
import com.anychart.graphics.vector.Stroke;
import com.walczyk.apps.exchangeapp.VolleyCallBack;
import com.walczyk.apps.exchangeapp.databinding.FragmentChartsBinding;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ChartsFragment extends Fragment {

    private FragmentChartsBinding binding;
    private JSONObject historicalData;
    private RequestQueue requestQueue;
    private Calendar calendar;
    private SimpleDateFormat formatter;
    private String base,currency, duration;
    private ArrayList<String> currencies;
    private Cartesian cartesian;
    private BaseWithMarkers series;
    boolean isChartColumn;

    @SuppressLint("SimpleDateFormat")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChartsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        base = "USD";
        currency = "USD";
        duration = "1W";
        isChartColumn = false;

        requestQueue = Volley.newRequestQueue(requireActivity());
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        formatter = new SimpleDateFormat("yyyy-MM-dd");

        currencies = new ArrayList<>();
        currencies.add("USD");
        currencies.add("EUR");
        currencies.add("JPY");
        currencies.add("GBP");
        currencies.add("AUD");
        currencies.add("CAD");
        currencies.add("CHF");
        currencies.add("CNY");
        currencies.add("HKD");
        currencies.add("NZD");
        currencies.add("SEK");
        currencies.add("PLN");

        Spinner baseSpinner = binding.baseSpinner;
        baseSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, getCurrenciesWithFlags(currencies)));
        baseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!Objects.equals(base, currencies.get(position))){
                    base = currencies.get(position);
                    getHistoricalData(() -> updateChart());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner currencySpinner = binding.currencySpinner;
        currencySpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, getCurrenciesWithFlags(currencies)));
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!Objects.equals(currency, currencies.get(position))){
                    currency = currencies.get(position);
                    getHistoricalData(() -> updateChart());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btn1w.setOnClickListener(v -> {
            Button btn = (Button) v;
            if(!Objects.equals(duration, btn.getText().toString())){
                duration = btn.getText().toString();
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -7);
                getHistoricalData(this::updateChart);
            }
        });
        binding.btn2w.setOnClickListener(v -> {
            Button btn = (Button) v;
            if(!Objects.equals(duration, btn.getText().toString())){
                duration = btn.getText().toString();
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -14);
                getHistoricalData(this::updateChart);
            }
        });
        binding.btn1m.setOnClickListener(v -> {
            Button btn = (Button) v;
            if(!Objects.equals(duration, btn.getText().toString())){
                duration = btn.getText().toString();
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -30);
                getHistoricalData(this::updateChart);
            }
        });
        binding.btn3m.setOnClickListener(v -> {
            Button btn = (Button) v;
            if(!Objects.equals(duration, btn.getText().toString())){
                duration = btn.getText().toString();
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -92);
                getHistoricalData(this::updateChart);
            }
        });
        binding.btn6m.setOnClickListener(v -> {
            Button btn = (Button) v;
            if(!Objects.equals(duration, btn.getText().toString())){
                duration = btn.getText().toString();
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -183);
                getHistoricalData(this::updateChart);
            }
        });
        binding.btn1y.setOnClickListener(v -> {
            Button btn = (Button) v;
            if(!Objects.equals(duration, btn.getText().toString())){
                duration = btn.getText().toString();
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -365);
                getHistoricalData(this::updateChart);
            }
        });

        binding.chartTypeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isChartColumn = isChecked;
            updateChart();
        });

        getHistoricalData(() -> new android.os.Handler(Looper.getMainLooper()).postDelayed(
                this::generateChart,
                1000));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getHistoricalData(final VolleyCallBack callBack){
        String startDate = formatter.format(calendar.getTime());

        JsonObjectRequest historicalDataRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://api.exchangerate.host/timeseries?start_date=" + startDate +
                        "&end_date=" + formatter.format(new Date()) +
                        "&symbols=" + currency +
                        "&base=" + base, null, response -> {
                            try {
                                historicalData = response.getJSONObject("rates");
                                callBack.onSuccess();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, error -> Toast.makeText(getActivity(), "Something went wrong ;(", Toast.LENGTH_SHORT).show());

        requestQueue.add(historicalDataRequest);
    }

    public void generateChart(){
        List<DataEntry> seriesData = new ArrayList<>();
        for (Iterator<String> it = historicalData.keys(); it.hasNext(); ) {
            try {
                String date = it.next();
                JSONObject oneDayData = historicalData.getJSONObject(date);
                if(oneDayData.has(currency))
                    seriesData.add(new ValueDataEntry(date, oneDayData.getDouble(currency)));
            }catch(Exception e){
                Toast.makeText(getActivity(), "Something went wrong ;(", Toast.LENGTH_SHORT).show();
            }
        }
        if(seriesData.size() == 0) {
            return;
        }

        cartesian = AnyChart.line();
        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair().yLabel(true).yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.crosshair().xLabel(true).xStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.autoRedraw();

        cartesian.title("");
        cartesian.yAxis(0).title("");

        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        series = cartesian.line(seriesData);
        series.name(currency);
        series.tooltip().enabled(false);

        cartesian.legend().enabled(false);

        binding.lineChartView.setChart(cartesian);
    }

    public void updateChart(){
        if(cartesian == null)
            return;
        if(cartesian.getSeries(0) != null)
            cartesian.removeAllSeries();

        List<DataEntry> seriesData = new ArrayList<>();
        for (Iterator<String> it = historicalData.keys(); it.hasNext(); ) {
            try {
                String date = it.next();
                JSONObject oneDayData = historicalData.getJSONObject(date);
                if(oneDayData.has(currency))
                    seriesData.add(new ValueDataEntry(date, oneDayData.getDouble(currency)));
            }catch(Exception e){
                Toast.makeText(getActivity(), "Something went wrong ;(", Toast.LENGTH_SHORT).show();
            }
        }

        if(isChartColumn)
            series = cartesian.column(seriesData);
        else
            series = cartesian.line(seriesData);

        series.name(currency);
        series.data(seriesData);

    }

    public String countryCodeToEmoji(String code) {
        code = code.substring(0, 2);
        int OFFSET = 127397;

        code = code.toUpperCase();

        StringBuilder emojiStr = new StringBuilder();

        for (int i = 0; i < code.length(); i++) {
            emojiStr.appendCodePoint(code.charAt(i) + OFFSET);
        }

        return emojiStr.toString();
    }

    public ArrayList<String> getCurrenciesWithFlags(ArrayList<String> currencies){
        ArrayList<String> currenciesWithFlags = new ArrayList<>();
        for (String currency:
                currencies) {
            currenciesWithFlags.add(countryCodeToEmoji(currency) + " " + currency);
        }
        return currenciesWithFlags;
    }
}