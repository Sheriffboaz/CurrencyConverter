package com.example.currencyconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private EditText editAmount;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private Button convertButton;
    private TextView resultText;
    private JSONObject currencyRates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        editAmount = findViewById(R.id.edit_amount);
        spinnerFrom = findViewById(R.id.spinner_from);
        spinnerTo = findViewById(R.id.spinner_to);
        convertButton = findViewById(R.id.convert_button);
        resultText = findViewById(R.id.result_text);

        // Load currency rates from JSON file
        loadCurrencyRates();

        // Set up spinner adapters
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Set up convert button click listener
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency();
            }
        });
    }

    private void loadCurrencyRates() {
        try {
            // Read currency rates JSON from resources
            String json = getResources().getString(R.string.currency_rates_json);

            // Parse JSON data
            JSONObject jsonRates = new JSONObject(json);
            currencyRates = jsonRates.getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void convertCurrency() {
        String fromCurrency = spinnerFrom.getSelectedItem().toString();
        String toCurrency = spinnerTo.getSelectedItem().toString();
        double amount = Double.parseDouble(editAmount.getText().toString());

        try {
            // Get exchange rates for the selected currencies
            double fromRate = currencyRates.getDouble(fromCurrency);
            double toRate = currencyRates.getDouble(toCurrency);

            // Convert the amount to the target currency
            double convertedAmount = (amount / fromRate) * toRate;

            // Check if the result is NaN or infinite
            if (Double.isNaN(convertedAmount) || Double.isInfinite(convertedAmount)) {
                resultText.setText("No result");
            } else {
                // Format the result with 2 decimal places
                BigDecimal result = new BigDecimal(convertedAmount)
                        .setScale(2, RoundingMode.HALF_UP);

                // Display the result
                resultText.setText(getString(R.string.result_text_format, String.valueOf(result)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
