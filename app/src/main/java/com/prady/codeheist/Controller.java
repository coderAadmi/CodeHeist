package com.prady.codeheist;


import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class Controller {

    public interface OnPermissionGrantedListener{
        public void onPermissionGranted();
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }
        Log.i("update_statut", "Network is available : FALSE ");
        return false;
    }

    public static void askForPermission(String[] permissions, AppCompatActivity context, int reqCode, OnPermissionGrantedListener listener)
    {
        if(ContextCompat.checkSelfPermission(context,permissions[0])!= PackageManager.PERMISSION_GRANTED)
        {
            //no permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(context.shouldShowRequestPermissionRationale(permissions[0])){
                    //show info why
                    Toast.makeText(context,"These permissions are required to get image form camera",Toast.LENGTH_SHORT).show();
                }
                    context.requestPermissions(permissions,reqCode);
            }
        }
        else
        {
            //listener activity
            listener.onPermissionGranted();
        }
    }
}
