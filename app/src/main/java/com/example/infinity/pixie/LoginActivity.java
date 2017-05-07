package com.example.infinity.pixie;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.infinity.pixie.service.HttpClientService;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailId, password;
    private EditText emailIdEdit, passwordEdit;
    private Button loginButton;
    private String emailIdString, passwordString;
    private LinearLayout loginWaitLayout;
    private RelativeLayout loginLayout;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    private TextView errorResponse;
    HttpClientService httpClient = new HttpClientService();

    JsonHttpResponseHandler loginListener = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                loginWaitLayout.setVisibility(View.GONE);
                if(statusCode == HttpURLConnection.HTTP_OK)
                {
                    Pixie.P.AUTH_CODE = response.getJSONObject("response").getString("X-Auth-Token").toString();
                    Pixie.P.write(getApplicationContext());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
                else
                {
                    loginLayout.setVisibility(View.VISIBLE);
                    errorResponse.setVisibility(View.VISIBLE);
                    try {
                        errorResponse.setText(response.get("response").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String err, Throwable e) {
            Pixie.showToast(LoginActivity.this, "Something went wrong. Please try again!!");
            e.printStackTrace();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailIdString = emailIdEdit.getText().toString();
                passwordString = passwordEdit.getText().toString();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


                emailId.setError(null);
                password.setError(null);

                if (emailIdString.trim().isEmpty()) {
                    emailId.setError("Email field cannot be empty");
                } else if (!validateEmail(emailIdString)) {
                    emailId.setError("Email Address is Invalid");
                } else if (passwordString.trim().isEmpty()) {
                    password.setError("Password field cannot be empty");
                } else if (passwordString.trim().length() < 8) {
                    password.setError("Password Should be 8 or more characters");
                } else {
                    emailId.setErrorEnabled(false);
                    password.setErrorEnabled(false);
                    httpClient.userLogin(loginListener, emailIdString, passwordString);
                    loginLayout.setVisibility(View.GONE);
                    loginWaitLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void initViews() {

        emailId = (TextInputLayout) findViewById(R.id.userEmailIdLoginLayout);
        password = (TextInputLayout) findViewById(R.id.passwordLoginLayout);
        emailIdEdit = (EditText) findViewById(R.id.userEmailIdLogin);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.hideSoftInputFromWindow(emailIdEdit.getWindowToken(), 0);
        passwordEdit = (EditText) findViewById(R.id.passwordLogin);
        loginButton = (Button) findViewById(R.id.loginBtn);
        loginWaitLayout = (LinearLayout) findViewById(R.id.login_wait_layout);
        errorResponse = (TextView) findViewById(R.id.errorResponseLogin);
        loginLayout = (RelativeLayout) findViewById(R.id.loginLayout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
