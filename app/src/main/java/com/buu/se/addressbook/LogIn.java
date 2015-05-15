package com.buu.se.addressbook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class LogIn extends Activity {

    //final int MYACTIVITY_REQUEST_CODE = 101;
    private EditText user, pass;
    private String username, password, baseurl, fullurl;
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        user = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);

        baseurl = "http://54.187.11.22/addressbook/index.php/";

        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickLogin(View v){

        if(isOnline()) {

            username = user.getText().toString();
            password = pass.getText().toString();

            if (!username.matches("") && !password.matches("")) {
                RequestParams params = new RequestParams();
                params.put("user", username);
                params.put("pass", password);
                fullurl = baseurl + "user/login";
                invokeWS(fullurl, params);
            } else {
                Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Network is Disconnect", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickRegister(View v){

        if(isOnline()) {
            Intent data = new Intent(LogIn.this, Register.class);

            SharedPreferences persondata = getSharedPreferences("persondata", MODE_PRIVATE);

            SharedPreferences.Editor editor = persondata.edit();
            editor.putString("baseurl", baseurl);
            editor.commit();

            startActivity(data);
        } else {
            Toast.makeText(getApplicationContext(), "Network is Disconnect", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void navigatetoHomeActivity(JSONObject obj) {
        Intent data = new Intent(LogIn.this, MainActivity.class);
        //startActivityForResult(data, MYACTIVITY_REQUEST_CODE);
        SharedPreferences persondata = getSharedPreferences("persondata", MODE_PRIVATE);
        try {
            SharedPreferences.Editor editor = persondata.edit();
            editor.putString("baseurl", baseurl);
            editor.putInt("usr_id", obj.getInt("usr_id"));
            editor.putString("usr_fname", obj.getString("usr_fname"));
            editor.putString("usr_lname", obj.getString("usr_lname"));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(data);
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
                        Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                        navigatetoHomeActivity(response.getJSONObject("data"));
                    }else {
                        Toast.makeText(getApplicationContext(), response.getJSONObject("data").getString("errormsg"), Toast.LENGTH_LONG).show();
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
