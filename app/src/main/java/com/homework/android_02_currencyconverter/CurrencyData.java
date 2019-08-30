package com.homework.android_02_currencyconverter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class CurrencyData {
    final static private String urlPath = "http://pf-soft.net/service/currency/";

    private ArrayList<Currency> currencies = new ArrayList<>();
    private Document document;
    private MainActivity mainActivity;



    public CurrencyData(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        try {
            document = new GetDOMTask().execute().get();
            getCurrencies();
        }catch (Exception ex){
            Log.println(Log.ERROR, "Exception", ex.getMessage());
        }
    }

    public ArrayList<Currency> GetCurrenciesData(){
        return currencies;
    }

    public int getIndexByCharCode(String charCode){
        int i = 0;
        for (Currency currency: currencies){
            if(currency.charCode.equals(charCode)){
                return i;
            }
            i++;
        }
        return 0;
    }

    public Currency getCurrecyByNumCode(String numCode){
        for (Currency currency: currencies){
            if(currency.numCode.equals(numCode)){
                return currency;
            }
        }
        return null;
    }

    public void getCurrencies(){
        Currency ukr = new Currency();
        ukr.name = "українська гривна";
        ukr.value = 1.0f;
        ukr.nominal = 1;
        ukr.charCode = "UAH";
        ukr.numCode = "000";
        currencies.add(ukr);
        Element root  = document.getDocumentElement();
        NodeList children = root.getChildNodes();
        for (int i = 0; i<children.getLength(); i++) {
            Node valute = children.item(i);
            NodeList data = valute.getChildNodes();
            Currency currency = new Currency();
            currency.numCode = getValue(data.item(0));
            currency.charCode = getValue(data.item(1));
            currency.nominal = Integer.parseInt(getValue(data.item(2)));
            currency.name = getValue(data.item(3));
            currency.value = Float.parseFloat(getValue(data.item(4)));
            currencies.add(currency);
        }
    }

    private String getValue(Node node){
        return node.getChildNodes().item(0).getNodeValue();
    }

    private Document getCurrencyData() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new URL(urlPath).openStream());
    }

    private class GetDOMTask extends AsyncTask<String, Void, Document> {
        private ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(mainActivity, "", "Loading", true,
                    false); // Create and show Progress dialog
        }

        @Override
        protected Document doInBackground(String... urls) {

            try {
                return getCurrencyData();
            } catch (Exception e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(Document result) {
            pd.dismiss();
            document = result;
            getCurrencies();
        }
    }
}



