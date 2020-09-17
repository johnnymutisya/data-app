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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.ichooselifeafrica.mydata.LoginActivity;
import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.adapters.CustomReportsAdapter;
import org.ichooselifeafrica.mydata.adapters.ShujaasAdapter;
import org.ichooselifeafrica.mydata.constants.Urls;
import org.ichooselifeafrica.mydata.models.Item;
import org.ichooselifeafrica.mydata.models.Shujaa;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShujaaYouthDataActivity extends AppCompatActivity {

    EditText inputNames;
    TextView txtName;
    ListView listView;
    ArrayList<Shujaa> listItems;
    ProgressDialog progress;
    ShujaasAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shujaa_youth_data);

        listView=findViewById(R.id.reportList);
        inputNames=findViewById(R.id.inputQuestionId);
        txtName =findViewById(R.id.txtNames);

        listItems=new ArrayList<>();
        customAdapter=new ShujaasAdapter(this, listItems);
        listView.setAdapter(customAdapter);
        this.progress = new ProgressDialog(this);
        progress.setMessage("Processing....");

    }
    String TAG="REPORTS";

    public void searchAgent(View view) {

        AndroidNetworking.post(Urls.FETCH_USER_REPORTS_SHUJAAS_FOR_SEAL)
                .addBodyParameter("name", inputNames.getText().toString().trim())
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                        try {
                            if (response.getBoolean("success")) {
                                //TODO
                                JSONObject mainObj = response.getJSONObject("message");
                                txtName.setText(mainObj.getString("name"));
                                JSONArray array = mainObj.getJSONArray("youths");
                                for (int i=0; i<array.length(); i++){
                                    JSONObject itemObj = array.getJSONObject(i);
                                    // String name,county,sub_county,school, gender;
                                    String name= itemObj.getString("names");
                                    String county= itemObj.getString("county");
                                    String sub_county= itemObj.getString("sub_county");
                                    String school= itemObj.getString("school");
                                    String gender= itemObj.getString("gender");
                                    Shujaa shujaa=new Shujaa(name,county,sub_county,school, gender);
                                    listItems.add(shujaa);
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
                        anError.printStackTrace();
                        Log.e(TAG, "onError: "+anError.getErrorBody() );
                        Log.e(TAG, "onError: "+anError.getErrorCode() );
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                        Log.e(TAG, "onError: "+anError.getLocalizedMessage() );
                        Toast.makeText(getApplicationContext(), "Error. Failed To Fetch Data", Toast.LENGTH_SHORT).show();
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
