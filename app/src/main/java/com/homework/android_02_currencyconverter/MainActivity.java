package com.homework.android_02_currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    final private String KEY_NAME = "name";
    final private String KEY_NUM_CODE = "numCode";
    final private String KEY_CHAR_CODE = "charCode";

    private TextView tvResult;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private EditText editValue;

    private CurrencyData data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new CurrencyData(this);

        tvResult = findViewById(R.id.tvResult);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        editValue = findViewById(R.id.editValue);


        editValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                CountCurrency(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ArrayList<Currency> currencies = data.GetCurrenciesData();
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for(int i = 0; i<currencies.size(); i++){
            HashMap<String, Object> item = new HashMap<>();
            item.put(KEY_NAME, currencies.get(i).name);
            item.put(KEY_NUM_CODE, currencies.get(i).numCode);
            item.put(KEY_CHAR_CODE, currencies.get(i).charCode);
            list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                R.layout.currency_activity,
                new String[]{KEY_NAME, KEY_NUM_CODE, KEY_CHAR_CODE},
                new int[] {R.id.tvName, R.id.tvNumCode, R.id.tvCharCode}
        );
        adapter.setDropDownViewResource(R.layout.currency_activity);
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String,Object> item = (HashMap<String,Object>) adapterView.getAdapter().getItem(i);
                Toast.makeText(MainActivity.this, item.get(KEY_NAME).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        fromSpinner.setAdapter(adapter);
        fromSpinner.setOnItemSelectedListener(listener);
        toSpinner.setAdapter(adapter);
        toSpinner.setSelection(data.getIndexByCharCode("USD"));
        toSpinner.setOnItemSelectedListener(listener);

        CountCurrency(editValue.getText().toString());
    }

    private void CountCurrency(String value){
        HashMap<String,Object> from = (HashMap<String,Object>)fromSpinner.getSelectedItem();
        HashMap<String,Object> to = (HashMap<String,Object>)toSpinner.getSelectedItem();
        Currency fromCurrency = data.getCurrecyByNumCode(from.get(KEY_NUM_CODE).toString());
        Currency toCurrency = data.getCurrecyByNumCode(to.get(KEY_NUM_CODE).toString());

        try {
            float val = Float.parseFloat(value);


            float c1 = fromCurrency.value / fromCurrency.nominal;
            c1 *= val;//uah

            float c2 = toCurrency.value / toCurrency.nominal;
            float res = c1 / c2;

            tvResult.setText(String.valueOf(res));
        }catch (Exception ex){
            tvResult.setText("Input value");
        }

    }

}