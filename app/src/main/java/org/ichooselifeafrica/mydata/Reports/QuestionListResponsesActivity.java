package org.ichooselifeafrica.mydata.Reports;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.adapters.CustomAdapter;
import org.ichooselifeafrica.mydata.adapters.CustomResponsesAdapter;
import org.ichooselifeafrica.mydata.constants.Urls;
import org.ichooselifeafrica.mydata.models.Question;
import org.ichooselifeafrica.mydata.models.Received;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuestionListResponsesActivity extends AppCompatActivity {
    ListView listQuestions;
    CustomResponsesAdapter customAdapter;
    ArrayList<Received> questionArrayList;
    ProgressDialog progress;
    String type="in";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list_responses);
        listQuestions = findViewById(R.id.list_responses);
        questionArrayList = new ArrayList<>();
        customAdapter = new CustomResponsesAdapter(this, questionArrayList);
        listQuestions.setAdapter(customAdapter);
        this.progress = new ProgressDialog(this);
        progress.setMessage("Processing....");
        type = getIntent().getStringExtra("type");
        Log.d("RESP_QUE", "onCreate: "+type);
        getData();
    }

    public void getData() {
        progress.show();
        AndroidNetworking.get(Urls.FETCH_ANALYSIS+type)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray mainObj = response.getJSONArray("message");
                                for (int i=0; i<mainObj.length(); i++){
                                    JSONObject obj = mainObj.getJSONObject(i);
                                    Log.d("RESP_QUE", "onResponse: "+obj.toString());
                                    String title= obj.getString("title");
                                    String yes= obj.getString("yes");
                                    String no= obj.getString("no");
                                    String undecided= obj.getString("undecided");
                                    String type= obj.getString("answers");
                                    Received received=new Received(title, yes, no, undecided);
                                    received.setAnswers(type);
                                    questionArrayList.add(received);
                                }
                                customAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed To Fetch Data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progress.dismiss();

                    }
                });
    }
}
