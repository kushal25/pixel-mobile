package com.example.infinity.pixie.service;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.infinity.pixie.MainActivity;
import com.example.infinity.pixie.R;
import com.example.infinity.pixie.SignupActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.HttpGet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by infinity on 4/21/17.
 */

public class HttpClientService {
    private final String baseUrl = "http://52.53.93.85/api/";
    final String extractUrl = baseUrl + "images/writeData";
    final String loginUrl = baseUrl + "users/userLogin";
    final String userInfoUrl = baseUrl + "users/userInfo";
    final String signupUrl = baseUrl + "users/userSignup";
    AsyncHttpClient httpClient = new AsyncHttpClient();
;
    public void extractData(JsonHttpResponseHandler listener, File file) {
        try {
            File sourceFile = new File(file.getAbsolutePath());
            RequestParams params = new RequestParams();
            params.put("img", sourceFile);
            httpClient.post(extractUrl, params, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userSignup(JsonHttpResponseHandler listener, String userName, String userEmail, String userPassword, String userPhoneNumber){
        try {
            RequestParams params = new RequestParams();
            params.put("userName", userName);
            params.put("userEmail", userEmail);
            params.put("userPassword", userPassword);
            params.put("userPhoneNumber", userPhoneNumber);
            httpClient.post(signupUrl, params, listener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void userLogin(JsonHttpResponseHandler listener, String userEmail, String userPassword) {
        try {
            RequestParams params = new RequestParams();
            params.put("userEmail", userEmail);
            params.put("userPassword", userPassword);
            httpClient.post(loginUrl, params, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userInfo(JsonHttpResponseHandler listener, String authToken) {
        try {
            RequestParams params = new RequestParams();
            httpClient.addHeader("X-Auth-Token", authToken);
            httpClient.get(userInfoUrl, params, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}