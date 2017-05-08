package com.example.infinity.pixie;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.RunnableFuture;

public class ExtractedDataActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener{
    private MainActivity mainActivity;
    private DatabaseHandler dbhandler;
    private ProgressDialog progressDialog;
    private ListView lv;
    private String startTime;
    private String endTime;
    private String dDate;
    private String eventEmail;
    private String d
            ;
    String arr[];
    ListAdapter adapter;
    ArrayList<HashMap<String, String>> extracted_items;

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
                //Toast.makeText(ExtractedDataActivity.this, "long clicked pos: " + i, Toast.LENGTH_LONG).show();


                //HashMap<String,String> data=extracted_items.get(i);
                String mEmail = null;
                String phoneNumber = null;
                String url = null;
                HashMap<String,String> data = (HashMap<String, String>) adapter.getItem(i);
                //Toast.makeText(ExtractedDataActivity.this, data.get("attr"), Toast.LENGTH_LONG).show();
                //Toast.makeText(ExtractedDataActivity.this, data.get("value"), Toast.LENGTH_LONG).show();
                if(data.containsKey("attr")){
                    if(data.get("attr").equals("Email Address") && !data.get("value").equals("null")){
                        //Toast.makeText(ExtractedDataActivity.this, data.get("attr"), Toast.LENGTH_LONG).show();
                        mEmail = data.get("value");
                        Intent mail = new Intent(Intent.ACTION_SEND);
                        mail.putExtra(Intent.EXTRA_EMAIL,eventEmail);
                        //mail.setData(Uri.parse("mailto:"));
                        mail.setType("plain/type");


                        Intent mailer = Intent.createChooser(mail, "Send Email");
                        startActivity(mailer);

                        //intent.putExtra(ContactsContract.Intents.Insert.EMAIL, mEmail);
                        //startActivity(intent);
                    }else if(data.get("attr").equals("Contact") && !data.get("value").equals("null")){
                        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                        //Toast.makeText(ExtractedDataActivity.this, "long clicked pos: " + data.get("attr"), Toast.LENGTH_LONG).show();
                        phoneNumber = data.get("value");
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
                        startActivity(intent);
                    } else if(data.get("attr").equals("Web URL") && !data.get("value").equals("null")){
                        //Toast.makeText(ExtractedDataActivity.this, data.get("attr"), Toast.LENGTH_LONG).show();
                        url = "http://"+data.get("value");
                        Log.d("Url",url);
                        Intent browser = new Intent(Intent.ACTION_VIEW);
                        browser.setData(Uri.parse(url));
                        startActivity(browser);
                    } else if(data.get("attr").equals("Date") && !data.get("value").equals("null")){
                        try{
                        /*//all version of android
                        Intent cal = new Intent();

                        // mimeType will popup the chooser any  for any implementing application (e.g. the built in calendar or applications such as "Business calendar"
                        cal.setType("vnd.android.cursor.item/event");

                        // the time the event should start in millis. This example uses now as the start time and ends in 1 hour
                        cal.putExtra("beginTime",startTime);
                        cal.putExtra("endTime", endTime);

                        // the action
                        cal.setAction(Intent.ACTION_EDIT);
                        startActivity(cal);*/
                       String x = "June 27,  2007";
                        String arr1[] = x.split("[\\s,;]+");
                        String string = "January 2, 2010";
                        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);

                        Date dddate = format.parse(string);System.out.println(dddate);

                        Intent intent1 = new Intent(Intent.ACTION_INSERT);
                        intent1.setType("vnd.android.cursor.item/event");

                        intent1.setData(CalendarContract.Events.CONTENT_URI);
                        intent1.putExtra(CalendarContract.Events.TITLE, ""+arr[0]+arr[1]);
                        intent1.putExtra(CalendarContract.Events.ALL_DAY, false);
                        GregorianCalendar calDate = new GregorianCalendar();
                            calDate.set(2017,Calendar.MAY,16,12,0);

                        System.out.println("Email"+eventEmail+ " start time  : " + startTime + " endtime  ; " + endTime);

                            intent1.putExtra(
                                CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                calDate.getTimeInMillis());
                        intent1.putExtra(
                                CalendarContract.EXTRA_EVENT_END_TIME,
                                1.8e+7);
                        intent1.putExtra(
                                Intent.EXTRA_EMAIL,eventEmail
                                );
                        startActivity(intent1);
                        /*Calendar cal = Calendar.getInstance();
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                            System.out.println("Email"+mEmail+ " start time  : " + startTime + " endtime  ; " + endTime);
                        intent.putExtra("beginTime", startTime);
                        intent.putExtra("allDay", false);

                        intent.putExtra("endTime", endTime);
                        intent.putExtra("title", ""+arr[0]+arr[1]);
                        startActivity(intent);*/
                    }catch (Exception e){
                            Log.e(getLocalClassName(), "parsing error ! : " + e.getMessage());

                        }
                    } else if(data.get("attr").equals("CompleteText") && !data.get("value").equals("null")){
                        Intent txt = new Intent();
                        txt.setAction(Intent.ACTION_SEND);
                        txt.putExtra(Intent.EXTRA_TEXT, "Extra Text");
                        txt.setType("text/plain");

                        Intent clipboardIntent = new Intent();
                        clipboardIntent.putExtra(Intent.EXTRA_TEXT, "Extra Text");
                        clipboardIntent.putExtra(Intent.EXTRA_SUBJECT, ""+arr[0]+arr[1]);

                        Intent chooserIntent = Intent.createChooser(txt, ""+arr[0]+arr[1]);
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { clipboardIntent });
                        startActivity(chooserIntent);
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
        //Toast.makeText(this, "long clicked pos: " + position, Toast.LENGTH_LONG).show();

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(ExtractedDataActivity.this, MainActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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

            String imgName = Pixie.getImgName();

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
                    HashMap<String, String> hm2 = new HashMap<>();
                    System.out.println("JSON response"+ items.toString());
                    for (int i = 0; i <4; i++) {
                        JSONObject c = items.getJSONObject(i);
                        System.out.println("C in I"+ c);
                        if(!c.isNull("data")&& c.getString("data")!=null && !"".equals(c.getString("data")) && !"null".equalsIgnoreCase(c.getString("data"))){

                            if(!c.getJSONArray("data").equals(null)){
                                String attr = c.getString("metadata");

                                //TODO : Loop for each value

                                //String value = c.getJSONArray("data").get(0).toString();
                                System.out.println("data len : "+c.getJSONArray("data").length() );
                                for (int j =0; j<c.getJSONArray("data").length();j++ ){
                                    System.out.println("In for loop j: "+j +" Value at j: " +c.getJSONArray("data").get(j) );

                                    // tmp hash map for single contact
                                    HashMap<String, String> hm = new HashMap<>();

                                    // adding each child node to HashMap key => value
                                    String value = c.getJSONArray("data").get(j).toString();
                                    if(attr.equals("Email Address") || attr.equals("Web URL")){
                                        value= value.replace("5","s");
                                        eventEmail = value;
                                    }
                                    if(attr.equals("Date")){
                                        dDate = value;
                                    }
                                    if(!attr.equals("null")){
                                        hm.put("attr", attr);
                                    }
                                    if(!c.getJSONArray("data").get(j).toString().equals("null")){
                                        hm.put("value", value );
                                    }

                                    // adding contact to contact list
                                    extracted_items.add(hm);
                                }

                            }

                        }
                    }
                    System.out.println("Get Strat time out: "+items.getJSONObject(4).getString("data") );
                    if(  !"null".equals(items.getJSONObject(4).getString("data"))){
                        // tmp hash map for start Time
                        HashMap<String, String> hm1 = new HashMap<>();
                        startTime =items.getJSONObject(4).getString("data");
                        hm1.put("attr",items.getJSONObject(4).getString("metadata"));
                        System.out.println("Get Strat time : "+items.getJSONObject(4).getString("data") );
                        hm1.put("value",startTime);
                        extracted_items.add(hm1);


                        arr = items.getJSONObject(6).getString("data").split(" ", 2);
                        hm2.put("attr",items.getJSONObject(6).getString("metadata"));
                        hm2.put("value",items.getJSONObject(6).getString("data"));


                    }
                    if( !"null".equals(items.getJSONObject(5).getString("data"))){
                        // tmp hash map for end Time
                        HashMap<String, String> hm1 = new HashMap<>();
                        endTime =items.getJSONObject(5).getString("data");
                        hm1.put("attr",items.getJSONObject(5).getString("metadata"));
                        hm1.put("value",endTime);
                        extracted_items.add(hm1);
                    }
                    if(!hm2.isEmpty()){
                        extracted_items.add(hm2);
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
                        email = email.replace("5","s");
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
                    if(!items.getJSONObject(4).isNull("data")){
                        eSTime = items.getJSONObject(4).getString("data");
                    }
                    if(!items.getJSONObject(5).isNull("data")){
                        eSTime = items.getJSONObject(5).getString("data");
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
            //Toast.makeText(getApplicationContext(),"Image Data"+(dbhandler.getAllTitles()), Toast.LENGTH_LONG).show();
           //Log.d("Imagge Data :", dbhandler.getAllTitles().getString(1));
            adapter = new SimpleAdapter(
                    ExtractedDataActivity.this, extracted_items,
                    R.layout.list_item, new String[]{"attr", "value"}, new int[]{R.id.attr,
                    R.id.value});

            lv.setAdapter(adapter);
        }
    }
}

