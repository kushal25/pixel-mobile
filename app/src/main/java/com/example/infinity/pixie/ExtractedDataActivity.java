package com.example.infinity.pixie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ExtractedDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extracted_data);
        String extractedData = getIntent().getStringExtra("ExtractedData");
        Log.d("ex data", extractedData);
        Pixie.showToast(ExtractedDataActivity.this,extractedData);

        TextView responseText = (TextView) findViewById(R.id.responseText);
        responseText.setText(extractedData);
    }
}
