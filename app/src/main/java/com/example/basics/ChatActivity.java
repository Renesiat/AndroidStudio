package com.example.basics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    private final String CHAT_URL = "https://gist.githubusercontent.com/samuglz/7260a132dbda41803e289506ccb5ce21/raw/dc1ada22a0bc4cbc77c20579f07392cbad9b268e/db.json" ;
    private String content;
    private LinearLayout chatContainer;
    private ArrayList<Message> messages;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.chatContainer = findViewById( R.id.chat_container );

        new Thread( this::loadUrl ).start();
    }

    private void loadUrl() {
        try(InputStream urlStream = new URL(this.CHAT_URL).openStream()){
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int len;
            while( ( len = urlStream.read( chunk ) ) != -1 ){
                bytes.write( chunk, 0, len );
            }
            this.content = new String( bytes.toByteArray(), StandardCharsets.UTF_8 );
            bytes.close();

            new Thread(this::parseContent).start();
        }
        catch (MalformedURLException ex){
            Log.d("loadUrl", "MalformedURLException: " + ex.getMessage());
        }
        catch (IOException ex){
            Log.d("loadUrl", "IOException: " + ex.getMessage());
        }
        catch (Exception ex){
            Log.d("loadUrl", "Exception: " + ex.getMessage());
        }
    }

    private void showContent(){
        LinearLayout ratesContainer = findViewById(R.id.chat_container);

        Drawable rateBgL = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rates_shape_l);
        Drawable rateBgR = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rates_shape_r);


        LinearLayout.LayoutParams rateLParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rateLParams.setMargins(10,7,10,7);

        LinearLayout.LayoutParams rateRParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rateRParams.setMargins(10,7,10,7);
        rateRParams.gravity = Gravity.END;

        for(Message message: this.messages){

            TextView tvRate = new TextView(this);

            if(new Random().nextBoolean() == true){
                tvRate.setTextSize(18);
                tvRate.setText(message.toString());
                tvRate.setBackground(rateBgL);
                tvRate.setPadding(15,5,15,5);
                tvRate.setLayoutParams(rateLParams);
                ratesContainer.addView(tvRate);
            }
            else{
                tvRate.setTextSize(18);
                tvRate.setText(message.toString());
                tvRate.setBackground(rateBgR);
                tvRate.setPadding(15,5,15,5);
                tvRate.setLayoutParams(rateRParams);
                ratesContainer.addView(tvRate);
            }


        }
    }

    private void showChatMessages() {
        TextView tvMessage = new TextView( this );
        tvMessage.setText( this.content );
        this.chatContainer.addView( tvMessage );
    }

    private void parseContent(){
        try {
            JSONObject object = new JSONObject (this.content);

            JSONArray array = object.getJSONArray("products");

            this.messages = new ArrayList<Message>();

            int len = array.length();

            for (int i = 0; i < len; ++i) {
                this.messages.add(new Message(array.getJSONObject(i)));
            }

        } catch (JSONException e) {
            Log.d("parseContent", "JSONException: " + e.getMessage());
            return;
        }

        runOnUiThread( this::showContent );
    }

    private static class Message {

        private int id;
        private String userName;
        private String text;

        public Message(JSONObject obj) throws JSONException {
            this.setId(obj.getInt("id"));
            this.setUserName(obj.getString("name"));
            this.setText(obj.getString("description"));
        }

        @NonNull
        @Override
        public String toString() {
            return "id: " + this.id + "\n"
                    + "User name: " + this.userName + "\n"
                    + "Text: \n\t" + this.text + "\n";
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }




}