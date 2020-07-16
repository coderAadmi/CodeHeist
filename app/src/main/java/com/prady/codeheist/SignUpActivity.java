package com.prady.codeheist;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.profile_name)
    EditText mName;

    @BindView(R.id.email)
    EditText mEmail;

    @BindView(R.id.password)
    EditText mPassword;

    @BindView(R.id.confirm_password_et)
    EditText mConfirmPassword;

    @BindView(R.id.checkBox)
    MaterialCheckBox mCheckBox;

    @BindView(R.id.signup_button)
    MaterialButton mSignup;

    @BindView(R.id.notify_user_tv)
    TextView mNotfiyTv;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getWindow().setStatusBarColor(Color.BLACK);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finishAfterTransition();
        return false;
    }

    private void init() {
        setSupportActionBar(mToolbar);
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private void signup() {
        if (mName.getText().toString().length() < 3) {
            mNotfiyTv.setText("Name must be atleast of 3 characters");
            return;
        }

        if (!isValidEmail(mEmail.getText().toString())) {
            mNotfiyTv.setText("Email address is invalid.");
            return;
        }

        if (!mPassword.getText().toString().equals(mConfirmPassword.getText().toString())) {
            mNotfiyTv.setText("Password must match confirm password.");
            return;
        }

        if (!mCheckBox.isChecked()) {
            mNotfiyTv.setText("Please check the agreement policy.");
            return;
        }

        mNotfiyTv.setVisibility(View.GONE);
        signUpWithEmailPassword(mEmail.getText().toString(), mPassword.getText().toString());
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private void signUpWithEmailPassword(String email, String password) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance()
                                    .getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(mName.getText().toString())
                                    .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("SIGN_UP", "User profile updated.");
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mNotfiyTv.setVisibility(View.VISIBLE);
                                                                    mNotfiyTv.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                                                                    mNotfiyTv.setText("Please verify your account by clicking on the email link sent to your email id,");
                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(SignUpActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                                    Log.d("SIGNUP_102",task.getException().getMessage());
                                                                }
                                                                FirebaseAuth.getInstance().signOut();
                                                            }
                                                        });
                                            }
                                            else
                                            {
                                                FirebaseAuth.getInstance().signOut();
                                                Toast.makeText(SignUpActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                Log.d("SIGNUP_101",task.getException().getMessage());
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUpActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            Log.d("SIGNUP_101",task.getException().getMessage());
                            mNotfiyTv.setVisibility(View.VISIBLE);
                            mNotfiyTv.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                            mNotfiyTv.setText(task.getException().getMessage());
                        }
                    }
                });
    }

}
