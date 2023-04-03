package com.example.basics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CalcActivity extends AppCompatActivity {

    private TextView tvHistory;
    private TextView tvResult;
    private String minus;
    private String comma;
    private boolean clearResult;
    private boolean clearHistory;
    private double operand;
    private String operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        tvHistory = findViewById(R.id.tvHistory);
        tvResult = findViewById(R.id.tvResult);

        tvHistory.setText("");
        tvResult.setText("0");
        minus = getApplicationContext().getString(R.string.calc_minus_sign);
        comma = getApplicationContext().getString(R.string.btn_calc_comma);

        for (int i = 0; i < 10; i++){
            findViewById(getResources().getIdentifier(
                    "button_digit_" + i,
                    "id", getPackageName())).
                    setOnClickListener(this::digitClick);
        }

        findViewById(R.id.button_plus_minus).setOnClickListener(this::pmClick);
        findViewById(R.id.button_comma).setOnClickListener(this::commaClick);
        findViewById(R.id.button_backspace).setOnClickListener(this::backspaceClick);
        findViewById(R.id.button_inverse).setOnClickListener(this::inverseClick);
        findViewById(R.id.button_sqrt).setOnClickListener(this::sqrtClick);

        findViewById(R.id.button_clear_e ).setOnClickListener(this::clearEntryClick);
        findViewById(R.id.button_clear_all).setOnClickListener(this::clearAllClick);

        findViewById(R.id.button_equal).setOnClickListener(this::equalClick);


        findViewById(R.id.button_divide).setOnClickListener(this::fnClick);
        findViewById(R.id.button_multiply).setOnClickListener(this::fnClick);
        findViewById(R.id.button_minus).setOnClickListener(this::fnClick);
        findViewById(R.id.button_plus).setOnClickListener(this::fnClick);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("history", tvHistory.getText());
        outState.putCharSequence("result", tvResult.getText());
        Log.d(CalcActivity.class.getName(), "Saved!");
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvHistory.setText(savedInstanceState.getCharSequence("history"));
        tvResult.setText(savedInstanceState.getCharSequence("result"));
        Log.d(CalcActivity.class.getName(), "Loaded!");
    }

    private void pmClick(View v){
        String result = tvResult.getText().toString();

        if(result.equals("0")) return;

        if(result.contains(minus))
            result = result.substring(1);
        else
            result = minus + result;

        tvResult.setText(result);
    }

    private void digitClick(View v){
        String result = tvResult.getText().toString();

        if(clearResult){
            clearResult = false;
            result = "0";
        }

        if(result.replaceAll("[" + comma + minus +"]","").length() >= 10) return;

        String digit = ((Button)v).getText().toString();

        if(result.equals("0")) {
            result = digit;
        }
        else
            result += digit;

        if(clearHistory){
            tvHistory.setText("");
            clearHistory = false;

        }
        tvResult.setText(result);
    }

    private void inverseClick(View v){
        String result = tvResult.getText().toString();

        double d = Parse(result);

        if (d == 0) {
            alert(R.string.calc_divide_by_zero);
            return;
        }



        tvHistory.setText(String.format("1/%s =", result));
        result = stringParser(1/d);
        tvResult.setText(result);

    }
    private void alert(int stringId){

        Toast.makeText(CalcActivity.this, stringId, Toast.LENGTH_LONG).show();

        Vibrator vibrator;

        long[] vibratePattern = {0,200,100,200};

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.S){
            VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        }
        else
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern,-1));
        else
            vibrator.vibrate(vibratePattern, -1);
    }
    private void sqrtClick(View v){
        String result = tvResult.getText().toString();

        double d = Parse(result);

        if(d < 0) {
            alert(R.string.calc_sqr_minus);
            return;
        }

        tvHistory.setText(String.format("√(%s) =", result));
        result = stringParser(Math.sqrt(d));
        tvResult.setText(result);

    }
    private void clearEntryClick(View v){
        tvResult.setText("0");
    }
    private void clearAllClick(View v){
        tvResult.setText("0");
        tvHistory.setText("");
    }
    private void fnClick(View v){
        String fn = ((Button) v).getText().toString();
        String result = tvResult.getText().toString();

        String history = String.format("%s %s", result, fn);
        tvHistory.setText(history);

        clearResult = true;
        clearHistory = false;

        operation = fn;
        operand = Parse(result);
    }
    private void equalClick(View v){
        String result = this.tvResult.getText().toString();
        String history = this.tvHistory.getText().toString();

        tvHistory.setText(String.format("%s %s =", history, result));

        double operand2 = Parse(result);

        if(operation.equals(getString(R.string.btn_calc_plus)))
            tvResult.setText(stringParser(operand + operand2));
        else if(operation.equals(getString(R.string.btn_calc_minus)))
            tvResult.setText(stringParser(operand - operand2));
        else if(operation.equals(getString(R.string.btn_calc_multiplication)))
            tvResult.setText(stringParser(operand * operand2));
        else if(operation.equals(getString(R.string.btn_calc_divide)))
            if(operand2 == 0)
                alert(R.string.calc_divide_by_zero);
            else
                tvResult.setText(stringParser(operand / operand2));

        clearResult = true;
        clearHistory = true;
    }



    private void commaClick(View v){
        String result = tvResult.getText().toString();

        if(!result.contains(comma)){
            result += comma;
        }

        tvResult.setText(result);

    }
    private void backspaceClick(View v){
        if(clearHistory){
            tvHistory.setText("");
            clearHistory = false;
        }
        if(clearResult)
            clearResult = false;

        String result = tvResult.getText().toString();

        if(result.equals("0"))
            return;
        if(result.replaceAll("[" + comma + minus +"]","").length() == 1)
            result = "0";
        else
            result = result.substring(0,result.length() - 1);

        tvResult.setText(result);
    }
    private double Parse(String str){
        if (str.contains(comma))
            str = str.replace(comma, ".");
        if (str.contains(minus))
            str = str.replace(minus, "-");
        return Double.parseDouble(str);
    }
    private String stringParser(double d){
        String str = String.valueOf(d);
        int length = 10;

        if(str.contains(".")) {
            str = str.replace(".", comma);
            length++;
        }
        if(str.startsWith("-")) {
            str = str.replace("-", minus);
            length++;
        }
        if(str.length() > length)
            str = str.substring(0, length);
        return str;
    }

}