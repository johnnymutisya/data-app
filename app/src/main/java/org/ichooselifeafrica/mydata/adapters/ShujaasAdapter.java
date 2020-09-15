package org.ichooselifeafrica.mydata.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.models.Shujaa;

import java.util.ArrayList;

public class ShujaasAdapter extends BaseAdapter {
    Context context;
    ArrayList<Shujaa> shujaaArrayList;
    LayoutInflater inflater;

    public ShujaasAdapter(Context context, ArrayList<Shujaa> questionsList) {
        this.context = context;
        this.shujaaArrayList = questionsList;
        this.inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return shujaaArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return shujaaArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.shujaa_item_layout, null);
        TextView txtName = view.findViewById(R.id.txtNames);
        TextView txtCounty = view.findViewById(R.id.txtCounty);
        TextView txtSubCounty = view.findViewById(R.id.txtSubCounty);
        TextView txtGender = view.findViewById(R.id.txtGender);
        TextView txtSchool = view.findViewById(R.id.txtSchool);
        Shujaa s= shujaaArrayList.get(i);
        txtName.setText(s.getName());
        txtCounty.setText(s.getCounty());
        txtSubCounty.setText(s.getSub_county());
        txtGender.setText(s.getGender());
        txtSchool.setText(s.getSchool());
        return view;
    }
}
