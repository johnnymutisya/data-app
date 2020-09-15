package org.ichooselifeafrica.mydata.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.models.Item;

import java.util.ArrayList;

public class CustomReportsAdapter extends BaseAdapter {
    Context context;
    ArrayList<Item> reportsList;
    LayoutInflater inflater;

    public CustomReportsAdapter(Context context, ArrayList<Item> reportsList) {
        this.context = context;
        this.reportsList = reportsList;
        this.inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return reportsList.size();
    }

    @Override
    public Object getItem(int i) {
        return reportsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.report_tem, null);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtCount = view.findViewById(R.id.txtTotalShujas);
        Item r = reportsList.get(i);
        txtTitle.setText(r.getTitle());
        txtCount.setText(r.getTotal()+"");
        return view;
    }
}
