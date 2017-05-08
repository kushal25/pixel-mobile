package com.example.infinity.pixie;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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
    HttpClientService httpClient = new HttpClientService();
    public Dialog mDialog = null;

    JsonHttpResponseHandler signupListener = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                signupWaitLayout.setVisibility(View.GONE);
                if(statusCode == HttpURLConnection.HTTP_OK)
                {
                    Pixie.P.AUTH_CODE = response.getJSONObject("response").getString("X-Auth-Token").toString();
                    Pixie.P.write(getApplicationContext());
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
                else
                {
                    signupLayout.setVisibility(View.VISIBLE);
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
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
            signupWaitLayout.setVisibility(View.GONE);
            signupLayout.setVisibility(View.VISIBLE);
            errorResponse.setVisibility(View.VISIBLE);
            try {
                errorResponse.setText(response.get("response").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Pixie.isNetworkOK(SignupActivity.this)) {
                    nameString = fullNameEdit.getText().toString();
                    emailIdString = emailIdEdit.getText().toString();
                    passwordString = passwordEdit.getText().toString();
                    confirmPasswordString = confirmPasswordEdit.getText().toString();
                    mobileNumberString = mobileNumberEdit.getText().toString();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

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
                        httpClient.userSignup(signupListener, nameString, emailIdString, passwordString, mobileNumberString);
                        signupLayout.setVisibility(View.GONE);
                        signupWaitLayout.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    openConnectionDialog();
                }
            }
        });

    }

    public void openConnectionDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
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
                if (Pixie.isNetworkOK(SignupActivity.this)) {
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
        fullNameEdit.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.hideSoftInputFromWindow(fullNameEdit.getWindowToken(), 0);
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





