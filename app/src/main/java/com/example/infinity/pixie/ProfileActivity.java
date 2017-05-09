package com.example.infinity.pixie;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private ListView profileMenuListView;
    private ProfileAdapter profileAdapter = null;
    private LinearLayout profileWaitLayout;
    ArrayList<String> listItems  = new ArrayList<>();
    ArrayList<Integer> listIcons  = new ArrayList<>();
    public Dialog mDialog = null;
    ArrayList<String> keyItems  = new ArrayList<>();
    ArrayList<String> valueitems  = new ArrayList<>();
    HttpClientService httpClient = new HttpClientService();

    JsonHttpResponseHandler infoListener = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                initializeDataFromService(response.getJSONObject("response"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String err, Throwable e) {
            Pixie.showToast(ProfileActivity.this, "Something went wrong. Please try again later");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeData();
        initViews();
        profileMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        if(Pixie.isNetworkOK(ProfileActivity.this)) {
                            httpClient.userInfo(infoListener, Pixie.P.AUTH_CODE);
                        }
                        else
                        {
                            openConnectionDialog();
                        }
                        break;
                    case 1:
//                        Intent historyIntent = new Intent(ProfileActivity.this, HistoryActivity.class);
//                        startActivity(historyIntent);
                        Pixie.showToast(ProfileActivity.this, "Work in Progress");
                        break;
                    case 2:
//                        Intent settingsIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
//                        startActivity(settingsIntent);
                        Pixie.showToast(ProfileActivity.this, "Work in Progress");
                        break;
                    case 3:
//                        Intent aboutUsIntent = new Intent(ProfileActivity.this, AboutUsActivity.class);
//                        startActivity(aboutUsIntent);
                        Pixie.showToast(ProfileActivity.this, "Work in Progress");
                        break;
                    case 4:
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, Pixie.P.SHARING_MSG);
                        shareIntent.setType("text/plain");
                        String shareTitle = ProfileActivity.this.getResources().getString(R.string.share_title);
                        ProfileActivity.this.startActivity(Intent.createChooser(shareIntent, shareTitle));
                        startActivity(shareIntent);
                        break;
                    case 5:
                        profileWaitLayout.setVisibility(View.VISIBLE);
                        Intent logoutIntent = new Intent(ProfileActivity.this, HomeActivity.class);
                        Pixie.P.AUTH_CODE = null;
                        Pixie.P.write(getApplicationContext());
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logoutIntent);
                        profileWaitLayout.setVisibility(View.GONE);
                        break;
                    default:
                        Pixie.showToast(ProfileActivity.this, "Something went wrong. Try Again");
                }

            }
        });
    }

    public void openConnectionDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                if (Pixie.isNetworkOK(ProfileActivity.this)) {
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

    public void initializeDataFromService(JSONObject res)
    {
        Iterator<String> keys = res.keys();
        while(keys.hasNext()){
            try {
                String key = keys.next();
                String value = res.getString(key);
                if(key.equals("userName")|| key.equals("userEmail") || key.equals("userPhoneNumber")) {
                    keyItems.add("Name: ");
                    valueitems.add(value);
                }
                else if(key.equals("userEmail"))
                {
                    keyItems.add("Email: ");
                    valueitems.add(value);
                }
                else if(key.equals("userPhoneNumber"))
                {
                    keyItems.add("Phone Number: ");
                    valueitems.add(value);
                }
                else if(key.equals("lastLoginAt"))
                {
                    DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date date = null;
                    try {
                        date = inputFormatter.parse(value);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    String output = outputFormatter.format(date);

                    keyItems.add("Last Login At: ");
                    valueitems.add(output);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent accountInfoIntent = new Intent(ProfileActivity.this, AccountInformation.class);
        accountInfoIntent.putStringArrayListExtra("keyItems", keyItems);
        accountInfoIntent.putStringArrayListExtra("valueItems", valueitems);
        startActivity(accountInfoIntent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void initializeData(){
        listItems.add("Account Information");
        listItems.add("History");
        listItems.add("Settings");
        listItems.add("About Us");
        listItems.add("Invite your friends");
        listItems.add("Logout");
        listIcons.add(R.string.profile_icon);
        listIcons.add(R.string.history_icon);
        listIcons.add(R.string.settings_icon);
        listIcons.add(R.string.about_us_icon);
        listIcons.add(R.string.share_icon);
        listIcons.add(R.string.logout_icon);
    }

    public void initViews(){
        this.profileMenuListView = (ListView) findViewById(R.id.profileMenuListView);
        this.profileAdapter = new ProfileAdapter(this,listItems, listIcons);
        this.profileMenuListView.setAdapter(profileAdapter);
        this.profileMenuListView.setSmoothScrollbarEnabled(true);
        profileWaitLayout = (LinearLayout) findViewById(R.id.profile_wait_layout);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
