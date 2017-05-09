package com.example.infinity.pixie;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.infinity.pixie.adapter.AccountInfoAdapter;
import com.example.infinity.pixie.adapter.ProfileAdapter;
import com.example.infinity.pixie.service.HttpClientService;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;


public class AccountInformation extends AppCompatActivity {

    private TextView responseTextView;
    HttpClientService httpClient = new HttpClientService();
    private ListView accountInfoListView;
    private AccountInfoAdapter accountInfoAdapter = null;
    ArrayList<String> keyItems  = new ArrayList<>();
    ArrayList<String> valueitems  = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);
        keyItems = getIntent().getStringArrayListExtra("keyItems");
        valueitems = getIntent().getStringArrayListExtra("valueItems");
        initViews();
    }

    public void initViews()
    {
        responseTextView = (TextView) findViewById(R.id.resultText);
        accountInfoListView = (ListView) findViewById(R.id.accountInfoListView);
        this.accountInfoAdapter = new AccountInfoAdapter(this,keyItems, valueitems);
        this.accountInfoListView.setAdapter(accountInfoAdapter);
        this.accountInfoListView.setSmoothScrollbarEnabled(true);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
