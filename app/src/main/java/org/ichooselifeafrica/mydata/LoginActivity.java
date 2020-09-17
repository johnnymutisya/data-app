package org.ichooselifeafrica.mydata;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.ichooselifeafrica.mydata.Reports.PerWardActivity;
import org.ichooselifeafrica.mydata.constants.Urls;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        this.progress = new ProgressDialog(this);
        progress.setMessage("Processing ....");
        SharedPreferences prefs = this.getSharedPreferences("database", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        inputEmail.setText(email);
        //inputPassword.setText("password");
    }

    public void login(View view) {
        View z = this.getCurrentFocus();
        if (z != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(z.getWindowToken(), 0);
        }
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if (email.isEmpty()|| password.isEmpty()){
            Toast.makeText(this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.show();
        AndroidNetworking.post(Urls.LOGIN_URL)
                .addBodyParameter("email",email)
                .addBodyParameter("password",password)
                .setTag("ITEMS_UPLOAD")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        try {
                            if (response.getBoolean("success"))
                            {
                                SharedPreferences.Editor prefs = LoginActivity.this.getSharedPreferences("database", MODE_PRIVATE).edit();
                                prefs.putString("user_id", response.getJSONObject("message").getString("id"));
                                prefs.putString("name", response.getJSONObject("message").getString("name"));
                                prefs.putString("email", response.getJSONObject("message").getString("email"));
                                prefs.putInt("user_level", response.getJSONObject("message").getInt("authorized"));
                                prefs.putBoolean("authorized", response.getJSONObject("message").getInt("authorized")==1 || response.getJSONObject("message").getInt("authorized")==2);
                                prefs.commit();
                                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getApplicationContext(), YouthInSchoolActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progress.dismiss();
                        Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
/*
        AsyncHttpClient client=new AsyncHttpClient();
        RequestParams params=new RequestParams();
        params.put("email",email);
        params.put("password",password);
        this.progress.show();
        client.post(Urls.LOGIN_URL,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progress.dismiss();
                try {
                    if (response.getBoolean("success"))
                    {
                        SharedPreferences.Editor prefs = LoginActivity.this.getSharedPreferences("database", MODE_PRIVATE).edit();
                        prefs.putString("user_id", response.getJSONObject("message").getString("id"));
                        prefs.putString("name", response.getJSONObject("message").getString("name"));
                        prefs.putString("email", response.getJSONObject("message").getString("email"));
                        prefs.putInt("user_level", response.getJSONObject("message").getInt("authorized"));
                        prefs.putBoolean("authorized", response.getJSONObject("message").getInt("authorized")==1 || response.getJSONObject("message").getInt("authorized")==2);
                        prefs.commit();
                        Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(), YouthInSchoolActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    public void Reports(View view) {
        Intent startIntent=new Intent(getApplicationContext(),GeneratedReportsActivity.class);
        startActivity(startIntent);

    }
}
