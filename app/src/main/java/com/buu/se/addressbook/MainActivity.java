package com.buu.se.addressbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
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


public class MainActivity extends Activity {

    ArrayList<Catagories> catList;
    SharedPreferences persondata;
    String baseurl, fullurl;
    int usr_id;
    CatagoryAdapter adapter;
    ProgressDialog prgDialog;
    final int MYACTIVITY_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagory);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getList();
    }

    public void getList(){
        persondata = getSharedPreferences("persondata", Context.MODE_PRIVATE);
        baseurl = persondata.getString("baseurl", "http://54.187.11.22/addressbook/index.php/");
        fullurl = baseurl + "catagory/get";
        usr_id = persondata.getInt("usr_id", 0);
        fullurl += "?usr_id="+usr_id;

        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        catList = new ArrayList<Catagories>();
        new JSONAsyncTask().execute(fullurl);

        ListView listview = (ListView) findViewById(R.id.list_catagory);
        adapter = new CatagoryAdapter(getApplicationContext(), R.layout.catagory_list, catList);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                // TODO Auto-generated method stub
                //Toast.makeText(getApplicationContext(), catList.get(position).getCatId() + "", Toast.LENGTH_LONG).show();
                Intent data = new Intent(MainActivity.this, Address.class);
                data.putExtra("cat_id", String.valueOf(catList.get(position).getCatId()));
                startActivityForResult(data, MYACTIVITY_REQUEST_CODE);
            }
        });

        listview.setLongClickable(true);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long id) {
//                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
//                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
//                popupMenu.show();
//
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        if (item.getTitle().equals("Delete")) {
//                            if (catList.get(position).getCatId() != 0) {
//                                RequestParams params = new RequestParams();
//                                params.put("cat_id", catList.get(position).getCatId());
//                                String url = baseurl + "catagory/delete";
//                                invokeWS(url, params);
//
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Don't leave any field blank", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                        if (item.getTitle().equals("Edit")) {
//                            Intent data = new Intent(MainActivity.this, Catagory.class);
//                            data.putExtra("cat_id", catList.get(position).getCatId());
//                            data.putExtra("cat_name", catList.get(position).getCatName());
//                            startActivityForResult(data, MYACTIVITY_REQUEST_CODE);
//                        }
//                        return false;
//                    }
//                });

                Intent data = new Intent(MainActivity.this, Catagory.class);
                data.putExtra("cat_id", catList.get(position).getCatId());
                data.putExtra("cat_name", catList.get(position).getCatName());
                startActivityForResult(data, MYACTIVITY_REQUEST_CODE);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent data = new Intent(MainActivity.this, Catagory.class);
            startActivityForResult(data, MYACTIVITY_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);


                    JSONObject jsono = new JSONObject(data);
                    JSONArray jarray = jsono.getJSONArray("catagories");

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        Catagories cat = new Catagories();

                        cat.setCatId(object.getInt("cat_id"));
                        cat.setCatName(object.getString("cat_name"));

                        catList.add(cat);
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

    public void invokeWS(String url, RequestParams params){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setConnectTimeout(5000);
        client.post(url,params ,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                prgDialog.hide();
                try {
                    if(response.getJSONObject("data").getBoolean("status")){
                        Toast.makeText(getApplicationContext(), response.getJSONObject("data").getString("msg"), Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), response.getJSONObject("data").getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Cannot connect to server. \nPlease make sure IP are correct.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
