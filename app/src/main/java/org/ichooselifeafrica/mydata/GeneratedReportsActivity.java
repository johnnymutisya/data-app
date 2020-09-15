package org.ichooselifeafrica.mydata;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.ichooselifeafrica.mydata.Reports.ByaAgeActivity;
import org.ichooselifeafrica.mydata.Reports.PerCountyActivity;
import org.ichooselifeafrica.mydata.Reports.PerSchoolActivity;
import org.ichooselifeafrica.mydata.Reports.PerSealActivity;
import org.ichooselifeafrica.mydata.Reports.PerSubcountyActivity;
import org.ichooselifeafrica.mydata.Reports.PerWardActivity;
import org.ichooselifeafrica.mydata.Reports.TotalCountiesActivity;

public class GeneratedReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_reports);
    }

    public void per_ward(View view) {
        Intent x=new Intent(this, PerWardActivity.class);
        startActivity(x);

    }

    public void per_school(View view) {
        Intent x=new Intent(this, PerSchoolActivity.class);
        startActivity(x);


    }

    public void per_sub_county(View view) {
        Intent x=new Intent(this, PerSubcountyActivity.class);
        startActivity(x);

    }

    public void per_county(View view) {
        Intent x=new Intent(this, PerCountyActivity.class);
        startActivity(x);


    }

    public void total_by_Age(View view) {
        Intent x=new Intent(this,ByaAgeActivity.class);
        startActivity(x);

    }

    public void per_seal(View view) {
        Intent x=new Intent(this,PerSealActivity.class);
        startActivity(x);


    }

    public void total_in_Counties(View view) {
        Intent x=new Intent(this, TotalCountiesActivity.class);
        startActivity(x);


    }
}
