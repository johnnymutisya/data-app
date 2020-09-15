package org.ichooselifeafrica.mydata.Reports;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.ichooselifeafrica.mydata.R;
import org.ichooselifeafrica.mydata.constants.Urls;
import org.ichooselifeafrica.mydata.models.Item;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContraceptiveReportActivity extends AppCompatActivity {
  TextView  txtQuestion, txt_1,txt_2,txt_3,txt_4,txt_5,txt_6;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contraceptive_report);
        txtQuestion=findViewById(R.id.txtQuestion);
        txt_1=findViewById(R.id.txt_1);
        txt_2=findViewById(R.id.txt_2);
        txt_3=findViewById(R.id.txt_3);
        txt_4=findViewById(R.id.txt_4);
        txt_5=findViewById(R.id.txt_5);
        txt_6=findViewById(R.id.txt_6);
        this.progress = new ProgressDialog(this);
        progress.setMessage("Processing....");
        txtQuestion.setText("Is the Change Agent using a contraceptive method?1f yes, which contraceptive method is she/he using?");
        fetch();
    }

    String TAG="SERVER_SAYS";
    private void fetch() {

        progress.show();
        AndroidNetworking.get(Urls.FETCH_SINGLE_REPORT)
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
                                    String text=itemObj.getString("item")+" : "+itemObj.getString("count");
                                    Log.d(TAG, "onResponse: "+text);
                                    if (i==0){
                                      txt_1.setText(text);
                                    }
                                    else if (i==1){
                                        txt_2.setText(text);
                                    }
                                    else if (i==2){
                                        txt_3.setText(text);
                                    }
                                    else if (i==3){
                                        txt_4.setText(text);
                                    } else if (i==4){
                                        txt_5.setText(text);
                                    } else if (i==5){
                                        txt_6.setText(text);
                                    }
                                }

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
}
