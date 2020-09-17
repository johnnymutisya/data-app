package org.ichooselifeafrica.mydata.utils;

import android.widget.Button;

import java.util.ArrayList;

public class AccessControl {
    public static boolean canSee(int user_level,String report){
        if (user_level==1){
            return  true;
        }else if (user_level==2 && (report.contains("regional manager"))){
             return  true;
        }
        else if (user_level==3 && (report.contains("master seal"))){
            return  true;
        }
        else if (user_level==4 && (report.contains("ordinary seal"))){
            return  true;
        }
        return false;
    }
}

