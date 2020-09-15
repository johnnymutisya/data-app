package org.ichooselifeafrica.mydata;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.ichooselifeafrica.mydata.adapters.CustomAdapter;
import org.ichooselifeafrica.mydata.adapters.CustomAnswersAdapter;
import org.ichooselifeafrica.mydata.constants.Urls;
import org.ichooselifeafrica.mydata.models.IndividualReport;
import org.ichooselifeafrica.mydata.models.Question;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ReportsActivity extends AppCompatActivity {

    CustomAnswersAdapter customAdapter;
    ArrayList<IndividualReport> questionArrayList;
    EditText inputAgentNumber;
    ProgressDialog progress;
    ListView listQuestions;
    TextView txtNames;
    ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listQuestions = findViewById(R.id.listAnswers);
        questionArrayList = new ArrayList<>();
        customAdapter = new CustomAnswersAdapter(this, questionArrayList);
        inputAgentNumber = findViewById(R.id.inputAgentNumber);
        txtNames = findViewById(R.id.txtNames);
        imgUser=findViewById(R.id.imgUser);
        listQuestions.setAdapter(customAdapter);
        this.progress = new ProgressDialog(this);
        progress.setMessage("Processing....");


    }
    String TAG="DATA";
    public void getReports(View view) {
        //close keyboard
        View z = this.getCurrentFocus();
        if (z != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(z.getWindowToken(), 0);
        }

        String agentNo = inputAgentNumber.getText().toString().trim();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("agent_no", agentNo);
        this.progress.show();
        txtNames.setVisibility(View.GONE);
        questionArrayList.clear();
        client.post(Urls.FETCH_USER_REPORTS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progress.dismiss();

                try {
                    if (response.getBoolean("success")) {
                        //txtSchool, txtCounty, txtWard, txtSubCou
                        //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("SERVER_DATA", "onSuccess: "+response.toString());

                        JSONArray array = response.getJSONObject("message").getJSONArray("answers");
                        txtNames.setText(response.getJSONObject("message").getString("names"));
                        String image = response.getJSONObject("message").getString("image");
                        Log.d(TAG, "onSuccess: "+image);
                        if (image.length()>5){
                            Log.d(TAG, "onSuccess:"+ Urls.FETCH_USER_IMAGES+image);
                            imgUser.setVisibility(View.VISIBLE);
                            Picasso.with(ReportsActivity.this).load(Urls.FETCH_USER_IMAGES+image).fit().centerCrop().into(imgUser);
                        }
                        txtNames.setVisibility(View.VISIBLE);
                        for (int i = 0; i < array.length(); i++) {

                            JSONObject item = array.getJSONObject(i);
                            String question = item.getJSONObject("question").getString("title");
                            String answer = "";
                            String text_value = item.getString("text_value");

                            if (item.getString("value").equals("1")){
                                answer="Yes";
                            }
                            else if (item.getString("value").equals("2")){
                                answer="No";
                            }
                            else if (item.getString("value").equals("3")){
                                answer="Not Aware";
                            }else{
                                answer="N/A";
                            }

                            IndividualReport r = new IndividualReport(question, answer);
                            if (text_value.length()>0){
                                  r.setText_value(text_value);
                            }
                            questionArrayList.add(r);
                        }
                        customAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
