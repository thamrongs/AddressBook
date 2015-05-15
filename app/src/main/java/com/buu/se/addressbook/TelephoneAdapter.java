package com.buu.se.addressbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by thamrongs on 5/15/15 AD.
 */

public class TelephoneAdapter extends ArrayAdapter<Telephones> {
    ArrayList<Telephones> telList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;

    public TelephoneAdapter(Context context, int resource, ArrayList<Telephones> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        telList = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convert view = design


        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.tel_number = (TextView) v.findViewById(R.id.tel_number);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.tel_number.setText(telList.get(position).getTel_phonenumber());
        return v;

    }

    static class ViewHolder {
        public TextView tel_number;

    }
}