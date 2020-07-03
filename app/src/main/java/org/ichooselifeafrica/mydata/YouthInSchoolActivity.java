package org.ichooselifeafrica.mydata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.ichooselifeafrica.mydata.constants.Urls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class YouthInSchoolActivity extends AppCompatActivity {

    EditText inputNames, inputAgentNo, inputAge, inputWard, inputSubCounty, inputCounty, inputSchool, inputForm;
    RadioGroup rgGender, rgReligion;
    String gender, religion;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youth_in_school);
        inputNames = findViewById(R.id.inputNames);
        inputAgentNo = findViewById(R.id.inputAgentNo);
        inputAge = findViewById(R.id.inputAge);
        inputWard = findViewById(R.id.inputWard);
        inputSubCounty = findViewById(R.id.inputSubCounty);
        inputCounty = findViewById(R.id.inputCounty);
        inputSchool = findViewById(R.id.inputSchool);
        inputForm = findViewById(R.id.inputForm);

        rgGender = findViewById(R.id.radioGroup);
        rgReligion = findViewById(R.id.radioGroup2);

        this.progress = new ProgressDialog(this);
        progress.setMessage("Processing ....");

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radioMale) {
                    gender = "Male";
                } else if (checkedId == R.id.radioFemale) {
                    gender = "Female";
                }
            }
        });

        rgReligion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radioChristian) {
                    religion = "Christian";
                } else if (checkedId == R.id.radioMuslim) {
                    religion = "Muslim";
                } else if (checkedId == R.id.radioOther) {
                    religion = "Other";
                }
            }
        });

    }

    public void submitInfo(View view) {
        //inputNames, inputAgentNo, inputAge, inputWard, inputSubCounty, inputCounty, inputSchool, inputForm
        String names = inputNames.getText().toString().trim();
        String agent_no = inputAgentNo.getText().toString().trim();
        String age = inputAge.getText().toString().trim();
        String ward = inputWard.getText().toString().trim();
        String sub_county = inputSubCounty.getText().toString().trim();
        String county = inputCounty.getText().toString().trim();
        String school = inputSchool.getText().toString().trim();
        String form = inputForm.getText().toString().trim();

        if (names.isEmpty() || agent_no.isEmpty() || age.isEmpty() || ward.isEmpty() || sub_county.isEmpty() || county.isEmpty() || school.isEmpty() || form.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("names", names);
        params.put("agent_no", agent_no);
        params.put("age", age);
        params.put("ward", ward);
        params.put("sub_county", sub_county);
        params.put("county", county);
        params.put("school", school);
        params.put("form", form);
        params.put("gender", gender);
        params.put("religion", religion);
        AsyncHttpClient client = new AsyncHttpClient();
        this.progress.show();
        client.post(Urls.SUBMIT_USER_INFO_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progress.dismiss();
                try {
                    if (response.getBoolean("success")){
                        Toast.makeText(YouthInSchoolActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        clearFields();
                    }else{
                        Toast.makeText(YouthInSchoolActivity.this, "Failed To Save", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
                Toast.makeText(YouthInSchoolActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void clearFields() {
        inputNames.setText("");
        inputAgentNo.setText("");
        inputAge.setText("");
        inputSubCounty.setText("");
        inputCounty.setText("");
        inputSchool.setText("");
        inputForm.setText("");
    }

    public void fillQuestions(View view) {
        Intent intent=new Intent(getApplicationContext(), QuestionnaireActivity.class);
        startActivity(intent);
    }
}
