package org.ichooselifeafrica.mydata.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.models.IndividualReport;

import java.util.ArrayList;

public class CustomAnswersAdapter extends BaseAdapter {
    Context context;
    ArrayList<IndividualReport> questionsList;
    LayoutInflater inflater;

    public CustomAnswersAdapter(Context context, ArrayList<IndividualReport> questionsList) {
        this.context = context;
        this.questionsList = questionsList;
        this.inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return questionsList.size();
    }

    @Override
    public Object getItem(int i) {
        return questionsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.individual_report_item_layout, null);
        TextView txtQuestion = view.findViewById(R.id.txtQuestion);
        TextView txtAnswer = view.findViewById(R.id.txtAnswer);
        TextView txtValue = view.findViewById(R.id.txtValue);
        IndividualReport r = questionsList.get(i);
        txtQuestion.setText(r.getQuestion());
        txtAnswer.setText(r.getAnswer());
        if (r.getText_value()!=null && !r.getText_value().contains("null")){
            txtValue.setVisibility(View.VISIBLE);
            txtValue.setText(r.getText_value());
        }
        return view;
    }
}
