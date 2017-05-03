package com.example.infinity.pixie;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SignupActivity extends AppCompatActivity {

    //    private TextView nameIcon;
    private TextInputLayout fullName, emailId, password, confirmPassword, mobileNumber;
    private EditText fullNameEdit, emailIdEdit, passwordEdit, confirmPasswordEdit, mobileNumberEdit;
    private CheckBox terms;
    private Button signUpButton;
    private String nameString, emailIdString, passwordString, confirmPasswordString, mobileNumberString;
    private LinearLayout signupWaitLayout;
    private RelativeLayout signupLayout;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    private TextView errorResponse;
    HttpResponse httpResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameString = fullNameEdit.getText().toString();
                emailIdString = emailIdEdit.getText().toString();
                passwordString = passwordEdit.getText().toString();
                confirmPasswordString = confirmPasswordEdit.getText().toString();
                mobileNumberString = mobileNumberEdit.getText().toString();

                fullName.setError(null);
                emailId.setError(null);
                password.setError(null);
                confirmPassword.setError(null);
                mobileNumber.setError(null);

                if (nameString.trim().isEmpty()) {
                    fullName.setError("Name field cannot be empty");
                } else if (emailIdString.trim().isEmpty()) {
                    emailId.setError("Email field cannot be empty");
                } else if (!validateEmail(emailIdString)) {
                    emailId.setError("Email Address is Invalid");
                } else if (passwordString.trim().isEmpty()) {
                    password.setError("Password field cannot be empty");
                } else if (passwordString.trim().length() < 8) {
                    password.setError("Password Should be 8 or more characters");
                } else if (confirmPasswordString.trim().isEmpty()) {
                    confirmPassword.setError("Confirm Password field cannot be empty");
                } else if (confirmPasswordString.trim().length() < 8) {
                    confirmPassword.setError("Confirm Password Should be 8 or more characters");
                } else if (!passwordString.equals(confirmPasswordString)) {
                    Pixie.showToast(SignupActivity.this, "Password and Confirm Password is not the same");
                } else if (mobileNumberString.trim().isEmpty()) {
                    mobileNumber.setError("Mobile Number field cannot be empty");
                } else if (!terms.isChecked()) {
                    Pixie.showToast(SignupActivity.this, "Please Check the terms and conditions for signup");
                } else {
                    fullName.setErrorEnabled(false);
                    emailId.setErrorEnabled(false);
                    password.setErrorEnabled(false);
                    confirmPassword.setErrorEnabled(false);
                    mobileNumber.setErrorEnabled(false);
                    sendPostRequest(nameString, emailIdString, passwordString, mobileNumberString);
                    signupLayout.setVisibility(View.GONE);
                    signupWaitLayout.setVisibility(View.VISIBLE);
                }


            }
        });

    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void initViews() {
//        nameIcon = (TextView) findViewById(R.id.nameIcon);
//        nameIcon.setTypeface(Pixie.fontawesome);
        fullName = (TextInputLayout) findViewById(R.id.fullNameLayout);
        emailId = (TextInputLayout) findViewById(R.id.userEmailIdLayout);
        password = (TextInputLayout) findViewById(R.id.passwordLayout);
        confirmPassword = (TextInputLayout) findViewById(R.id.confirmPasswordLayout);
        mobileNumber = (TextInputLayout) findViewById(R.id.mobileNumberLayout);
        fullNameEdit = (EditText) findViewById(R.id.fullName);
        emailIdEdit = (EditText) findViewById(R.id.userEmailId);
        passwordEdit = (EditText) findViewById(R.id.password);
        confirmPasswordEdit = (EditText) findViewById(R.id.confirmPassword);
        mobileNumberEdit = (EditText) findViewById(R.id.mobileNumber);
        terms = (CheckBox) findViewById(R.id.terms_conditions);
        signUpButton = (Button) findViewById(R.id.signUpBtn);
        signupWaitLayout = (LinearLayout) findViewById(R.id.signup_wait_layout);
        errorResponse = (TextView) findViewById(R.id.errorResponse);
        signupLayout = (RelativeLayout) findViewById(R.id.signupLayout);

    }

        private void sendPostRequest(String uName, String uEmail, String uPass, String uMob) {
            class UserSignup extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String userName = params[0];
                String userEmail = params[1];
                String userPassword = params[2];
                String mobileNumber = params[3];

                HttpClient httpClient = new DefaultHttpClient();

                final HttpPost httppost = new HttpPost("http://52.53.93.85/api/users/userSignup");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);

                nameValuePairs.add(new BasicNameValuePair("userName", userName));
                nameValuePairs.add(new BasicNameValuePair("userEmail", userEmail));
                nameValuePairs.add(new BasicNameValuePair("userPassword", userPassword));
                nameValuePairs.add(new BasicNameValuePair("userPhoneNumber", mobileNumber));

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

                signupWaitLayout.setVisibility(View.GONE);
                if(httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
                {
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
                else
                {
                    signupLayout.setVisibility(View.VISIBLE);
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


        UserSignup signup = new UserSignup();
        signup.execute(uName, uEmail, uPass, uMob);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}





