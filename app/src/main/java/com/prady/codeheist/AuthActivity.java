package com.prady.codeheist;

import android.content.Intent;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.api.LogDescriptor;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.prady.codeheist.fragments.EmailVerificationFragment;
import com.prady.codeheist.fragments.ProgressFragment;
import com.prady.codeheist.fragments.SignupFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity  {


    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions mGso;

    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 101;

    @BindView(R.id.signin_google)
    CircularImageView mSignInWithGoogle;

    @BindView(R.id.signin_facebook)
    CircularImageView mSignInWithFacebook;

    @BindView(R.id.signin_github)
    CircularImageView mSignInWithGithub;

    @BindView(R.id.login_button)
    MaterialButton mLoginButton;

    @BindView(R.id.username_email_et)
    EditText mEmail;

    @BindView(R.id.password_et)
    EditText mPassword;

    @BindView(R.id.forgot_password_button)
    MaterialButton mForgotPasswordButton;

    @BindView(R.id.progress_tv)
    TextView mProgressTv;

    @BindView(R.id.auth_progress)
    ProgressBar mAuthProgress;

    @BindView(R.id.login_with_phone)
            MaterialButton mLoginPhone;

    @BindView(R.id.signup_contianer)
            RelativeLayout mContainer;

    CallbackManager mCallbackManager;

    OAuthProvider.Builder mGitAuthProvider;

    private static final  int RC_SIGN_UP = 701;

    private ProgressFragment mProgressFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        getWindow().setStatusBarColor(Color.BLACK);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            updateUI(user);
        }
    }

    private void inflateProgressFragment()
    {
        mAuthProgress.setVisibility(View.VISIBLE);
        mProgressTv.setVisibility(View.VISIBLE);
        mContainer.setVisibility(View.VISIBLE);
        if(mProgressFragment == null)
        {
            mProgressFragment = new ProgressFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.signup_contianer,mProgressFragment)
                .commit();
    }

    private void hideProgressFragment()
    {
        mAuthProgress.setVisibility(View.GONE);
        mProgressTv.setVisibility(View.GONE);
        mContainer.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .remove(mProgressFragment)
                .commit();
    }

    private void init() {

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("USER_FB", "SUCCESS: " + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("USER_FB", "CANCELED ");
                Toast.makeText(AuthActivity.this,"You canceled facebook signin",Toast.LENGTH_SHORT).show();
                hideProgressFragment();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("USER_FB", "ERROR: " + error);
                Toast.makeText(AuthActivity.this,"Error occured: "+error,Toast.LENGTH_SHORT).show();
                hideProgressFragment();
            }
        });


        //sign in facebook
        mSignInWithFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inflateProgressFragment();

                LoginManager.getInstance().logInWithReadPermissions(AuthActivity.this,
                        Arrays.asList(new String[]{"user_photos", "email", "user_birthday", "public_profile"}));
            }
        });

        //sign in with google
        mSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(AuthActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
                inflateProgressFragment();

                initGoogleAuth();
                signInWithGoogle();
            }
        });

        //sign in with github
        mGitAuthProvider = OAuthProvider.newBuilder("github.com");
        mGitAuthProvider.addCustomParameter("login", "your-email@gmail.com");
        List<String> scopes =
                new ArrayList<String>() {
                    {
                        add("user:email");
                    }
                };
        mGitAuthProvider.setScopes(scopes);

        mSignInWithGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateProgressFragment();

                signInWithGit();
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                if(!isValidEmail(email) || email.length()<6)
                {
                    Toast.makeText(AuthActivity.this,"Please enter valid email or username",Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = mPassword.getText().toString();
                if(password.length()<8)
                {
                    Toast.makeText(AuthActivity.this,"Please enter valid password",Toast.LENGTH_SHORT).show();
                    return;
                }

                inflateProgressFragment();

                signInWithEmailPassword(email, password);
            }
        });

        mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AuthActivity.this,"Feature under development",Toast.LENGTH_SHORT).show();
                return;
            }
        });


        mLoginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AuthActivity.this,SignUpActivity.class);
                intent.putExtra("LOG_IN",true);
                startActivityForResult(intent,RC_SIGN_UP);
            }
        });

    }

    public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private void signInWithEmailPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("USER_EP", "signInWithEmail:success: " + mAuth.getCurrentUser().isEmailVerified());
                            FirebaseUser user = mAuth.getCurrentUser();

                            hideProgressFragment();

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("USER_EP_FAIL", "signInWithEmail:failure", task.getException());
                            Toast.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            hideProgressFragment();
//                            updateUI(null);
                            // ...
                        }
                        // ...
                    }
                });
    }

    private void signInWithGit() {
        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                    Log.d("USER_GIT", ": " + mAuth.getCurrentUser().isEmailVerified());
                                    AuthCredential credential = authResult.getCredential();
                                    Log.d("USER_GIT_TOKEN", credential.toString());

                                    hideProgressFragment();
                                    updateUI(mAuth.getCurrentUser());
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    Log.d("USER_GIT", "ERROR: " + e.getMessage());
                                    Toast.makeText(AuthActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    hideProgressFragment();
                                }
                            });
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.

            mAuth.startActivityForSignInWithProvider(/* activity= */ this, mGitAuthProvider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
//                                    Log.d("USER_GIT", authResult.getAdditionalUserInfo().getProfile().toString());
                                    Log.d("USER_GIT", ": " + mAuth.getCurrentUser().isEmailVerified());
                                    Log.d("USER_GIT_TOKEN", authResult.getCredential().toString());

                                    hideProgressFragment();
                                    updateUI(mAuth.getCurrentUser());
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    Log.d("USER_GIT", "ERROR: " + e.getMessage());
                                    Toast.makeText(AuthActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    hideProgressFragment();
                                }
                            });
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("USER_FB", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("USER_FB", "signInWithCredential:success " + mAuth.getCurrentUser().isEmailVerified());
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("USER_FB", user.getPhotoUrl().toString() + " " + user.getProviderId());
                            hideProgressFragment();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("USER_FB", "signInWithCredential:failure", task.getException());
//                            Toast.makeText(AuthActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(AuthActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            hideProgressFragment();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void initGoogleAuth() {
        GoogleSignInOptions mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(AuthActivity.this, mGso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("USER_G", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Toast.makeText(AuthActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                Log.w("USER_G", "Google sign in failed : " + e.getMessage());
                hideProgressFragment();
            }
        }
        else if(requestCode == RC_SIGN_UP)
        {
            if(resultCode == RESULT_OK)
                updateUI(mAuth.getCurrentUser());
        }
        else {
            //facebook
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("USER_S", "GOOGLE_SIGN_IN_SUCCESSFULL : " + mAuth.getCurrentUser().isEmailVerified());
                            Toast.makeText(AuthActivity.this, "Google sign in successfull", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("USER_D", "details: " + user.getDisplayName() + " : " + user.getPhoneNumber() + " : " + user.getPhotoUrl());
                            hideProgressFragment();
                            updateUI(user);
//                            mAuth.signOut();
                        } else {
                            Log.d("USER_S", "FAILED: " + task.getException().getMessage());
                            Toast.makeText(AuthActivity.this, "Google sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            hideProgressFragment();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("USER_S", "Error: " + e.getMessage());
                        Toast.makeText(AuthActivity.this, "Google sign in error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressFragment();
                    }
                });
    }

    private void updateUI(FirebaseUser user)
    {
        Intent intent = new Intent(AuthActivity.this,HomeActivity.class);
        startActivity(intent);
        finishAfterTransition();
    }
}
