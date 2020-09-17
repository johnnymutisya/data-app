package org.ichooselifeafrica.mydata;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.ichooselifeafrica.mydata.Reports.ContraceptiveReportActivity;
import org.ichooselifeafrica.mydata.Reports.QuestionListResponsesActivity;
import org.ichooselifeafrica.mydata.Reports.QuestionResponseActivity;
import org.ichooselifeafrica.mydata.Reports.ShujaaYouthDataActivity;
import org.ichooselifeafrica.mydata.constants.Urls;
import org.ichooselifeafrica.mydata.utils.AccessControl;
import org.ichooselifeafrica.mydata.utils.ImageLoadingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class YouthInSchoolActivity extends AppCompatActivity {

    EditText inputNames, inputAgentNo, inputAge, inputSchool, inputForm;
    RadioGroup rgGender, rgReligion;
    String gender, religion;
    ProgressDialog progress;

    TextInputLayout textInputLayoutSchool, textInputLayoutForm, layoutHighestLevel, layoutHighestLevelYear;
    TextView txtMaritalStatus;
    RadioGroup radioGroupMaritalStatus, radioGroupSchooling;
    String [] sub_counties= {"Turkana West", "Mvita", "Laikipia West", "North Imenti", "Kangundo", "Kibra", "Garissa Town", "Ainabkoi", "Bomet Central", "Kisumu East"};

    String sub_county="";
    String maritalStatus = "";
    boolean inSchool = true;
    int MY_PERMISSIONS_REQUEST_CAMERA=1111;
    private ImageLoadingUtils utils;

    FancyButton allReportsBtn, ceo_reports_1, ceo_reports_2;

    String IMAGE_UPLOAD_URL="";

    Spinner county_spinner, subcounty_spinner, ward_spinner, spinnerEduLevel,spinnerYearOfCompletion;
    ArrayList<String> array_list_subcounties, array_list_ward;

    ArrayAdapter subCounty_adapter, ward_adapter;

    TextView labelHighestLevel, labelYearOfCompletion;
    int user_level=0;

    //Report buttons
    FancyButton btnShujaaResponses;
    FancyButton btnUptakeInSchools;
    FancyButton btnUptakeOutSchools;
    FancyButton btnContraceptiveReports;

    FancyButton btnMasterSealReports;
    FancyButton btnShujaasCapturedByEachSeal;
    FancyButton btnTotalServiceUptakePerItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youth_in_school);
        AndroidNetworking.initialize(getApplicationContext());
        inputNames = findViewById(R.id.inputNames);
        inputAgentNo = findViewById(R.id.inputAgentNo);
        inputAge = findViewById(R.id.inputAge);
        //inputWard = findViewById(R.id.inputWard);
        //inputSubCounty = findViewById(R.id.inputSubCounty);
        subcounty_spinner=findViewById(R.id.spinnerSubCounties);
        ward_spinner = findViewById(R.id.spinnerWard);
        spinnerEduLevel=findViewById(R.id.spinnerEduLevel);
        spinnerYearOfCompletion=findViewById(R.id.spinnerYearOfCompletion);
        //inputCounty = findViewById(R.id.inputCounty);
        inputSchool = findViewById(R.id.inputSchool);
        inputForm = findViewById(R.id.inputForm);
//        inputEduLevel = findViewById(R.id.inputEduLevel);
//        inputEduLevelCompletion = findViewById(R.id.inputEduLevelCompletion);
        allReportsBtn=findViewById(R.id.btn_all_reports);
        ceo_reports_1=findViewById(R.id.ceo_reports_1);
        ceo_reports_2=findViewById(R.id.ceo_reports_2);
        county_spinner=findViewById(R.id.spinner_county);

        utils = new ImageLoadingUtils(this);

        textInputLayoutSchool = findViewById(R.id.textInputLayoutSchool);
        textInputLayoutForm = findViewById(R.id.textInputLayoutForm);
