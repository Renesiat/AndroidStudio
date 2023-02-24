package com.example.basics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CalcActivity extends AppCompatActivity {
    private TextView tvHistory ;
    private TextView tvResult  ;
    private String minusSign ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        tvHistory = findViewById( R.id.tvHistory ) ;
        tvResult  = findViewById( R.id.tvResult  ) ;
        tvHistory.setText( "" ) ;
        tvResult.setText( "0" ) ;
        minusSign = getApplicationContext().getString( R.string.calc_minus_sign ) ;

        for( int i = 0; i < 10; i++ ) {
            findViewById(
                    getResources().getIdentifier(
                            "button_digit_" + i,
                            "id",
                            getPackageName()
                    ) ).setOnClickListener( this::digitClick ) ;
        }
        findViewById( R.id.button_plus_minus ).setOnClickListener( this::pmClick ) ;
        findViewById(R.id.button_comma).setOnClickListener(this::commaClick);
        findViewById(R.id.button_backspace).setOnClickListener(this::backspaceClick);
        findViewById( R.id.button_inverse ).setOnClickListener( this::inverseClick ) ;
        findViewById(R.id.button_sqrt).setOnClickListener(this::sqrtClick);
        Log.d( CalcActivity.class.getName(), "onCreate" ) ;
    }

    @Override
    protected void onSaveInstanceState( @NonNull Bundle outState ) {
        super.onSaveInstanceState( outState ) ;   // необхідно залишити
        outState.putCharSequence( "history", tvHistory.getText() ) ;
        outState.putCharSequence( "result",  tvResult.getText()  ) ;
        Log.d( CalcActivity.class.getName(), "Дані збережено" ) ;
    }

    @Override
    protected void onRestoreInstanceState( @NonNull Bundle savedInstanceState ) {
        super.onRestoreInstanceState( savedInstanceState ) ;
        tvHistory.setText( savedInstanceState.getCharSequence( "history" ) ) ;
        tvResult.setText(  savedInstanceState.getCharSequence( "result"  ) ) ;
        Log.d( CalcActivity.class.getName(), "Дані відновлено" ) ;
    }


    private void digitClick( View v ) {
        String result = tvResult.getText().toString() ;
        if( result.length() >= 10 ) return ;
        String digit = ((Button) v).getText().toString() ;
        if( result.equals( "0" ) ) {
            result = digit ;
        }
        else {
            result += digit ;
        }
        tvResult.setText( result ) ;
    }
    private void pmClick( View v ) {
        String result = tvResult.getText().toString() ;
        if( result.startsWith( minusSign ) ) {
            result = result.substring(1 ) ;
        }
        else {
            result = minusSign + result ;
        }
        tvResult.setText( result ) ;
    }
    private void commaClick(View v){
        String result = this.tvResult.getText().toString();
        if(!result.contains(",")){
            result += ",";
        }
        this.tvResult.setText(result);

    }
    private void backspaceClick(View v){
        String result = this.tvResult.getText().toString();
        if(result.equals("0"))
            return;
        if(result.replaceAll("[-,]","").length() == 1)
            result = "0";
        else
            result = result.substring(0,result.length() - 1);
        this.tvResult.setText(result);
    }
    private void inverseClick( View v ) {
        String result = tvResult.getText().toString() ;
        double arg = parseResult( result ) ;
        if( arg == 0 ) {
            Toast
                    .makeText(
                            CalcActivity.this,
                            R.string.calc_divide_by_zero,
                            Toast.LENGTH_SHORT
                    )
                    .show() ;
            return ;
        }
        tvHistory.setText( String.format( "1/(%s) =", result ) ) ;
        showResult( 1 / arg ) ;
    }

    private void sqrtClick(View v){
        String result = this.tvResult.getText().toString();
        double d = this.parseResult(result);
        this.tvHistory.setText(String.format("√(%s) =", result));
        showResult(Math.sqrt(d));

    }
    private double parseResult( String result ) {
        return Double.parseDouble( result.replace( minusSign, "-" ) ) ;
    }
    private void showResult( double arg ) {
         String result = String.valueOf( arg ) ;
        if( result.length() > 10 ) {
            result = result.substring( 0, 10 ) ;
        }
        tvResult.setText( result.replace( "-", minusSign ) ) ;
    }
}