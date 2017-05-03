package com.example.infinity.pixie;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    HttpResponse httpResponse;

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
                    sendPostRequest(emailIdString, passwordString);
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
        passwordEdit = (EditText) findViewById(R.id.passwordLogin);
        loginButton = (Button) findViewById(R.id.loginBtn);
        loginWaitLayout = (LinearLayout) findViewById(R.id.login_wait_layout);
        errorResponse = (TextView) findViewById(R.id.errorResponseLogin);
        loginLayout = (RelativeLayout) findViewById(R.id.loginLayout);
    }

    private void sendPostRequest(String uEmail, String uPass) {
        class UserLogin extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String userEmail = params[0];
                String userPassword = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                final HttpPost httppost = new HttpPost("http://52.53.93.85/api/users/userLogin");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);

                nameValuePairs.add(new BasicNameValuePair("userEmail", userEmail));
                nameValuePairs.add(new BasicNameValuePair("userPassword", userPassword));

                try {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs);
                    httppost.setEntity(urlEncodedFormEntity);
                    try {
                        httpResponse = httpClient.execute(httppost);
                        InputStream inputStream = httpResponse.getEntity().getContent();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String bufferedStrChunk = null;
                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }
                        return stringBuilder.toString();


                    } catch (ClientProtocolException cpe) {
                        System.out.println("First Exception HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Second Exception HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }

                } catch (UnsupportedEncodingException uee) {
                    System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(final String response) {
                super.onPostExecute(response);
                loginWaitLayout.setVisibility(View.GONE);
                if(httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
                {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
                else
                {
                    loginLayout.setVisibility(View.VISIBLE);
                    errorResponse.setVisibility(View.VISIBLE);
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        errorResponse.setText(jsonObj.get("response").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }


        UserLogin login = new UserLogin();
        login.execute(uEmail, uPass);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
