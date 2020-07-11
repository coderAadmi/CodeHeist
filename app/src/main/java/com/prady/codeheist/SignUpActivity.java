package com.prady.codeheist;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.prady.codeheist.fragments.EmailVerificationFragment;
import com.prady.codeheist.fragments.ProgressFragment;
import com.prady.codeheist.fragments.SignupFragment;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements EmailVerificationFragment.OnOtpVerifyClickedListener, SignupFragment.OnSignUpClickedListener {

    @BindView(R.id.fragment_container)
    RelativeLayout mFragmentContainer;

    @BindView(R.id.helper_txt)
    TextView mHelperText;

    @BindView(R.id.send_otp_button)
    MaterialButton mSendOtpButton;

    @BindView(R.id.phone_et)
    EditText mPhone;

    String email, password, uname, phoneNumber;

    private  EmailVerificationFragment emailVerificationFragment;
    private SignupFragment signupFragment;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mOtpResendToken;

    private FirebaseAuth mAuth;

    private boolean isLoggingIn;

    private ProgressFragment mProgressFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setStatusBarColor(Color.BLACK);

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        isLoggingIn = getIntent().getBooleanExtra("LOG_IN",false);

        init();
    }

    private void init()
    {
        mSendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhone.getText().toString();
                if(!isValid(phone))
                {
                    Toast.makeText(SignUpActivity.this,"Please input correct phone number",Toast.LENGTH_SHORT).show();
                    mHelperText.setVisibility(View.VISIBLE);
                    return;
                }
                mHelperText.setVisibility(View.GONE);
                sendOtp("+91"+phone);
            }
        });
    }

    private void inflateProgressFragment()
    {
        if(mProgressFragment == null)
            mProgressFragment = new ProgressFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container,mProgressFragment)
                .commit();
    }

    private void hideProgressFragment()
    {
        if(mProgressFragment!=null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mProgressFragment)
                    .commit();
            mProgressFragment = null;
        }
    }

    private void sendOtp(String phone)
    {
        phoneNumber = phone;
        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                        phone,
                        60L,
                        TimeUnit.SECONDS,
                        SignUpActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    if(emailVerificationFragment!=null)
                                    {
                                        emailVerificationFragment.setOtp(phoneAuthCredential.getSmsCode());
                                        return;
                                    }
                                    signInWithPhone(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                hideProgressFragment();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                super.onCodeSent(s, forceResendingToken);
                                mVerificationId = s;
                                mOtpResendToken = forceResendingToken;
                                emailVerificationFragment = new EmailVerificationFragment();
                                emailVerificationFragment.setPhoneText(phone);
                                hideProgressFragment();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.fragment_container,emailVerificationFragment)
                                        .commit();
                            }
                        }
                );
    }

    private void signInWithPhone(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            if(emailVerificationFragment!=null)
                            {
                                emailVerificationFragment.cancelCountDown();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .remove(emailVerificationFragment)
                                        .commit();
                                emailVerificationFragment = null;
                            }
                            FirebaseUser user  = mAuth.getCurrentUser();
                            if(user.getDisplayName()==null) {
                                Toast.makeText(SignUpActivity.this,"You are new, let's signup", Toast.LENGTH_SHORT).show();
                                signupFragment = new SignupFragment();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.fragment_container, signupFragment)
                                        .commit();
                            }
                            else
                                finishWithResult(0);
                        }
                        else
                        {
                            //show error message to user
                            Log.d("USER_SIGNUP",task.getException().getMessage());
                            if(task.getException()  instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                emailVerificationFragment.setWrongOtpMessage();
                                Toast.makeText(SignUpActivity.this,"Invalid Otp Entered",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private boolean isValid(String s)
    {
        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // 1) Begins with 0 or 91
        // 2) Then contains 7 or 8 or 9.
        // 3) Then contains 9 digits
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    @Override
    public void onOtpVerificationClicked(String otp) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId,otp);
        signInWithPhone(phoneAuthCredential);
    }

    @Override
    public void onResendOtpClicked() {
        emailVerificationFragment.cancelCountDown();
        getSupportFragmentManager().beginTransaction()
                .remove(emailVerificationFragment)
                .commit();
        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                        phoneNumber,
                        60L,
                        TimeUnit.SECONDS,
                        SignUpActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                if(emailVerificationFragment!=null)
                                {
                                    emailVerificationFragment.setOtp(phoneAuthCredential.getSmsCode());
                                    return;
                                }
                                signInWithPhone(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                hideProgressFragment();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                super.onCodeSent(s, forceResendingToken);
                                mVerificationId = s;
                                mOtpResendToken = forceResendingToken;
                                emailVerificationFragment = new EmailVerificationFragment();
                                emailVerificationFragment.setPhoneText(phoneNumber);
                                hideProgressFragment();
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.fragment_container,emailVerificationFragment)
                                        .commit();
                            }
                        }
                ,mOtpResendToken);
        //call resend otp
    }

    @Override
    public void onChangePhoneClicked() {
        //ask user if he wants to cancel or not.
        emailVerificationFragment.cancelCountDown();
        getSupportFragmentManager().beginTransaction()
                .remove(emailVerificationFragment)
                .commit();
    }

    @Override
    public void onSignUpButtonClicked(String email, String password, String uname) {

        //Progress Fragment to be inflated here
        ProgressBar progressBar = new ProgressBar(SignUpActivity.this);
        progressBar.setIndeterminate(true);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.BLUE));
        this.email = email;
        this.password = password;
        this.uname = uname;
            FirebaseUser user = mAuth.getCurrentUser();
            updateEmail(user);
    }

    private void updateEmail(FirebaseUser user)
    {
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.d("USER_SIGNUP","EMAIL UPDATED");
                            updatePassword(user);
                            return;
                        }
                        Toast.makeText(SignUpActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updatePassword(FirebaseUser user)
    {
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.d("USER_SIGNUP","EMAIL UPDATED");
                            updateUserName(user);
                            return;
                        }
                        //
                        finishWithResult(0);
                    }
                });
    }

    private void updateUserName(FirebaseUser user)
    {
       UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
       builder.setDisplayName(uname);
       builder.setPhotoUri(null);
       user.updateProfile(builder.build())
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful())
                       {
                           finishWithResult(0);
                        return;
                       }
                       //show error
                       finishWithResult(0);
                   }
               });
    }

    private void finishWithResult(int RESULT_CODE)
    {
        setResult(RESULT_OK);
        finishAfterTransition();
    }

}
