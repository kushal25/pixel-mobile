package com.example.infinity.pixie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.infinity.pixie.service.HttpClientService;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class AccountInformation extends AppCompatActivity {

    private TextView responseTextView;
    HttpClientService httpClient = new HttpClientService();

    JsonHttpResponseHandler infoListener = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                responseTextView.setText(response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String err, Throwable e) {
            Pixie.showToast(AccountInformation.this, "Listener Error");
            responseTextView.setText(err);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);
        initViews();
        httpClient.userInfo(infoListener, Pixie.P.AUTH_CODE);
    }

    public void initViews()
    {
        responseTextView = (TextView) findViewById(R.id.resultText);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
