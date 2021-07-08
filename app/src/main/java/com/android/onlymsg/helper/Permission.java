package com.android.onlymsg.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {
    public static boolean validatePermission(int requestCode, Activity activity, String[] permissions){
        if (Build.VERSION.SDK_INT >= 23){

            List<String> permissionList = new ArrayList<String>();

            //see if all the permissions are allowed
            for (String permission:permissions){
                Boolean checkPermission = ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED;
                if (!checkPermission) permissionList.add(permission);
            }

            if(permissionList.isEmpty())return true;

            String[] newPermissions = new String[permissionList.size()];
            permissionList.toArray(newPermissions);

            //Ask permission
            ActivityCompat.requestPermissions(activity,newPermissions,requestCode);

        }
        return true;
    }
}
