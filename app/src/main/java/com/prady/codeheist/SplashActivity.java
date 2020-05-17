package com.prady.codeheist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prady.codeheist.datamodels.Question;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    boolean isOnStartCalled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(Color.BLACK);
        isOnStartCalled = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isOnStartCalled)
        FirebaseFirestore.getInstance()
                .collection("users")
                .document("x5LXPkwEIIWtvA7fOgxF")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(SplashActivity.this,"Profile info counldn't be loaded",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DocumentSnapshot doc = task.getResult();
                        SharedPreferences.Editor editor = getSharedPreferences("USER",MODE_PRIVATE).edit();
                        editor.putString("NAME",doc.get("name").toString());
                        editor.putString("U_NAME",doc.get("username").toString());
                        editor.putString("U_IMG",doc.get("image").toString());
                        editor.apply();
                        editor.commit();

                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DATA_ERROR",e.getMessage());
                        Toast.makeText(SplashActivity.this,"Profile info counldn't be loaded",Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Poloman");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Poloman");
                        bundle.putString("Exception",e.getMessage());
                        FirebaseAnalytics.getInstance(SplashActivity.this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    }
                });
        isOnStartCalled = true;
    }

}
