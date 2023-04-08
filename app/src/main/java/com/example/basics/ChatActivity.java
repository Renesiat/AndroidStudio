package com.example.basics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.media.MediaPlayer;
import android.os.Build;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.ScrollView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private final String CHAT_URL = "https://express-messages-api.onrender.com";
    private final String CHANNEL_ID = "CHAT_NOTIFY";
    private String content;
    private List<ChatMessage> chatMessages;
    private ChatMessage userMessage;
    private LinearLayout chatContainer;
    private EditText etUserName;
    private EditText etUserMessage;
    private ScrollView svContainer;

    private Handler handler;
    private MediaPlayer incomingMessagePlayer;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );

        chatMessages = new ArrayList<ChatMessage>();

        handler = new Handler();
        handler.post( this::updateChat );

        //incomingMessagePlayer = MediaPlayer.create( this, R.raw.sound_1 );
        new Thread( this::loadUrl ).start();

        chatContainer = findViewById( R.id.chat_container );
        etUserName = findViewById( R.id.et_chat_user_name );
        etUserMessage = findViewById( R.id.et_chat_message );
        svContainer = findViewById(R.id.sv_container);
        findViewById(R.id.btn_chat_send).setOnClickListener( this::sendButtonClick );
    }
    private void sendButtonClick( View view ){

        String author = etUserName.getText().toString();
        if( author.isEmpty() ) {
            Toast.makeText( this, "Enter author name", Toast.LENGTH_SHORT ).show();
            etUserName.requestFocus();
            return;
        }

        String messageText = etUserMessage.getText().toString();
        if( messageText.isEmpty() ){
            Toast.makeText( this, "Enter message text", Toast.LENGTH_SHORT ).show();
            etUserMessage.requestFocus();
            return;
        }
        etUserMessage.setText("");

        userMessage = new ChatMessage();
        userMessage.setAuthor( author );
        userMessage.setText( messageText );

        new Thread( this::postUserMessage ).start();
    }
    private void updateChat(){
        new Thread( this::loadUrl ).start();
        handler.postDelayed( this::updateChat, 3000 );
    }
    private void postUserMessage() {
        try {

            URL chatUrl = new URL( CHAT_URL );
            HttpURLConnection connection = ( HttpURLConnection ) chatUrl.openConnection();
            connection.setDoOutput( true );
            connection.setDoInput( true ) ;
            connection.setRequestMethod( "POST" );
            connection.setRequestProperty( "Content-Type", "application/json" );
            connection.setRequestProperty( "Accept", "*/*" );
            connection.setChunkedStreamingMode( 0 );

            OutputStream body = connection.getOutputStream();
            body.write( userMessage.toJsonString().getBytes() );
            body.flush();
            body.close();

            int responseCode = connection.getResponseCode();
            if( responseCode >= 400 ) {
                Log.d( "postUserMessage", "Request fails with code " + responseCode );
                return;
            }

            InputStream response = connection.getInputStream();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int len;
            while( ( len = response.read( chunk ) ) != -1 ){
                bytes.write( chunk, 0, len );
            }

            String responseBody = new String( bytes.toByteArray(), StandardCharsets.UTF_8 );
            Log.i( "postUserMessage", responseBody );


            bytes.close();
            response.close();
            connection.disconnect();

            new Thread( this::loadUrl ).start();

        }catch ( Exception ex ) {

            Log.d( "postUserMessage", ex.getMessage() );

        }
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

    private void showContent1(){
        LinearLayout ratesContainer = findViewById(R.id.chat_container);

        Drawable otherBg = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rates_shape_l);
        Drawable myBg = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rates_shape_r);

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

        for(ChatMessage message: this.chatMessages){


            TextView tvRate = new TextView(this);

            if(message.getAuthor() == this.etUserName.getText().toString()){
                tvRate.setTextSize(18);
                tvRate.setText( message.getAuthor() + ": " + message.getText() );
                tvRate.setBackground(otherBg);
                tvRate.setPadding(15,5,15,5);
                tvRate.setLayoutParams(rateLParams);
                ratesContainer.addView(tvRate);
            }
            else{
                tvRate.setTextSize(18);
                tvRate.setText( message.getAuthor() + ": " + message.getText() );
                tvRate.setBackground(myBg);
                tvRate.setPadding(15,5,15,5);
                tvRate.setLayoutParams(rateRParams);
                ratesContainer.addView(tvRate);
            }


        }
    }

    private void showChatMessages() {
        String author = etUserName.getText().toString();

        Drawable otherBg = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rates_shape_l);
        Drawable myBg = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.rates_shape_r);

        LinearLayout.LayoutParams otherParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        otherParams.setMargins(10,7,10,7);
        LinearLayout.LayoutParams myParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        myParams.setMargins(10,7,10,7);
        myParams.gravity = Gravity.END;
        boolean needScroll = false;
        for( ChatMessage chatMessage : chatMessages ) {
            if (chatMessage.getView() != null) continue;
            TextView tvMessage = new TextView( this );
            tvMessage.setText(String.format( "[%s]\n%s",
                    chatMessage.getAuthor(),
                    chatMessage.getText()));
            tvMessage.setTextSize(18);
            tvMessage.setPadding(15,5,15,5);
            tvMessage.setLayoutParams(
                    author.equals(chatMessage.getAuthor())
                            ? myParams : otherParams
            );
            tvMessage.setBackground(
                    author.equals(chatMessage.getAuthor())
                            ? myBg : otherBg
            );
            chatContainer.addView(tvMessage);
            chatMessage.setView(tvMessage);
            tvMessage.setTag(chatMessage);
            needScroll = true;
        }

        if (needScroll) {
            this.svContainer.post(() -> this.svContainer.fullScroll(View.FOCUS_DOWN));
            this.incomingMessagePlayer.start();
        }
    }

        private void parseContent(){
        try {
            JSONObject object = new JSONObject (this.content);

            JSONArray array = object.getJSONArray("data");

            chatMessages = new ArrayList<ChatMessage>();


            int len = array.length();

            for (int i = 0; i < len; ++i) {
                ChatMessage tmp = new ChatMessage(array.getJSONObject(i));
                if (this.chatMessages.stream().noneMatch(cm -> cm.getId().equals(tmp.getId())))
                    this.chatMessages.add(tmp);
            }

        } catch (JSONException e) {
            Log.d("parseContent", "JSONException: " + e.getMessage());
            return;
        }

            runOnUiThread(this::showChatMessages);
        }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(this.CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(ChatActivity.this, this.CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle("Chat")
                        .setContentText("New message in chat")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(ChatActivity.this);

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        ChatActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        10002
                );
                return;
            }
        }
        notificationManagerCompat.notify(1002, notification);
    }

    public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        if( requestCode == 10002) {

        }
    }

    private static class ChatMessage{
        private UUID id;
        private String author;
        private String text;
        private Date moment;
        private UUID idReply;
        private View view;
        private String replyPreview;
        private static final SimpleDateFormat scanFormat =
                new SimpleDateFormat( "MMM d, yyyy KK:mm:ss a", Locale.US );
        public ChatMessage(){
            }
            public ChatMessage( JSONObject object ) throws JSONException {
            setId(UUID.fromString( object.getString( "id" ) ) );
            setAuthor( object.getString( "author" ) );
            setText( object.getString( "txt") );

        }
        public String toJsonString(){
            StringBuilder sb = new StringBuilder();
            sb.append( String.format( "{\"author\":\"%s\", \"txt\":\"%s\"", getAuthor(), getText() ) );
            if( idReply != null )
                sb.append( String.format( ", \"idReply\":\"%s\"", getIdReply() ) );

            sb.append( "}" );

            return sb.toString();
        }
        public UUID getId() {
            return id;
        }
        public void setId(UUID id) {
            this.id = id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getText() {
            return text;
        }
        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }

        public void setText(String text) {
            this.text = text;
        }
        public Date getMoment() {
            return moment;
        }
        public void setMoment(Date moment) {
            this.moment = moment;
        }
        public UUID getIdReply() {
            return idReply;
        }
        public void setIdReply(UUID idReply) {
            this.idReply = idReply;
        }
        public String getReplyPreview() {
            return replyPreview;
        }
        public void setReplyPreview(String replyPreview) {
            this.replyPreview = replyPreview;
        }
    }




}