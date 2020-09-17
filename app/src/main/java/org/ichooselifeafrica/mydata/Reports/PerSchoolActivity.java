package org.ichooselifeafrica.mydata.Reports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.ichooselifeafrica.mydata.LoginActivity;
import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.adapters.CustomReportsAdapter;
import org.ichooselifeafrica.mydata.constants.Urls;
import org.ichooselifeafrica.mydata.models.Item;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PerSchoolActivity extends AppCompatActivity {
    ListView listView;
    CustomReportsAdapter customAdapter;
    ArrayList<Item> listItems;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_school);
        listView = findViewById(R.id.reportList);
        listItems=new ArrayList<>();
        customAdapter=new CustomReportsAdapter(this, listItems);
        listView.setAdapter(customAdapter);
        this.progress = new ProgressDialog(this);
        progress.setMessage("Processing....");

        fetchData();
    }
    String TAG="FETCHING_DATA";

    private void fetchData() {
        AndroidNetworking.get(Urls.FETCH_USER_REPORTS_PER_SCHOOL)
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
                                JSONArray array = response.getJSONArray("message");
                                for (int i=0; i<array.length(); i++){
                                    JSONObject itemObj = array.getJSONObject(i);
                                    String title= itemObj.getString("school");
                                    int count= itemObj.getInt("total");
                                    Item item=new Item(title,count);
                                    if (!title.contains("N/A")){
                                        listItems.add(item);
                                    }

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
