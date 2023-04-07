package com.walczyk.apps.exchangeapp.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.walczyk.apps.exchangeapp.CurrencyAdapter;
import com.walczyk.apps.exchangeapp.R;
import com.walczyk.apps.exchangeapp.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    CurrencyAdapter adapter;
    ArrayList<String> currencies, selectedCurrencies;
    String base;
    Double value;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        base = "USD";
        value = 1.0;

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

        Spinner currencySpinner = binding.spinner;
        currencySpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, getCurrenciesWithFlags(currencies)));
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                base = currencies.get(position);
                adapter = new CurrencyAdapter(
                        requireActivity(),
                        R.layout.layout_currency,
                        selectedCurrencies,
                        base,
                        value
                );

                binding.currencyList.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectedCurrencies = new ArrayList<>();

        adapter = new CurrencyAdapter(
                requireActivity(),
                R.layout.layout_currency,
                selectedCurrencies,
                base,
                value
        );

        binding.currencyList.setAdapter(adapter);

        EditText value_edit = binding.value;
        value_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("") || s.toString().equals("."))
                    return;
                value = Double.valueOf(s.toString());
                adapter = new CurrencyAdapter(
                        requireActivity(),
                        R.layout.layout_currency,
                        selectedCurrencies,
                        base,
                        value
                );

                binding.currencyList.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button addBtn = binding.addBtn;
        addBtn.setOnClickListener(v -> {
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
            b.setTitle("Add currency");
            b.setItems(getCurrenciesWithFlags(currencies).toArray(new CharSequence[currencies.size()]), (dialog, which) -> {
                dialog.dismiss();
                if(selectedCurrencies.contains(currencies.get(which))){
                    Toast.makeText(getActivity(), "Currency already selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedCurrencies.add(currencies.get(which));
                adapter = new CurrencyAdapter(
                        requireActivity(),
                        R.layout.layout_currency,
                        selectedCurrencies,
                        base,
                        value
                );

                binding.currencyList.setAdapter(adapter);
            });
            b.show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new CurrencyAdapter(
                requireActivity(),
                R.layout.layout_currency,
                selectedCurrencies,
                base,
                value
        );

        binding.currencyList.setAdapter(adapter);
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