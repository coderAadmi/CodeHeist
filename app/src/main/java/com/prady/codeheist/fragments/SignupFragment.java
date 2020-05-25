package com.prady.codeheist.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.prady.codeheist.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupFragment extends Fragment {

    public interface OnSignUpClickedListener{
        public void onSignUpButtonClicked(String email, String password, String uname);
    }

    private OnSignUpClickedListener mListener;

    @BindView(R.id.login_button)
    MaterialButton mSignupButton;


    @BindView(R.id.email_et)
    EditText mEmail;

    @BindView(R.id.username_et)
    EditText mUsername;

    @BindView(R.id.password_et)
    EditText mPassword;

    @BindView(R.id.confirm_password_et)
    EditText mConfirmPassword;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnSignUpClickedListener) context;
        if(mListener == null)
        {
            Log.d("USER_SIGNUP_LISTENER","LISTENER not implemented by activity");
            return ;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();
                String username = mUsername.getText().toString().trim();
                mListener.onSignUpButtonClicked(email,password,username);
            }
        });

    }
}