//        layoutHighestLevel = findViewById(R.id.layoutHighestLevel);
//        layoutHighestLevelYear = findViewById(R.id.layoutHighestLevelYear);

        labelHighestLevel=findViewById(R.id.labelHighestLevel);
        labelYearOfCompletion=findViewById(R.id.labelYearOfCompletion);

        txtMaritalStatus = findViewById(R.id.txtMaritalStatus);

        radioGroupMaritalStatus = findViewById(R.id.radioGroupMaritalStatus);
        radioGroupSchooling = findViewById(R.id.radioGroupSchooling);

        array_list_subcounties =new ArrayList<>();
        array_list_ward=new ArrayList<>();

        subCounty_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, array_list_subcounties);//, ward_adapter
        ward_adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, array_list_ward);//, ward_adapter

        subCounty_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcounty_spinner.setAdapter(subCounty_adapter);

        ward_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ward_spinner.setAdapter(ward_adapter);

        //"regional manager" "master seal" "ordinary seal"




        county_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                sub_county=sub_counties[position];
                //inputSubCounty.setText(sub_county);
                //populate the subscounty spinner
                populateWards(sub_county);
                array_list_subcounties.clear();
               // Toast.makeText(YouthInSchoolActivity.this, "Subcounty "+sub_county, Toast.LENGTH_SHORT).show();
                if (position==0){
                    List<String> list_0 = Arrays.asList(sub_county);
                    array_list_subcounties.addAll(list_0);
                }
                else if (position==1){
                    List<String> list_0 = Arrays.asList(sub_county);
                    array_list_subcounties.addAll(list_0);
                }
                else if (position==2){
                    List<String> list_0 = Arrays.asList(sub_county);
                    array_list_subcounties.addAll(list_0);
                }
                else if (position==3){
                    List<String> list_0 = Arrays.asList(sub_county);
                    array_list_subcounties.addAll(list_0);
                }
                else if (position==4){
                    List<String> list_0 = Arrays.asList(sub_county);
                    array_list_subcounties.addAll(list_0);
                }
                else if (position==5){
                    List<String> list_0 = Arrays.asList(sub_county);
                    array_list_subcounties.addAll(list_0);
                }
                else if (position==6){
                    List<String> list_0 = Arrays.asList(sub_county);
                    array_list_subcounties.addAll(list_0);
                }
                else if (position==7){
                    List<String> list_0 = Arrays.asList(sub_county);
                    array_list_subcounties.addAll(list_0);
                }
                else if (position==8){
                    List<String> list_0 = Arrays.asList(sub_county);
                    array_list_subcounties.addAll(list_0);
                }
                else if (position==9){
                    List<String> list_0 = Arrays.asList(sub_county);
                    array_list_subcounties.addAll(list_0);
                }
                subCounty_adapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        subcounty_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String subcounty = subcounty_spinner.getSelectedItem().toString();
                populateWards(subcounty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ward_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        radioGroupSchooling.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radioInSchool) {
                    inSchool = true;
                    labelHighestLevel.setVisibility(View.GONE);
                    labelYearOfCompletion.setVisibility(View.GONE);
                    spinnerEduLevel.setVisibility(View.GONE);
                    spinnerYearOfCompletion.setVisibility(View.GONE);
                    txtMaritalStatus.setVisibility(View.GONE);
                    radioGroupMaritalStatus.setVisibility(View.GONE);
                    //show others
                    inputSchool.setVisibility(View.VISIBLE);
                    inputForm.setVisibility(View.VISIBLE);
                } else {
                    inSchool = false;
                    //vice versa
                    inputSchool.setVisibility(View.GONE);
                    inputForm.setVisibility(View.GONE);
                    labelHighestLevel.setVisibility(View.VISIBLE);
                    labelYearOfCompletion.setVisibility(View.VISIBLE);

                    spinnerEduLevel.setVisibility(View.VISIBLE);
                    spinnerYearOfCompletion.setVisibility(View.VISIBLE);
                    txtMaritalStatus.setVisibility(View.VISIBLE);
                    radioGroupMaritalStatus.setVisibility(View.VISIBLE);
                }

            }
        });

        radioGroupMaritalStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radioMarried) {
                    maritalStatus = "Married";
                } else if (checkedId == R.id.radioSingle) {
                    maritalStatus = "Single";
                } else if (checkedId == R.id.radioDivorced) {
                    maritalStatus = "Separated";
                }
            }
        });

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},  MY_PERMISSIONS_REQUEST_CAMERA);
                }

            }
        }
        SharedPreferences prefs = this.getSharedPreferences("database", MODE_PRIVATE);
        boolean authorized = prefs.getBoolean("authorized", false);
        user_level = prefs.getInt("user_level", 4);

        btnShujaaResponses = findViewById(R.id.btnShujaaResponses); //"ordinary seal"
        btnUptakeInSchools = findViewById(R.id.clean_report);// "ceo"
        btnUptakeOutSchools = findViewById(R.id.clean_report_2);// "ceo"
        btnContraceptiveReports = findViewById(R.id.btn_contraceptive);//ceo

        btnMasterSealReports=findViewById(R.id.btn_all_reports);//master seal,"regional manager"
        btnShujaasCapturedByEachSeal=findViewById(R.id.ceo_reports_1);//master seal,"regional manager"
        btnTotalServiceUptakePerItem=findViewById(R.id.ceo_reports_2);//ceo


        if (AccessControl.canSee(user_level,"ordinary seal")){
            btnShujaaResponses.setVisibility(View.VISIBLE);
        }
        if (AccessControl.canSee(user_level,"ceo")){
            btnUptakeInSchools.setVisibility(View.VISIBLE);
        }
        if (AccessControl.canSee(user_level,"ceo")){
            btnUptakeOutSchools.setVisibility(View.VISIBLE);
        }
        if (AccessControl.canSee(user_level,"ceo")){
            btnContraceptiveReports.setVisibility(View.VISIBLE);
        }
        if (AccessControl.canSee(user_level,"master seal regional manager")){
            btnMasterSealReports.setVisibility(View.VISIBLE);
        }
        if (AccessControl.canSee(user_level,"master seal regional manager")){
            btnShujaasCapturedByEachSeal.setVisibility(View.VISIBLE);
        }
        if (AccessControl.canSee(user_level,"ceo")){
            btnTotalServiceUptakePerItem.setVisibility(View.VISIBLE);
        }


    }

    private void populateWards(String subcounty) {
        array_list_ward.clear();
        //Silibwet, Singorwet, Ndarawetta, Chesoen & Mutarakwa
        if (subcounty.contains("Bomet Central")){
            List<String> list_0 = Arrays.asList("Silibwet township","Singorwet", "Longisa", "Kembu", "Ndarawetta", "Chemaner");
            array_list_ward.addAll(list_0);
        }
        else if (subcounty.contains("Mvita")){
            List<String> list_0 = Arrays.asList("Ganjoni/Shimanzi","Old Town","Tononoka","Old Town","Tudor","Majengo/Mwembe Tayari");
            array_list_ward.addAll(list_0);
        }
        else if (subcounty.contains("Kangundo")){
            List<String> list_0 = Arrays.asList("Kangundo West","Kangundo North","Kangundo Central","Kangundo East");
            array_list_ward.addAll(list_0);
        }
        else if (subcounty.contains("Ainabkoi")){
            List<String> list_0 = Arrays.asList("Ainabkoi Olare","Kapsoya","Kaptagat");
            array_list_ward.addAll(list_0);
        }
        else if (subcounty.contains("North Imenti")){
            List<String> list_0 = Arrays.asList("Ntima East","Ntima West","Nyaki West","Nyaki East");
            array_list_ward.addAll(list_0);
        }
        else if (subcounty.contains("Kisumu East")){
            List<String> list_0 = Arrays.asList("Kolwa East","Nyalenda A","Kajulu","Kolwa Central","Kolwa Central","Manyatta B");
            array_list_ward.addAll(list_0);
        }
        else if (subcounty.contains("Kibra")){
            List<String> list_0 = Arrays.asList("Woodley","Sarangombe","Makina","Lindi","Laini Saba");
            array_list_ward.addAll(list_0);
        }
        else if (subcounty.contains("Laikipia West")){
            List<String> list_0 = Arrays.asList("Igwamiti ward","Kiirita Ward","Igwamiti ward","Laikipia West","Rumuruti Ward");
            array_list_ward.addAll(list_0);
        }
        else if (subcounty.contains("Turkana West")){
            List<String> list_0 = Arrays.asList("Kakuma Ward","Lopur Ward","Letea Ward","Songot Ward","Kalobeyei Ward","Lokichoggio Ward","Nanaam Ward");
            array_list_ward.addAll(list_0);
        }
        else if (subcounty.contains("Garissa Town")){
            List<String> list_0 = Arrays.asList("Waberi","Galbet","Township","Iftin");
            array_list_ward.addAll(list_0);
        }
        ward_adapter.notifyDataSetChanged();
    }

    public void submitInfo(View view) {
        View z = this.getCurrentFocus();
        if (z != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(z.getWindowToken(), 0);
        }

        File file=null;
        if (mCurrentPhotoPath!=null && !mCurrentPhotoPath.isEmpty()){
            IMAGE_UPLOAD_URL=compressImage(mCurrentPhotoPath);
            file=new File(IMAGE_UPLOAD_URL);
        }





        //  new ImageCompression(this).execute(mCurrentPhotoPath);
        //inputNames, inputAgentNo, inputAge, inputWard, inputSubCounty, inputCounty, inputSchool, inputForm
        String names = inputNames.getText().toString().trim();
        String agent_no = inputAgentNo.getText().toString().trim();
        String age = inputAge.getText().toString().trim();
        String ward = ward_spinner.getSelectedItem().toString();
        String sub_county = subcounty_spinner.getSelectedItem().toString();
        String county = county_spinner.getSelectedItem().toString();
        String school = inSchool ? inputSchool.getText().toString().trim() : "N/A";
        String form = inSchool ? inputForm.getText().toString().trim() : "N/A";
        maritalStatus = inSchool ? "N/A" : maritalStatus;
        //inputEduLevel,inputEduLevelCompletion
        String highestLevel = inSchool ? "N/A" : spinnerEduLevel.getSelectedItem().toString();
        String yearCompletion = inSchool ? "N/A" : spinnerYearOfCompletion.getSelectedItem().toString();//inputEduLevelCompletion.getText().toString().trim();


        if (highestLevel.isEmpty() || names.isEmpty() || agent_no.isEmpty() || age.isEmpty() || ward.isEmpty() || sub_county.isEmpty() || county.isEmpty() || school.isEmpty() || form.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences prefs = this.getSharedPreferences("database", MODE_PRIVATE);
        String user_id = prefs.getString("user_id", "0");

        this.progress.show();

        AndroidNetworking.upload(Urls.SUBMIT_USER_INFO_URL)
                .addMultipartFile("image", file)
                .addMultipartParameter("names", names)
                .addMultipartParameter("user_id", user_id)
                .addMultipartParameter("agent_no", agent_no)
                .addMultipartParameter("age", age)
                .addMultipartParameter("ward", ward)
                .addMultipartParameter("sub_county", sub_county)
                .addMultipartParameter("county", county)
                .addMultipartParameter("school", school)
                .addMultipartParameter("form", form)
                .addMultipartParameter("gender", gender)
                .addMultipartParameter("religion", religion)
                .addMultipartParameter("marital_status", maritalStatus)
                .addMultipartParameter("highest_level", highestLevel)
                .addMultipartParameter("year_completion", yearCompletion)
                .setTag("ITEMS_UPLOAD")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(YouthInSchoolActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(YouthInSchoolActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                clearFields();
                            } else {
                                Toast.makeText(YouthInSchoolActivity.this, "Failed To Save", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(YouthInSchoolActivity.this, "Failed To Send Data"+anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        inputNames.setText("");
        inputAgentNo.setText("");
        inputAge.setText("");
        //inputSubCounty.setText("");
        county_spinner.setSelection(0);
        inputSchool.setText("");
        inputForm.setText("");
        radioGroupMaritalStatus.clearCheck();
        radioGroupSchooling.clearCheck();
        rgGender.clearCheck();
        rgReligion.clearCheck();
    }

    public void fillQuestions(View view) {
        Intent intent = new Intent(getApplicationContext(), QuestionnaireActivity.class);
        startActivity(intent);
    }

    public void getUserReports(View view) {
        Intent intent = new Intent(getApplicationContext(), ReportsActivity.class);
        startActivity(intent);
    }

    /*Image Uploads*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // main logic
        }
    }

    int REQUEST_IMAGE_CAPTURE = 3456;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            // imgUser.setImageBitmap(imageBitmap);
            galleryAddPic();
            Log.d(TAG, "onActivityResult: " + mCurrentPhotoPath);
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        //new ImageCompression(this).execute(mCurrentPhotoPath);
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    public void launch_cam(View view) {
        dispatchTakePictureIntent();
    }

    String TAG = "SUBMIT_USER_INFO";

    /*Image Uploads*/
    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath,options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16*1024];

        try{
            bmp = BitmapFactory.decodeFile(filePath,options);
        }
        catch(OutOfMemoryError exception){
            exception.printStackTrace();

        }
        try{
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        }
        catch(OutOfMemoryError exception){
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float)options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Users");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"+ System.currentTimeMillis() + ".jpg");
        Log.d("PICHA", ""+uriSting);
        return uriSting;

    }

    public void allReports(View view) {
        Intent startIntent=new Intent(getApplicationContext(),GeneratedReportsActivity.class);
        startActivity(startIntent);
    }

    public void ceo_reports_1(View view) {
        Intent startIntent=new Intent(getApplicationContext(),ShujaaYouthDataActivity.class);
        startActivity(startIntent);
    }

    public void ceo_reports_2(View view) {

        Intent startIntent=new Intent(getApplicationContext(),QuestionResponseActivity.class);
        startActivity(startIntent);
    }

    public void out_school(View view) {
        Intent startIntent=new Intent(getApplicationContext(),QuestionListResponsesActivity.class);
        startIntent.putExtra("type","out");
        startActivity(startIntent);
    }

    public void in_school(View view) {
        Intent startIntent=new Intent(getApplicationContext(),QuestionListResponsesActivity.class);
        startIntent.putExtra("type","in");
        startActivity(startIntent);
    }

    public void contraceptive_reports(View view) {
        Intent startIntent=new Intent(getApplicationContext(),ContraceptiveReportActivity.class);
        startActivity(startIntent);
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
