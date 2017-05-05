package com.example.infinity.pixie;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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

public class ExtractedDataActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener{
    private MainActivity mainActivity;
    private DatabaseHandler dbhandler;
    private ProgressDialog progressDialog;
    private ListView lv;
    ListAdapter adapter;
    ArrayList<HashMap<String, String>> extracted_items;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbhandler = new DatabaseHandler(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extracted_data);
        extracted_items = new ArrayList<>();
        lv = (ListView) findViewById(R.id.extracted_items);
        lv.setLongClickable(true);
        lv.setOnItemLongClickListener(new AdapterView.
                OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ExtractedDataActivity.this, "long clicked pos: " + i, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                //HashMap<String,String> data=extracted_items.get(i);
                String mEmail = null;
                String phoneNumber = null;
                String url = null;
                HashMap<String,String> data = (HashMap<String, String>) adapter.getItem(i);
                Toast.makeText(ExtractedDataActivity.this, data.get("value"), Toast.LENGTH_LONG).show();
                if(data.containsKey("attr")){
                    if(i==0 && !data.get("value").equals("null")){
                        mEmail = data.get("value");
                        Intent mail = new Intent(Intent.ACTION_SEND);
                        mail.putExtra(Intent.EXTRA_EMAIL,mEmail);
                        //mail.setData(Uri.parse("mailto:"));
                        mail.setType("plain/type");


                        Intent mailer = Intent.createChooser(mail, "Send Email");
                        startActivity(mailer);

                        //intent.putExtra(ContactsContract.Intents.Insert.EMAIL, mEmail);
                        //startActivity(intent);
                    }else if(i== 1 && !data.get("value").equals("null")){
                        phoneNumber = data.get("value");
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
                        startActivity(intent);
                    } else if(i == 2 && !data.get("value").equals("null")){

                        url = "http://"+data.get("value");
                        Log.d("Url",url);
                        Intent browser = new Intent(Intent.ACTION_VIEW);
                        browser.setData(Uri.parse(url));
                        startActivity(browser);
                    }
                }





                return true;
            }
        });
        new GetContacts().execute();
    }
    //Implemented Long click on list view for extracted data.
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "long clicked pos: " + position, Toast.LENGTH_LONG).show();

        return true;
    }

    /**
     * Async task class to get json by making HTTP call
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
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
            //Add to database:
            String imgName = mainActivity.imgName;

            String email =null;
            String number =null;
            String url =null;
            String eDate =null;
            String eSTime =null;
            String eETime =null;
            String text =null;
            //////
            String extractedData = getIntent().getStringExtra("ExtractedData");
            //Uncomment to test.
            //String imgName =  "img-2";
            /*extractedData = "{\n" +
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
                    "                \"data\": [\n" +
                    "           \"www.google.com\"\n" +
                    "           ]\n"+
                    "        },\n" +
                    "        {\n" +
                    "            \"metadata\": \"Date and Time\",\n" +
                    "                \"data\": [\n" +
                    "           \"12-12-2012\"\n"+
                    "           ]\n" +
                    "        }\n" +
                    "        ]\n" +
                    "        };";*/
            //Log.d("ex data", extractedData);
            //Pixie.showToast(ExtractedDataActivity.this,extractedData);

            //TextView responseText = (TextView) findViewById(R.id.responseText);
            //responseText.setText(extractedData);
            if (extractedData != null) {

                try {
                    JSONObject jsonObject = new JSONObject(extractedData);
                    JSONArray items = jsonObject.getJSONArray("ExtractedData");

                    for (int i = 0; i < 4; i++) {
                        JSONObject c = items.getJSONObject(i);
                        if(!c.isNull("data")&& c.getString("data")!=null && !"".equals(c.getString("data")) && !"null".equalsIgnoreCase(c.getString("data"))){

                            if(!c.getJSONArray("data").equals(null)){
                                String attr = c.getString("metadata");

                                //TODO : Loop for each value

                                String value = c.getJSONArray("data").get(0).toString();

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

                        }
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

//For Database

            if (extractedData != null) {

                try {
                    JSONObject jsonObject = new JSONObject(extractedData);
                    JSONArray items = jsonObject.getJSONArray("ExtractedData");
                    if(!items.getJSONObject(0).isNull("data")){
                        email=items.getJSONObject(0).getJSONArray("data").get(0).toString();
                    }
                    if(!items.getJSONObject(1).isNull("data")){
                        number=items.getJSONObject(1).getJSONArray("data").get(0).toString();
                    }
                    if(!items.getJSONObject(2).isNull("data")){
                        url=items.getJSONObject(2).getJSONArray("data").get(0).toString();
                    }
                    if(!items.getJSONObject(3).isNull("data")){
                        eDate=items.getJSONObject(3).getJSONArray("data").get(0).toString();
                    }
                    /*if(!items.getJSONObject(4).isNull("data")){

                        eSTime=items.getJSONObject(4).getJSONArray("data").get(0).toString();
                        if(items.getJSONObject(4).getJSONArray("data").length()>=2){
                        eETime=items.getJSONObject(4).getJSONArray("data").get(1).toString();
                        }
                    }*/
                    /*if(!items.getJSONObject(5).isNull("data")){
                        text=items.getJSONObject(5).getJSONArray("data").get(0).toString();
                    }*/



                dbhandler.insertTitle(imgName,email,number,url,eDate,eSTime,eETime,text);


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




        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            Toast.makeText(getApplicationContext(),"Image Data"+(dbhandler.getAllTitles()), Toast.LENGTH_LONG).show();
            Log.d("Imagge Data :", dbhandler.getAllTitles().getString(1));
            adapter = new SimpleAdapter(
                    ExtractedDataActivity.this, extracted_items,
                    R.layout.list_item, new String[]{"attr", "value"}, new int[]{R.id.attr,
                    R.id.value});

            lv.setAdapter(adapter);
        }
    }
}

