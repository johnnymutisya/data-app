package org.ichooselifeafrica.mydata;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.ichooselifeafrica.mydata.constants.Urls;
import org.ichooselifeafrica.mydata.utils.ImageCompression;
import org.ichooselifeafrica.mydata.utils.ImageLoadingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class YouthInSchoolActivity extends AppCompatActivity {

    EditText inputNames, inputAgentNo, inputAge, inputWard, inputSubCounty, inputCounty, inputSchool, inputForm, inputEduLevel, inputEduLevelCompletion;
    RadioGroup rgGender, rgReligion;
    String gender, religion;
    ProgressDialog progress;

    TextInputLayout textInputLayoutSchool, textInputLayoutForm, layoutHighestLevel, layoutHighestLevelYear;
    TextView txtMaritalStatus;
    RadioGroup radioGroupMaritalStatus, radioGroupSchooling;

    String maritalStatus = "";
    boolean inSchool = true;
    int MY_PERMISSIONS_REQUEST_CAMERA=1111;
    private ImageLoadingUtils utils;

    String IMAGE_UPLOAD_URL="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youth_in_school);
        AndroidNetworking.initialize(getApplicationContext());
        inputNames = findViewById(R.id.inputNames);
        inputAgentNo = findViewById(R.id.inputAgentNo);
        inputAge = findViewById(R.id.inputAge);
        inputWard = findViewById(R.id.inputWard);
        inputSubCounty = findViewById(R.id.inputSubCounty);
        inputCounty = findViewById(R.id.inputCounty);
        inputSchool = findViewById(R.id.inputSchool);
        inputForm = findViewById(R.id.inputForm);
        inputEduLevel = findViewById(R.id.inputEduLevel);
        inputEduLevelCompletion = findViewById(R.id.inputEduLevelCompletion);

        utils = new ImageLoadingUtils(this);

        textInputLayoutSchool = findViewById(R.id.textInputLayoutSchool);
        textInputLayoutForm = findViewById(R.id.textInputLayoutForm);
        layoutHighestLevel = findViewById(R.id.layoutHighestLevel);
        layoutHighestLevelYear = findViewById(R.id.layoutHighestLevelYear);

        txtMaritalStatus = findViewById(R.id.txtMaritalStatus);

        radioGroupMaritalStatus = findViewById(R.id.radioGroupMaritalStatus);
        radioGroupSchooling = findViewById(R.id.radioGroupSchooling);


        radioGroupSchooling.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.radioInSchool) {
                    inSchool = true;
                    layoutHighestLevel.setVisibility(View.GONE);
                    layoutHighestLevelYear.setVisibility(View.GONE);
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

                    layoutHighestLevel.setVisibility(View.VISIBLE);
                    layoutHighestLevelYear.setVisibility(View.VISIBLE);
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


    }

    public void submitInfo(View view) {
        View z = this.getCurrentFocus();
        if (z != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(z.getWindowToken(), 0);
        }

        IMAGE_UPLOAD_URL=compressImage(mCurrentPhotoPath);




        //  new ImageCompression(this).execute(mCurrentPhotoPath);
        //inputNames, inputAgentNo, inputAge, inputWard, inputSubCounty, inputCounty, inputSchool, inputForm
        String names = inputNames.getText().toString().trim();
        String agent_no = inputAgentNo.getText().toString().trim();
        String age = inputAge.getText().toString().trim();
        String ward = inputWard.getText().toString().trim();
        String sub_county = inputSubCounty.getText().toString().trim();
        String county = inputCounty.getText().toString().trim();
        String school = inSchool ? inputSchool.getText().toString().trim() : "N/A";
        String form = inSchool ? inputForm.getText().toString().trim() : "N/A";
        maritalStatus = inSchool ? "N/A" : maritalStatus;
        //inputEduLevel,inputEduLevelCompletion
        String highestLevel = inSchool ? "N/A" : inputEduLevel.getText().toString().trim();
        String yearCompletion = inSchool ? "N/A" : inputEduLevelCompletion.getText().toString().trim();


        if (highestLevel.isEmpty() || yearCompletion.isEmpty() || names.isEmpty() || agent_no.isEmpty() || age.isEmpty() || ward.isEmpty() || sub_county.isEmpty() || county.isEmpty() || school.isEmpty() || form.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        this.progress.show();
        File file=new File(IMAGE_UPLOAD_URL);
        AndroidNetworking.upload(Urls.SUBMIT_USER_INFO_URL)
                .addMultipartFile("image", file)
                .addMultipartParameter("names", names)
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
                       /* anError.printStackTrace();
                        Log.e(TAG, "onError: "+anError.getErrorBody() );
                        Log.e(TAG, "onError: "+anError.getErrorCode() );
                        Log.e(TAG, "onError: "+anError.getErrorDetail() );
                        Log.e(TAG, "onError: "+anError.getLocalizedMessage() );*/
                        Toast.makeText(YouthInSchoolActivity.this, "Failed To Send Data"+anError.getErrorBody(), Toast.LENGTH_SHORT).show();
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

}
