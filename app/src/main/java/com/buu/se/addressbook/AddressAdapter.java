package com.buu.se.addressbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by thamrongs on 5/12/15 AD.
 */
public class AddressAdapter extends ArrayAdapter<Addresses> {
    ArrayList<Addresses> conList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;

    public AddressAdapter(Context context, int resource, ArrayList<Addresses> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        conList = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convert view = design
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.addName = (TextView) v.findViewById(R.id.add_name);
            holder.addTel = (TextView) v.findViewById(R.id.add_tel);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.addName.setText(conList.get(position).getCon_name());
        holder.addTel.setText(conList.get(position).getCon_tel());

        return v;

    }

    static class ViewHolder {
        public TextView addName;
        public TextView addTel;

    }
}

