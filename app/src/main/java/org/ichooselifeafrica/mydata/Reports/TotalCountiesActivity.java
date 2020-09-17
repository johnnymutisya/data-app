package org.ichooselifeafrica.mydata.Reports;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.ichooselifeafrica.mydata.LoginActivity;
import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.constants.Urls;
import org.json.JSONException;
import org.json.JSONObject;

public class TotalCountiesActivity extends AppCompatActivity {

    TextView txtTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_counties);
        txtTotal=findViewById(R.id.txtTotalShujas);
        fetch();

    }

    String TAG="TOTAL";
    private void fetch() {
        AndroidNetworking.get(Urls.FETCH_USER_REPORTS_SHUJAS)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        try {
                            if (response.getBoolean("success")) {
                                //TODO
                               int num = response.getInt("message");
                               txtTotal.setText(num+"");
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed To Fetch Data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
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
