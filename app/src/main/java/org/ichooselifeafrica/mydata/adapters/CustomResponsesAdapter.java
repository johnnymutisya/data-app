package org.ichooselifeafrica.mydata.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.models.Item;
import org.ichooselifeafrica.mydata.models.Received;

import java.util.ArrayList;

public class CustomResponsesAdapter extends BaseAdapter {
    Context context;
    ArrayList<Received> reportsList;
    LayoutInflater inflater;

    public CustomResponsesAdapter(Context context, ArrayList<Received> reportsList) {
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
        view = inflater.inflate(R.layout.response_item, null);
        TextView txtQuestion = view.findViewById(R.id.txtQuestion);
        TextView txtYes = view.findViewById(R.id.txtYes);
        TextView txtNo = view.findViewById(R.id.txtNo);
        TextView txtUndecided = view.findViewById(R.id.txtUndecided);
        Received r = reportsList.get(i);
        txtQuestion.setText(r.getQuestion());
        txtYes.setText("Responses For Yes :"+r.getYes());
        txtNo.setText("Responses For No :"+r.getNo());

        if (r.getAnswers().equals("3")) {
            txtUndecided.setVisibility(View.VISIBLE);
            txtUndecided.setText("Responses For Not Aware :"+r.getUndecided());
        }
        return view;
    }
}
