package com.example.infinity.pixie;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.infinity.pixie.service.HttpClientService;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class AccountInformation extends AppCompatActivity {

    private TextView responseTextView;
    HttpClientService httpClient = new HttpClientService();
    public Dialog mDialog = null;

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
        if(Pixie.isNetworkOK(AccountInformation.this)) {
            httpClient.userInfo(infoListener, Pixie.P.AUTH_CODE);
        }
        else
        {
            openConnectionDialog();
        }
    }

    public void openConnectionDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountInformation.this);
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout dialogView = (RelativeLayout) inflater.inflate(R.layout.dialog_no_internet, null);
        TextView textTitle = (TextView) dialogView.findViewById(R.id.title);
        TextView retryBtn = (TextView) dialogView.findViewById(R.id.retry);
        builder.setView(dialogView);

        mDialog = builder.create();
        mDialog.setCancelable(false);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Pixie.isNetworkOK(AccountInformation.this)) {
                    mDialog.dismiss();
                    onResume();
                } else {
                    openConnectionDialog();
                }
            }
        });
        mDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });
        mDialog.show();
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
