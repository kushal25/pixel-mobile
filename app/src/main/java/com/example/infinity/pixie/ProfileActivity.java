package com.example.infinity.pixie;

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
                Pixie.showToast(ProfileActivity.this, "position: " + i);
              

            }
        });
    }
    public void initializeData(){
        listItems.add("Account Information");
        listItems.add("History");
        listItems.add("Settings");
        listItems.add("Delete Images");
        listItems.add("About Us");
        listItems.add("Invite your friends");
        listItems.add("Logout");
        listIcons.add(R.string.profile_icon);
        listIcons.add(R.string.history_icon);
        listIcons.add(R.string.settings_icon);
        listIcons.add(R.string.delete_icon);
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
