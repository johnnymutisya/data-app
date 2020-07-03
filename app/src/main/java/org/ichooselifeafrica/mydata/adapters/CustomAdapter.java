package org.ichooselifeafrica.mydata.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.models.Question;
import org.ichooselifeafrica.mydata.models.Response;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<Question> questionsList;
    LayoutInflater inflater;
    ArrayList<Response> selectedAnswers;

    public CustomAdapter(Context context, ArrayList<Question> questionsList) {
        this.context = context;
        this.questionsList = questionsList;
        this.selectedAnswers = new ArrayList<>();
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

        view = inflater.inflate(R.layout.question_layout, null);
        TextView txtQuestion = view.findViewById(R.id.txtQuestion);
        RadioButton radioYes = view.findViewById(R.id.radioYes);
        RadioButton radioNo = view.findViewById(R.id.radioNo);
        RadioButton radioNotAware = view.findViewById(R.id.radioNotAware);
        RadioGroup rg = view.findViewById(R.id.rg);

        final Question question = questionsList.get(i);

        radioYes.setChecked(question.firstAnswerChecked);
        radioNo.setChecked(question.secondAnswerChecked);
        radioNotAware.setChecked(question.thirdAnswerChecked);

        if (question.getAnswers() == 2) {
            radioNotAware.setVisibility(View.INVISIBLE);
        }
        txtQuestion.setText(question.getTitle());

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                try {
                    for (Response p : selectedAnswers) {
                        if (p.getQuestion_id() == question.getId()) {
                            selectedAnswers.remove(p);
                        }
                    }
                    int value = 0;
                    int question_id = question.getId();
                    if (checkedId == R.id.radioYes) {
                        value = 1;
                        question.firstAnswerChecked=true;
                    } else if (checkedId == R.id.radioNo) {
                        question.secondAnswerChecked=true;
                        value = 2;
                    } else if (checkedId == R.id.radioNotAware) {
                        question.thirdAnswerChecked=true;
                        value = 3;
                    }
                    selectedAnswers.add(new Response(question_id, value));
                }catch (Exception e){
                    Log.d("ERROR", "onCheckedChanged: Error doig concurrent modification");
                }


            }
        });
        return view;
    }

    public ArrayList<Response> getSelectedAnswers() {
        return selectedAnswers;
    }
}
