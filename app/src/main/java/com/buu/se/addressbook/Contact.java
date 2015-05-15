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

import java.util.ArrayList;


public class Contact extends Activity implements View.OnClickListener {

    ArrayList<Addresses> conList;
    EditText name;
    EditText company;
    EditText address;
    EditText email;
    EditText tel;
    Button btn_edit, btn_add, btn_delete;
    int con_id;
    String cat_id;
    SharedPreferences persondata;
    String baseurl;
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        con_id = intent.getIntExtra("con_id", 0);
        cat_id = intent.getStringExtra("cat_id");

        String con_name = intent.getStringExtra("con_name");
        String con_company = intent.getStringExtra("con_company");
        String con_address = intent.getStringExtra("con_address");
        String con_email = intent.getStringExtra("con_email");
        String con_tel = intent.getStringExtra("con_tel");

        persondata = getSharedPreferences("persondata", Context.MODE_PRIVATE);
        baseurl = persondata.getString("baseurl", "http://192.168.1.7/addressbook/index.php/");

        name = (EditText) findViewById(R.id.det_name);
        company = (EditText) findViewById(R.id.det_company);
        address = (EditText) findViewById(R.id.det_address);
        email = (EditText) findViewById(R.id.det_email);
        tel = (EditText) findViewById(R.id.det_tel);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_delete = (Button) findViewById(R.id.btn_delete);

        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        if(con_id != 0) {
            name.setText(con_name);
            company.setText(con_company);
            address.setText(con_address);
            email.setText(con_email);
            tel.setText(con_tel);

            name.setEnabled(false);
            company.setEnabled(false);
            address.setEnabled(false);
            email.setEnabled(false);
            tel.setEnabled(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_contact, menu);

        MenuItem item = menu.findItem(R.id.action_edit);

        if (con_id == 0) {
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
            company.setEnabled(true);
            address.setEnabled(true);
            email.setEnabled(true);
            tel.setEnabled(true);
            btn_edit.setVisibility(View.VISIBLE);
            btn_delete.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void action_edit(){


        String con_name = name.getText().toString();
        String con_company = company.getText().toString();
        String con_address = address.getText().toString();
        String con_email = email.getText().toString();
        String con_tel = tel.getText().toString();

        if (!con_name.matches("") && !con_tel.matches("")) {
            RequestParams params = new RequestParams();
            params.put("con_id", con_id);
            params.put("con_name", con_name);
            params.put("con_company", con_company);
            params.put("con_address", con_address);
            params.put("con_email", con_email);
            params.put("con_tel", con_tel);
            params.put("cat_id", cat_id);

            String fullurl = baseurl + "contact/update";
            invokeWS(fullurl, params);

            name.setEnabled(false);
            company.setEnabled(false);
            address.setEnabled(false);
            email.setEnabled(false);
            tel.setEnabled(false);
            btn_edit.setVisibility(View.GONE);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }
    }

    public void action_add(){
        String con_name = name.getText().toString();
        String con_company = company.getText().toString();
        String con_address = address.getText().toString();
        String con_email = email.getText().toString();
        String con_tel = tel.getText().toString();

        if (!con_name.matches("") && !con_tel.matches("")) {

            RequestParams params = new RequestParams();
            params.put("con_name", con_name);
            params.put("con_company", con_company);
            params.put("con_address", con_address);
            params.put("con_email", con_email);
            params.put("con_tel", con_tel);
            params.put("cat_id", cat_id);

            String fullurl = baseurl + "contact/insert";
            invokeWS(fullurl, params);
            finish();

        } else {
            Toast.makeText(getApplicationContext(), "Please fill the Name and Tel.", Toast.LENGTH_LONG).show();
        }

    }

    public void action_delete(){
        if (con_id != 0) {
            RequestParams params = new RequestParams();
            params.put("con_id", con_id);
            String url = baseurl + "contact/delete";
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
