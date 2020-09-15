package org.ichooselifeafrica.mydata.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.models.Question;
import org.ichooselifeafrica.mydata.models.Response;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<Question> questionsList;
    LayoutInflater inflater;
    ArrayList<Response> selectedAnswers;
    HashMap<String, Response> resultMap;

    public CustomAdapter(Context context, ArrayList<Question> questionsList) {
        this.context = context;
        this.questionsList = questionsList;
        this.selectedAnswers = new ArrayList<>();
        this.inflater = (LayoutInflater.from(context));
        this.resultMap=new HashMap<>();
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
        final TextView txtQuestion = view.findViewById(R.id.txtQuestion);
        RadioButton radioYes = view.findViewById(R.id.radioYes);
        RadioButton radioNo = view.findViewById(R.id.radioNo);
        RadioButton radioNotAware = view.findViewById(R.id.radioNotAware);
        RadioGroup rg = view.findViewById(R.id.rg);

        final EditText inputValue = view.findViewById(R.id.inputValue);

        final Question question = questionsList.get(i);

        radioYes.setChecked(question.firstAnswerChecked);
        radioNo.setChecked(question.secondAnswerChecked);
        radioNotAware.setChecked(question.thirdAnswerChecked);

        if (question.getAnswers() == 0) {
            rg.setVisibility(View.GONE);
            radioNotAware.setVisibility(View.GONE);
            radioYes.setVisibility(View.GONE);
            radioNo.setVisibility(View.GONE);
            txtQuestion.setTextColor(Color.parseColor("#2a04d1"));
        }

        if (question.getAnswers() == 2 || question.getAnswers() == 24) {
            radioNotAware.setVisibility(View.INVISIBLE);
        }

        if (question.getAnswers() == 24) {
            //inputValue.setVisibility(View.VISIBLE);
            inputValue.setText(question.getInputValueAns());
        }


        txtQuestion.setText(question.getTitle());

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                try {
                 /*   for (Response p : selectedAnswers) {
                        if (p.getQuestion_id() == question.getId()) {
                            selectedAnswers.remove(p);
                        }
                    }*/
                    int value = 0;
                    int question_id = question.getId();
                    if (checkedId == R.id.radioYes) {
                        value = 1;
                        question.firstAnswerChecked = true;
                    } else if (checkedId == R.id.radioNo) {
                        question.secondAnswerChecked = true;
                        value = 2;
                    } else if (checkedId == R.id.radioNotAware) {
                        question.thirdAnswerChecked = true;
                        value = 3;
                    }
                   // notifyDataSetChanged();
                    Response res = new Response(question_id, value);
                    res.setQuestionType(question.getAnswers() + "");
                    resultMap.put("item_"+question_id, res);
                   // selectedAnswers.add(res);
                } catch (Exception e) {
                    Log.e("SERVER_SAYS", "onCheckedChanged: ", e);
                    Log.d("SERVER_SAYS", "onCheckedChanged: Error doing concurrent modification");
                }


            }
        });
        radioYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked && question.getAnswers() == 24) {
                    inputValue.setVisibility(View.VISIBLE);
                } else if (!isChecked && question.getAnswers() == 24) {
                    inputValue.setVisibility(View.GONE);
                }
            }
        });

        radioNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked && question.getAnswers() == 24) {
                    inputValue.setVisibility(View.GONE);
                    question.setInputValueAns("");
                    inputValue.setText("");

                } else if (!isChecked && question.getAnswers() == 24) {
                    inputValue.setVisibility(View.VISIBLE);
                }
               //notifyDataSetChanged();

            }
        });

        inputValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    question.setInputValueAns(charSequence.toString());
                    Log.d("SERVER_SAYS", "onTextChanged: "+charSequence.toString());
                    try {
                       /* for (Response p : selectedAnswers) {
                            if (p.getQuestion_id() == question.getId()) {
                                p.setInputVal(charSequence.toString());
                            }
                        }*/
                        for (String key:resultMap.keySet()) {
                            Response response = resultMap.get(key);
                            if (response.getQuestion_id()==question.getId()){
                                response.setInputVal(charSequence.toString());
                                Log.d(TAG, "onTextChanged: Saving To Item "+response.getQuestion_id());
                            }
                        }

                    } catch (Exception e) {
                        Log.e("SERVER_SAYS", "onTextChanged: ", e );
                    }
                   // notifyDataSetChanged();
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }
    String TAG ="SERVER_SAYS";
    public ArrayList<Response> getSelectedAnswers() {
        selectedAnswers.clear();
        for (String response_key:resultMap.keySet()) {
          selectedAnswers.add(resultMap.get(response_key));
            Log.d(TAG, "getSelectedAnswers: "+response_key);
            Log.d(TAG, "getSelectedAnswers: "+resultMap.get(response_key).getValue()+" : "+resultMap.get(response_key).getInputVal());
        }
        return selectedAnswers;
    }
}
