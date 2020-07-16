package com.prady.codeheist;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_img)
    CircularImageView mProfileImg;

    @BindView(R.id.edit_img_fab)
    FloatingActionButton mEditImgFab;

    @BindView(R.id.email)
    TextView mUsername;

    @BindView(R.id.profile_name)
    TextView mProfileName;

    @BindView(R.id.profile_email)
    TextView mProfileEmail;

    @BindView(R.id.profile_phone)
    TextView mProfilePhone;

    @BindView(R.id.name_edit_button)
    MaterialRadioButton mNameEditButton;

    @BindView(R.id.mail_edit_button)
    MaterialRadioButton mEmailEditButton;

    @BindView(R.id.phone_edit_button)
    MaterialRadioButton mPhoneEditButton;

    @BindView(R.id.my_answers_button)
    MaterialButton mMyAnswersButton;

    @BindView(R.id.my_questions_button)
    MaterialButton mMyQuestionsButton;

    @BindView(R.id.linked_accounts_button)
    MaterialButton mLinkedAccountsButton;

    @BindView(R.id.change_passsword_button)
    MaterialButton mChangePasswordButton;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        getWindow().setStatusBarColor(Color.BLACK);

        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        setSupportActionBar(mToolbar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //need to fetch username from somewhere........sheh
        if(user.getPhotoUrl()!=null)
        {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(mProfileImg);
        }
        if(user.getDisplayName()!=null)
            mProfileName.setText(user.getDisplayName());
        if(user.getEmail()!=null ) {
            if(user.getEmail().length()!=0)
            mProfileEmail.setText(user.getEmail());
        }
        if(user.getPhoneNumber()!=null ) {
            if(user.getPhoneNumber().length()!=0)
            mProfilePhone.setText(user.getPhoneNumber());
        }



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finishAfterTransition();
        return false;
    }
}
