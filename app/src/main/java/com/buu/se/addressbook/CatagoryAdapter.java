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
public class CatagoryAdapter extends ArrayAdapter<Catagories> {
    ArrayList<Catagories> catList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;

    public CatagoryAdapter(Context context, int resource, ArrayList<Catagories> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        catList = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convert view = design
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.catName = (TextView) v.findViewById(R.id.cat_name);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.catName.setText(catList.get(position).getCatName());

        return v;

    }

    static class ViewHolder {
        public TextView catName;

    }
}
