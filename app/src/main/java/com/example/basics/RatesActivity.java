package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RatesActivity extends AppCompatActivity {

    private final String nbuApiUrl = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private TextView tvContent;
    private String content;
    private List<Rate> rates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        tvContent = findViewById(R.id.tv_rates_content);

        new Thread(this::loadUrl).start();
    }

    private void loadUrl(){
        try(InputStream urlStream = new URL(nbuApiUrl).openStream()){
            StringBuilder sb = new StringBuilder();
            int sym;
            while((sym = urlStream.read()) != -1){
                sb.append((char) sym);
            }

            content = new String(sb.toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            new Thread(this::parseContent).start();
        }
        catch (MalformedURLException ex){
            Log.d("loadUrl", "MalformedURLException: " + ex.getMessage());
        }
        catch (IOException ex){
            Log.d("loadUrl", "IOException: " + ex.getMessage());
        }
    }

    private void showContent(){
        ratesToString();
        tvContent.setText(content);
    }

    private void ratesToString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Change Date: " + rates.get(0).exchangeDate + "\n\n");

        for (Rate r: rates) {
            sb.append("r030: " + r.r030 + "\n")
                    .append("\ttxt: " + r.txt + "\n")
                    .append("\trate: " + r.rate + "\n")
                    .append("\tcc: " + r.cc + "\n\n");
        }

        this.content = sb.toString();
    }

    private void parseContent(){
        try {
            JSONArray array = new JSONArray(content);

            this.rates = new ArrayList<Rate>();

            int len = array.length();

            for (int i = 0; i < len; ++i) {
                rates.add(new Rate(array.getJSONObject(i)));
            }

        } catch (JSONException e) {
            Log.d("parseContent", "JSONException: " + e.getMessage());
            return;
        }

        runOnUiThread( this::showContent);
    }

    static class Rate{
        private int r030;
        private String txt;
        private double rate;
        private String cc;
        private String exchangeDate;



        public Rate(JSONObject obj) throws JSONException {
            this.setR030(obj.getInt("r030"));
            this.setTxt(obj.getString("txt"));
            this.setRate(obj.getDouble("rate"));
            this.setCc(obj.getString("cc"));
            this.setExchangeDate(obj.getString("exchangedate"));
        }

        public int getR030() {
            return r030;
        }
        public void setR030(int r030) {
            this.r030 = r030;
        }

        public String getTxt() {
            return txt;
        }
        public void setTxt(String txt) {
            this.txt = txt;
        }

        public double getRate() {
            return rate;
        }
        public void setRate(double rate) {
            this.rate = rate;
        }

        public String getCc() {
            return cc;
        }

        public void setCc(String cc) {
            this.cc = cc;
        }

        public String getExchangeDate() {
            return exchangeDate;
        }
        public void setExchangeDate(String exchangeDate) {
            this.exchangeDate = exchangeDate;
        }


    }
}
