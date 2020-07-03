package org.ichooselifeafrica.mydata;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.ichooselifeafrica.mydata.adapters.CustomAdapter;
import org.ichooselifeafrica.mydata.constants.Urls;
import org.ichooselifeafrica.mydata.models.Question;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class QuestionnaireActivity extends AppCompatActivity {
    ListView listQuestions;
    CustomAdapter customAdapter;
    ArrayList<Question> questionArrayList;
    EditText inputAgentNumber;
    TextView txtNames, txtSchool, txtCounty, txtWard, txtSubCounty;
    ProgressDialog progress;
    int youth_id;
    String name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        listQuestions = findViewById(R.id.listQuestions);
        questionArrayList = new ArrayList<>();
        customAdapter = new CustomAdapter(this, questionArrayList);
        inputAgentNumber = findViewById(R.id.inputAgentNumber);
        txtNames = findViewById(R.id.txtNames);
        txtSchool = findViewById(R.id.txtSchool);
        txtCounty = findViewById(R.id.txtCounty);
        txtWard = findViewById(R.id.txtWard);




        txtSubCounty = findViewById(R.id.txtSubcounty);
        listQuestions.setAdapter(customAdapter);
        this.progress = new ProgressDialog(this);
        progress.setMessage("Processing....");


    }

    public void fetchQuestions() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Urls.FETCH_QUESTIONS_INFO_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                questionArrayList.clear();
                try {
                    JSONArray jsonArray = response.getJSONArray("message");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject q = jsonArray.getJSONObject(i);
                        Question question = new Question(q.getInt("id"), q.getString("title"), q.getInt("answers"));
                        questionArrayList.add(question);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(QuestionnaireActivity.this, "Failed To Fetch", Toast.LENGTH_SHORT).show();
            }
        });
    }

    String TAG = "SERVER_SAYS";

    public void submitQuestions() {
        //close keyboard
        View z = this.getCurrentFocus();
        if (z != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(z.getWindowToken(), 0);
        }
        RequestParams params = new RequestParams();
        Gson gson = new Gson();
        String data = gson.toJson(customAdapter.getSelectedAnswers());
        Log.d(TAG, "submitQuestions: " + data);
        params.put("data", data);
        SharedPreferences prefs = this.getSharedPreferences("database", MODE_PRIVATE);
        String user_id = prefs.getString("user_id", "0");
        params.put("user_id", user_id);
        params.put("youth_id", youth_id);
        AsyncHttpClient client = new AsyncHttpClient();
        this.progress.show();
        client.post(Urls.SUBMIT_QUESTIONS_INFO_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progress.dismiss();
                try {
                    if (response.getBoolean("success")) {
                        questionArrayList.clear();
                        customAdapter.getSelectedAnswers().clear();
                        customAdapter.notifyDataSetChanged();
                        txtSchool.setVisibility(View.GONE);
                        txtSchool.setText("");
                        txtNames.setVisibility(View.GONE);
                        txtNames.setText("");
                        txtCounty.setVisibility(View.GONE);
                        txtCounty.setText("");
                        txtWard.setVisibility(View.GONE);
                        txtWard.setText("");
                        txtSubCounty.setVisibility(View.GONE);
                        txtSubCounty.setText("");
                        inputAgentNumber.setText("");

                        Toast.makeText(QuestionnaireActivity.this, "Successfully Submitted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QuestionnaireActivity.this, "Failed To Submit Responses", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
                Toast.makeText(QuestionnaireActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchAgent(View view) {
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
        client.post(Urls.FETCH_USER_INFO_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progress.dismiss();
                try {
                    if (response.getBoolean("success")) {
                        name = response.getJSONObject("message").getString("names");
                        youth_id = response.getJSONObject("message").getInt("id");

                        String county = response.getJSONObject("message").getString("county");
                        String sub = response.getJSONObject("message").getString("sub_county");
                        String school = response.getJSONObject("message").getString("school");
                        String ward = response.getJSONObject("message").getString("ward");
                        txtSchool.setVisibility(View.VISIBLE);
                        txtSchool.setText("School: "+school);
                        txtNames.setVisibility(View.VISIBLE);
                        txtNames.setText("Names: "+name);
                        txtCounty.setVisibility(View.VISIBLE);
                        txtCounty.setText("County: "+county);
                        txtWard.setVisibility(View.VISIBLE);
                        txtWard.setText("Ward: "+ward);
                        txtSubCounty.setVisibility(View.VISIBLE);
                        txtSubCounty.setText("Sub County :"+sub);
                        fetchQuestions();
                    } else {
                        Toast.makeText(QuestionnaireActivity.this, "No Record Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
                Toast.makeText(QuestionnaireActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_questionnaire, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.submit_answers == id) {
            submitQuestions();
        }
        return true;
    }
}
