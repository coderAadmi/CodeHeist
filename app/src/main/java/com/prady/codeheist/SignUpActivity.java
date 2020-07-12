package com.prady.codeheist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    private static final  int RC_SIGN_UP = 701;

    @BindView(R.id.signup_via_email)
    MaterialButton mSignupViaEmail;

    @BindView(R.id.signup_via_phone)
    MaterialButton mSignUpViaPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getWindow().setStatusBarColor(Color.BLACK);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        mSignupViaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mSignUpViaPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void signupViaEmail()
    {

    }

    private void signupViaPhone()
    {
        Intent intent= new Intent(SignUpActivity.this, SignInViaPhoneActivity.class);
        intent.putExtra("LOG_IN",true);
        startActivityForResult(intent,RC_SIGN_UP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_UP)
        {
            if(resultCode == RESULT_OK)
                finish();
        }
    }
}
