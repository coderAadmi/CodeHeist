package com.prady.codeheist.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

        public void onResendOtpClicked();

        public void onChangePhoneClicked();
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

    @BindView(R.id.wrong_otp_tv)
    TextView mWrongOTPTextView;

    private OnOtpVerifyClickedListener  mListener;

    private String phoneText;

    private CountDownTimer countDownTimer;

    private boolean isTimeOver;

    public void setPhoneText(String phone)
    {
        phoneText = phoneText;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnOtpVerifyClickedListener) context;
    }

    public void setOtp(String code)
    {
        mOTPInput.setText(code);
        countDownTimer.cancel();
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
        mPhone.setText(phoneText);
        isTimeOver = false;
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isTimeOver)
                mListener.onOtpVerificationClicked(mOTPInput.getText().toString());
                else
                {
                    Toast.makeText(getActivity(),"Please resend otp.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mResendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResendOtpClicked();
            }
        });

        mChangePhoneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onChangePhoneClicked();
            }
        });

        countDownTimer = new CountDownTimer(59000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimer.setText(millisUntilFinished/1000+"");
            }

            @Override
            public void onFinish() {
                isTimeOver = true;
                Toast.makeText(getActivity(),"Time over, Please resend the otp.",Toast.LENGTH_LONG).show();
                mProgress.setVisibility(View.GONE);
            }
        };
        countDownTimer.start();

    }

    public void setWrongOtpMessage()
    {
        mWrongOTPTextView.setVisibility(View.VISIBLE);
        mOTPInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mWrongOTPTextView.setVisibility(View.GONE);
                mOTPInput.removeTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void cancelCountDown()
    {
        countDownTimer.cancel();
        mTimer.setText("0");
        mProgress.setVisibility(View.GONE);
    }
}
