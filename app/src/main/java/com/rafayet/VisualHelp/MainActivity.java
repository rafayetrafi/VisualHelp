package com.rafayet.VisualHelp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rafayet.VisualHelp.objectdetection.DetectorActivity;
import com.rafayet.VisualHelp.textrecognition.TextDetection;

public class MainActivity extends AppCompatActivity {

    Button btn_objectDetection, btn_textDetection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        btn_objectDetection = findViewById(R.id.btn_objectDetectionClick);
        btn_textDetection = findViewById(R.id.btn_textDetectionClick);

        btn_objectDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
                startActivity(intent);
            }
        });

        btn_textDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TextDetection.class);
                startActivity(i);
            }
        });
    }
}