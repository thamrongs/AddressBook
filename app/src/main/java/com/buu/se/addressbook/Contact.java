package com.buu.se.addressbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class Contact extends Activity implements View.OnClickListener {

    ArrayList<Addresses> conList;
    EditText name;
    EditText company;
    EditText address;
    EditText email;
    EditText tel;
    Button btn_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        int con_id = intent.getIntExtra("con_id", 0);
        String con_name = intent.getStringExtra("con_name");
        String con_company = intent.getStringExtra("con_company");
        String con_address = intent.getStringExtra("con_address");
        String con_email = intent.getStringExtra("con_email");
        String con_tel = intent.getStringExtra("con_tel");

        name = (EditText) findViewById(R.id.det_name);
        company = (EditText) findViewById(R.id.det_company);
        address = (EditText) findViewById(R.id.det_address);
        email = (EditText) findViewById(R.id.det_email);
        tel = (EditText) findViewById(R.id.det_tel);
        btn_edit = (Button) findViewById(R.id.btn_edit);

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
        btn_edit.setEnabled(false);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit:
                action_edit();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact, menu);
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
            btn_edit.setEnabled(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void action_edit(){
        name.setEnabled(false);
        company.setEnabled(false);
        address.setEnabled(false);
        email.setEnabled(false);
        tel.setEnabled(false);
        btn_edit.setEnabled(false);
    }
}
