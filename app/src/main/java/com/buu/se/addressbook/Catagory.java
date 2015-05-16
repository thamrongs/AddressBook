package com.buu.se.addressbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class Catagory extends Activity  implements View.OnClickListener {

    EditText name;
    Button btn_edit, btn_add, btn_delete;
    SharedPreferences persondata;
    String baseurl;
    ProgressDialog prgDialog;
    int cat_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_catagory);

        Intent intent = getIntent();
        cat_id = intent.getIntExtra("cat_id", 0);
        String cat_name = intent.getStringExtra("cat_name");


        persondata = getSharedPreferences("persondata", Context.MODE_PRIVATE);
        baseurl = persondata.getString("baseurl", "http://54.187.11.22/addressbook/index.php/");

        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        name = (EditText) findViewById(R.id.cat_name);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_delete = (Button) findViewById(R.id.btn_delete);

        if(cat_id != 0) {
            name.setText(cat_name);
            name.setEnabled(false);

            //btn_edit.setEnabled(false);
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
            btn_add.setVisibility(View.GONE);
        } else {
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
            btn_add.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catagory, menu);

        MenuItem item = menu.findItem(R.id.action_edit);

        if (cat_id == 0) {
            item.setVisible(false);
        } else {
            item.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            name.setEnabled(true);

            btn_edit.setVisibility(View.VISIBLE);
            btn_delete.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit:
                action_edit();
                break;
            case R.id.btn_delete:
                action_delete();
                break;
            case R.id.btn_add:
                action_add();
                break;
        }
    }

    public void action_edit(){
        String cat_name = name.getText().toString();
        int usr_id = persondata.getInt("usr_id", 0);

        if (!cat_name.matches("")) {
            RequestParams params = new RequestParams();
            params.put("cat_id", cat_id);
            params.put("cat_name", cat_name);
            params.put("usr_id", usr_id);

            String fullurl = baseurl + "catagory/update";
            invokeWS(fullurl, params);

            name.setEnabled(false);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }
    }

    public void action_add(){
        String cat_name = name.getText().toString();

        if (!cat_name.matches("")) {

            int usr_id = persondata.getInt("usr_id", 0);
            RequestParams params = new RequestParams();
            params.put("cat_name", cat_name);
            params.put("usr_id", usr_id);

            String fullurl = baseurl + "catagory/insert";
            invokeWS(fullurl, params);
            finish();

        } else {
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }


    }

    public void action_delete(){
        if (cat_id != 0) {
            RequestParams params = new RequestParams();
            params.put("cat_id", cat_id);
            String url = baseurl + "catagory/delete";
            invokeWS(url, params);
            finish();

        } else {
            Toast.makeText(getApplicationContext(), "Don't leave any field blank", Toast.LENGTH_LONG).show();
        }


    }

    public void invokeWS(String url, RequestParams params){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setConnectTimeout(5000);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                prgDialog.hide();
                try {
                    if (response.getJSONObject("data").getBoolean("status")) {
                        Toast.makeText(getApplicationContext(), response.getJSONObject("data").getString("msg"), Toast.LENGTH_LONG).show();
                    } else {
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
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Cannot connect to server. \nPlease make sure IP are correct.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
