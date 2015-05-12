package com.buu.se.addressbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class Address extends Activity {

    ArrayList<Addresses> conList;
    SharedPreferences persondata;
    String fullurl;
    AddressAdapter adapter;
    ProgressDialog prgDialog;
    final int MYACTIVITY_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Intent intent = getIntent();
        String cat_id = intent.getStringExtra("cat_id");

        persondata = getSharedPreferences("persondata", Context.MODE_PRIVATE);
        fullurl = persondata.getString("baseurl", "http://192.168.1.7/addressbook/index.php/") + "contact/get";

        fullurl += "?cat_id="+cat_id;
        Toast.makeText(getApplicationContext(), cat_id + "", Toast.LENGTH_LONG).show();

        conList = new ArrayList<Addresses>();
        new JSONAsyncTask().execute(fullurl);

        ListView listview = (ListView) findViewById(R.id.list_address);
        adapter = new AddressAdapter(getApplicationContext(), R.layout.address_list, conList);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                // TODO Auto-generated method stub
                // Toast.makeText(getApplicationContext(), conList.get(position).getCon_id() + "", Toast.LENGTH_LONG).show();
                Intent data = new Intent(Address.this, Contact.class);
                data.putExtra("con_id", conList.get(position).getCon_id());
                data.putExtra("con_name", conList.get(position).getCon_name());
                data.putExtra("con_company", conList.get(position).getCon_company());
                data.putExtra("con_address", conList.get(position).getCon_address());
                data.putExtra("con_email", conList.get(position).getCon_email());
                data.putExtra("con_tel", conList.get(position).getCon_tel());

                startActivityForResult(data, MYACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Address.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);


                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("contacts");

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        Addresses con = new Addresses();

                        con.setCon_id(object.getInt("con_id"));
                        con.setCon_name(object.getString("con_name").matches("null") ? "" : object.getString("con_name"));
                        con.setCon_company(object.getString("con_company").matches("null") ? "" : object.getString("con_company"));
                        con.setCon_address(object.getString("con_address").matches("null") ? "" : object.getString("con_address"));
                        con.setCon_tel(object.getString("con_tel").matches("null") ? "" : object.getString("con_tel"));
                        con.setCon_email(object.getString("con_email").matches("null") ? "" : object.getString("con_email"));
                        con.setCon_image(object.getString("con_image").matches("null") ? "" : object.getString("con_image"));

                        conList.add(con);
                    }

                    return true;
                }

                //------------------>>

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            adapter.notifyDataSetChanged();

        }
    }
}
