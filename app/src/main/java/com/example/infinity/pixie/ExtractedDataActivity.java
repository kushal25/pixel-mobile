package com.example.infinity.pixie;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.RunnableFuture;

public class ExtractedDataActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private ListView lv;
    ArrayList<HashMap<String, String>> extracted_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extracted_data);
        extracted_items = new ArrayList<>();
        lv = (ListView) findViewById(R.id.extracted_items);
        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(ExtractedDataActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String extractedData = getIntent().getStringExtra("ExtractedData");
            extractedData = "{\n" +
                    "                \"ExtractedData\": [\n" +
                    "        {\n" +
                    "            \"metadata\": \"email\",\n" +
                    "                \"data\": [\n" +
                    "            \"DEE.TRAN@LOCATIONLABS.COM\"\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"metadata\": \"phonenumber\",\n" +
                    "                \"data\": [\n" +
                    "            \"510 449 9757\"\n" +
                    "            ]\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"metadata\": \"url\",\n" +
                    "                \"data\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"metadata\": \"Date and Time\",\n" +
                    "                \"data\": null\n" +
                    "        }\n" +
                    "        ]\n" +
                    "        };";
            //Log.d("ex data", extractedData);
            //Pixie.showToast(ExtractedDataActivity.this,extractedData);

            //TextView responseText = (TextView) findViewById(R.id.responseText);
            //responseText.setText(extractedData);
            if (extractedData != null) {
                try {
                    JSONObject jsonObject = new JSONObject(extractedData);
                    JSONArray items = jsonObject.getJSONArray("ExtractedData");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);

                            String attr = c.getString("metadata");
                            String value = c.getString("data");

                            // tmp hash map for single contact
                            HashMap<String, String> hm = new HashMap<>();

                            // adding each child node to HashMap key => value
                            if(!attr.equals("null")){
                                hm.put("attr", attr);
                            }
                            if(!value.equals("null")){
                                hm.put("value", value);
                            }




                            // adding contact to contact list
                            extracted_items.add(hm);


                    }
                } catch (final JSONException e) {
                    Log.e(getLocalClassName(), "JSON parsing error ! : " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(getLocalClassName(), "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    ExtractedDataActivity.this, extracted_items,
                    R.layout.list_item, new String[]{"attr", "value"}, new int[]{R.id.attr,
                    R.id.value});

            lv.setAdapter(adapter);
        }
    }
}

