package com.example.infinity.pixie;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.infinity.pixie.adapter.ProfileAdapter;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ListView profileMenuListView;
    private ProfileAdapter profileAdapter = null;
    ArrayList<String> listItems  = new ArrayList<>();
    ArrayList<Integer> listIcons  = new ArrayList<>();

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
                        Intent accountInfoIntent = new Intent(ProfileActivity.this, AccountInformation.class);
                        startActivity(accountInfoIntent);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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
                        Intent logoutIntent = new Intent(ProfileActivity.this, HomeActivity.class);
                        Pixie.P.AUTH_CODE = null;
                        Pixie.P.write(getApplicationContext());
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logoutIntent);

                        break;
                    default:
                        Pixie.showToast(ProfileActivity.this, "Something went wrong. Try Again");
                }

            }
        });
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
