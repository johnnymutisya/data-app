package org.ichooselifeafrica.mydata.Reports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.ichooselifeafrica.mydata.LoginActivity;
import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.constants.Urls;
import org.ichooselifeafrica.mydata.models.Item;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuestionResponseActivity extends AppCompatActivity {
    EditText inputNames;
    TextView txtName, txtInSchool, txtOutSchool;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_response);
        this.progress = new ProgressDialog(this);
        progress.setMessage("Processing....");
        inputNames=findViewById(R.id.inputQuestionId);
        txtName =findViewById(R.id.txtQuestion);
        txtInSchool =findViewById(R.id.txtInSchool);
        txtOutSchool =findViewById(R.id.txtOutSchool);
    }

    String TAG="QUESTION_DATA";

    public void searchQuestion(View view){

        AndroidNetworking.get(Urls.FETCH_SHUJAA_RESPONSE+inputNames.getText().toString())
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: "+response.toString());
                        progress.dismiss();
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject mainObj = response.getJSONObject("message");
                                txtName.setText(mainObj.getString("title"));
                                txtInSchool.setText("In School :"+mainObj.getString("in_school"));
                                txtOutSchool.setText("Out Of School :"+mainObj.getString("out_school"));
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
                        anError.printStackTrace();
                        Log.e(TAG, "onError: "+anError.getErrorBody() );
                        Log.e(TAG, "onError: "+anError.getErrorCode() );
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                        Log.e(TAG, "onError: "+anError.getLocalizedMessage() );
                        Toast.makeText(getApplicationContext(), "Failed To Send Data", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.logout_btn == id) {
            Intent x=new Intent(this, LoginActivity.class);
            startActivity(x);
            finish();
        }
        return true;
    }

}
