package com.prady.codeheist.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.card.MaterialCardView;
import com.prady.codeheist.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseOptionDialog extends DialogFragment {

    public static final int OPTION_CANCEL = -1;
    public static final int OPTION_CAMERA = 0;
    public static final int OPTION_GALLERY = 1;

    boolean isDialogCancelled;

    public interface OnDialogOptionSelectedListener{
           public void onDialogOptionSelected(int item);
    }

    OnDialogOptionSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnDialogOptionSelectedListener) context;
        if(listener == null)
        {
            Log.d("ERROR: ","Listener not implemented");
            return;
        }
    }

    @BindView(R.id.cancel)
    MaterialCardView mCancel;

    @BindView(R.id.camera)
    MaterialCardView mCamera;

    @BindView(R.id.gallery)
    MaterialCardView mGallery;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_chooser_vew,null);
        ButterKnife.bind(this,view);
        builder.setView(view);
        init();
        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(!isDialogCancelled)
        listener.onDialogOptionSelected(OPTION_CANCEL);
    }

    private void init()
    {
        isDialogCancelled = false;
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialogCancelled = true;
                listener.onDialogOptionSelected(OPTION_CANCEL);
                getDialog().dismiss();
            }
        });

        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialogCancelled = true;
                listener.onDialogOptionSelected(OPTION_CAMERA);
                getDialog().dismiss();
            }
        });

        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialogCancelled = true;
                listener.onDialogOptionSelected(OPTION_GALLERY);
                getDialog().dismiss();
            }
        });
    }
}
