package com.buu.se.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


public class Contact extends Activity implements View.OnClickListener {

    ArrayList<Telephones> telList;
    EditText name;
    EditText company;
    EditText email;
    EditText phone;
    Button btn_edit, btn_add, btn_delete;
    ImageView add_telephone;
    int con_id, last_id, last_con_id;
    String cat_id;
    SharedPreferences persondata;
    String baseurl;
    ProgressDialog prgDialog;
    TelephoneAdapter adapter;
    ListView listview;
    private  AlertDialog.Builder alertDlg;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        con_id = intent.getIntExtra("con_id", 0);
        cat_id = intent.getStringExtra("cat_id");

        String con_name = intent.getStringExtra("con_name");
        String con_company = intent.getStringExtra("con_company");
        String con_email = intent.getStringExtra("con_email");

        persondata = getSharedPreferences("persondata", Context.MODE_PRIVATE);
        baseurl = persondata.getString("baseurl", "http://192.168.1.7/addressbook/index.php/");

        name = (EditText) findViewById(R.id.det_name);
        company = (EditText) findViewById(R.id.det_company);
        email = (EditText) findViewById(R.id.det_email);
        phone = (EditText) findViewById(R.id.add_phone);
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        add_telephone = (ImageView) findViewById(R.id.add_telephone);

        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        persondata = getSharedPreferences("persondata", Context.MODE_PRIVATE);
        String fullurl = persondata.getString("baseurl", "http://192.168.1.7/addressbook/index.php/") + "contact/get_telephone";

        fullurl += "?con_id="+con_id;

        telList = new ArrayList<Telephones>();
        new JSONAsyncTask().execute(fullurl);

        listview = (ListView) findViewById(R.id.list_telephone);
        adapter = new TelephoneAdapter(getApplicationContext(), R.layout.telephone_list, telList);

        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long id) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Delete")) {
                            if (telList.get(position).getTel_id() != 0) {
                                RequestParams params = new RequestParams();
                                params.put("tel_id", telList.get(position).getTel_id());
                                String url = baseurl + "contact/delete_telephone";
                                invokeWS(url, params);

                                telList.remove(position);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "Don't leave any field blank", Toast.LENGTH_LONG).show();
                            }
                        }
                        if (item.getTitle().equals("Edit")) {
                            createAlert(position, telList.get(position).getTel_id(), telList.get(position).getTel_phonenumber());
                            alert.show();

                        }
                        return false;
                    }
                });


                return true;
            }
        });

        if(con_id != 0) {
            name.setText(con_name);
            company.setText(con_company);
            email.setText(con_email);

            name.setEnabled(false);
            company.setEnabled(false);
            email.setEnabled(false);
            listview.setLongClickable(false);
            phone.setVisibility(View.GONE);
            add_telephone.setVisibility(View.GONE);
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
            btn_add.setVisibility(View.GONE);
        } else {
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
            btn_add.setVisibility(View.VISIBLE);
        }



    }

    public void createAlert(final int position, final int id, String telephone) {
        alertDlg = null;
        alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("Edit phone number")
                .setCancelable(false);
        final EditText input = new EditText(this);
        input.setId(R.id.telephone);
        input.setText(telephone);
        alertDlg.setView(input);

        alertDlg.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phonenumber = input.getText().toString();
                        if (!phonenumber.matches("")) {
                            RequestParams params = new RequestParams();
                            params.put("tel_id", id);
                            params.put("tel_phonenumber", phonenumber);

                            String fullurl = baseurl + "contact/update_telephone";
                            invokeWS(fullurl, params);
                            telList.get(position).setTel_phonenumber(phonenumber);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );

        alertDlg.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        alert = null;
        alert = alertDlg.create();
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
            case R.id.add_telephone:
                action_addphone();
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
            email.setEnabled(true);
            listview.setLongClickable(true);
            phone.setVisibility(View.VISIBLE);
            add_telephone.setVisibility(View.VISIBLE);
            btn_edit.setVisibility(View.VISIBLE);
            btn_delete.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void action_edit(){

        String con_name = name.getText().toString();
        String con_company = company.getText().toString();
        String con_email = email.getText().toString();

        if (!con_name.matches("")) {
            RequestParams params = new RequestParams();
            params.put("con_id", con_id);
            params.put("con_name", con_name);
            params.put("con_company", con_company);
            params.put("con_email", con_email);
            params.put("cat_id", cat_id);

            String fullurl = baseurl + "contact/update";
            invokeWS(fullurl, params);

            name.setEnabled(false);
            company.setEnabled(false);
            email.setEnabled(false);
            listview.setLongClickable(false);
            phone.setVisibility(View.GONE);
            add_telephone.setVisibility(View.GONE);
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
        } else {
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }
    }

    public void action_add(){
        String con_name = name.getText().toString();
        String con_company = company.getText().toString();
        String con_email = email.getText().toString();

        if (!con_name.matches("")) {
            RequestParams params = new RequestParams();
            if(con_id == 0) {
                params.put("con_id", last_con_id);
            }else {
                params.put("con_id", con_id);
            }
            params.put("con_name", con_name);
            params.put("con_company", con_company);
            params.put("con_email", con_email);
            params.put("cat_id", cat_id);

            String fullurl = baseurl + "contact/update";
            invokeWS(fullurl, params);

            name.setEnabled(false);
            company.setEnabled(false);
            email.setEnabled(false);
            listview.setLongClickable(false);
            phone.setVisibility(View.GONE);
            add_telephone.setVisibility(View.GONE);
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

    }

    public void action_addphone(){

        String phonenumber = phone.getText().toString();
        if (!phonenumber.matches("")) {

            RequestParams params = new RequestParams();
            params.put("tel_phonenumber", phonenumber);
            if(con_id == 0) {
                params.put("con_id", last_con_id);
            }else {
                params.put("con_id", con_id);
            }
            params.put("cat_id", cat_id);

            String fullurl = baseurl + "contact/insert_telephone";
            invokeWS(fullurl, params);

            Telephones tel = new Telephones();

            tel.setTel_id(last_id);
            tel.setTel_phonenumber(phonenumber);

            telList.add(tel);
            adapter.notifyDataSetChanged();
            phone.setText("");
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
                        if(response.getJSONObject("data").getInt("last_insert_id") != 0 && response.getJSONObject("data").getInt("last_con_id") != 0 ) {
                            last_id = response.getJSONObject("data").getInt("last_insert_id");
                            last_con_id = response.getJSONObject("data").getInt("last_con_id");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), response.getJSONObject("data").getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
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

    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Contact.this);
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
                    JSONArray jarray = jsono.getJSONArray("telephones");

                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        Telephones tel = new Telephones();

                        tel.setTel_id(object.getInt("tel_id"));
                        tel.setTel_phonenumber(object.getString("tel_phonenumber").matches("null") ? "" : object.getString("tel_phonenumber"));

                        telList.add(tel);
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
