package com.example.pyrov.a2048;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // кнопка старта
    Button buttonStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // инициализируем и вешаем слушателя
        buttonStart = (Button) findViewById(R.id.button_start);
        buttonStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intentNewGame;
        switch (view.getId()) {
            case R.id.button_start:
                // если нажат старт - открываем новую активность
                intentNewGame = new Intent(this, GameActivity.class);
                startActivity(intentNewGame);
                break;
        }
    }
}
