package com.prady.codeheist.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.prady.codeheist.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmailVerificationFragment extends Fragment {

    public interface OnOtpVerifyClickedListener{
        public void onOtpVerificationClicked(String otp);
    }

    @BindView(R.id.phone_tv)
    TextView mPhone;

    @BindView(R.id.timer_tv)
    TextView mTimer;

    @BindView(R.id.progressBar)
    ProgressBar mProgress;

    @BindView(R.id.resend_otp_button)
    MaterialButton mResendOtpButton;

    @BindView(R.id.verify_otp_button)
    MaterialButton mVerifyButton;

    @BindView(R.id.chnage_phone_no)
    MaterialCardView mChangePhoneCard;

    @BindView(R.id.otp_input_et)
    EditText mOTPInput;

    OnOtpVerifyClickedListener  mListener;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnOtpVerifyClickedListener) context;
    }

    public void setOtp(String code)
    {
        mOTPInput.setText(code);
        mVerifyButton.performClick();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_verification,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onOtpVerificationClicked(mOTPInput.getText().toString());
            }
        });
    }
}
