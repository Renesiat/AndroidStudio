package com.example.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button calcButton = findViewById( R.id.calcButton ) ;
        calcButton.setOnClickListener( this::btnCalcClick ) ;

        findViewById( R.id.exitButton )
                .setOnClickListener( this::btnExitClick ) ;
        findViewById( R.id.gameButton )
                .setOnClickListener( this::btnGameClick ) ;
        findViewById(R.id.ratesButton)
                .setOnClickListener(this::btnRatesClick);
        findViewById(R.id.chatButton)
                .setOnClickListener(this::btnChatClick);
    }
    private void btnGameClick( View v ) {
        Intent gameIntent = new Intent(
                MainActivity.this,
                GameActivity.class ) ;
        startActivity( gameIntent ) ;
    }
    private void btnCalcClick( View v ) {
        Intent calcIntent = new Intent(
                MainActivity.this,
                CalcActivity.class ) ;
        startActivity( calcIntent ) ;
    }
    private void btnRatesClick(View v){
        Intent ratesIntent = new Intent(MainActivity.this, RatesActivity.class);
        startActivity( ratesIntent ) ;
    }
    private void btnChatClick(View v){
        Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity( chatIntent ) ;
    }
    private void btnExitClick( View v ) {
        finish() ;
    }
}
