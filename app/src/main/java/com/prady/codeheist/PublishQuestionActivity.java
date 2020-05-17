package com.prady.codeheist;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prady.codeheist.adaptors.AnswerEditorListAdapter;
import com.prady.codeheist.datamodels.Answer;
import com.prady.codeheist.datamodels.QuestionTitle;
import com.prady.codeheist.dialogs.ChooseOptionDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PublishQuestionActivity extends AppCompatActivity implements AnswerEditorListAdapter.OnAnswerListUpdatedListener, ChooseOptionDialog.OnDialogOptionSelectedListener {

    @BindView(R.id.add)
    MaterialButton mAdd;

    @BindView(R.id.ans_text_input_layout1)
    TextInputLayout mAnsText;

    @BindView(R.id.ans_list)
    RecyclerView mAnsListView;

    @BindView(R.id.question_text)
    TextInputLayout mQuestionTextView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab)
    FloatingActionButton mFAB;

    @BindView(R.id.main_layout)
    CoordinatorLayout mMainView;

    @BindView(R.id.queston_title_textview)
    TextView mQuestionTitle;

    @BindView(R.id.postAnswer)
    MaterialButton mPostAnswer;

    @BindView(R.id.cancel)
    MaterialButton mCancel;

    @BindView(R.id.img_view)
    ImageView mImgView;

    @BindView(R.id.remove_img_view)
    AppCompatImageView mRemoveImgView;


    AnswerEditorListAdapter answerListAdapter;

    Bitmap bitmap;

    QuestionTitle questionTitle;

    boolean isAsking;
    boolean isPostingQuestion;

    int imgUploadedCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_publish_question);
        ButterKnife.bind(this);
        isAsking = getIntent().getBooleanExtra("IS_ASKING", false);
        isPostingQuestion = isAsking;
        if (questionTitle == null) {
            questionTitle = getIntent().getParcelableExtra("Q_TITLE");
        }
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pusblish_question_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("DATA_", "item clicked  " + item.getItemId());
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return false;
    }

    private void init() {
        imgUploadedCount = 0;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addEndIconListenerOnAnswerEditor();
        if (!isAsking) {
            mQuestionTextView.setVisibility(GONE);
            mQuestionTitle.setVisibility(VISIBLE);
            mQuestionTitle.setText(questionTitle.getTitle());
        } else {
            mPostAnswer.setText("Post Question");
            questionTitle = new QuestionTitle();
            addEndIconListenerOnQuestionEditor();
        }
        answerListAdapter = new AnswerEditorListAdapter(this);
        mAnsListView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mAnsListView.setAdapter(answerListAdapter);

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImgView.getVisibility() == VISIBLE) {
                    addImgtoAnswer();
                } else {
                    String text = mAnsText.getEditText().getText().toString();
                    text = text.trim();
                    if (text == null || text.length() == 0) {
                        mAnsText.setError("Please enter something");
                        return;
                    }
                    Log.d("DATA_ADDED", text);
                    mFAB.setExpanded(false);
                    answerListAdapter.addText("tex: " + text);
                    mAnsText.getEditText().setText(null);
                }

                answerListAdapter.notifyDataSetChanged();
                mAnsListView.smoothScrollToPosition(answerListAdapter.getItemCount() - 1);
                hideKeyBoard();
            }
        });

        mAnsText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
//                    mAnsText.removeOnEndIconChangedListener();
                    mAnsText.setEndIconVisible(false);
                    mAnsText.clearOnEndIconChangedListeners();
                    mAnsText.setError(null);
                } else {
                    mAnsText.setEndIconVisible(true);
                    addEndIconListenerOnAnswerEditor();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImgView.getVisibility() == View.VISIBLE) {
                    mImgView.setImageBitmap(null);
                    mImgView.setVisibility(GONE);
                    mRemoveImgView.setVisibility(GONE);
                    mAnsText.setVisibility(View.VISIBLE);
                }
                mFAB.setExpanded(false);
                if (answerListAdapter.getItemCount() != 0)
                    mPostAnswer.setVisibility(VISIBLE);
                mAnsText.getEditText().setText(null);
                mAnsText.setError(null);
                hideKeyBoard();
            }
        });

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFAB.isExpanded()) {
                    mFAB.setExpanded(false);
                } else {
                    if (!isAsking) {
                        mFAB.setExpanded(true);
                        mPostAnswer.setVisibility(GONE);
                    } else {
                        Snackbar.make(mMainView, "Add question first", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mPostAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPostingQuestion)
                    postQuestion();
                else
                    postAnswer();
            }
        });

        mRemoveImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImgView.setImageBitmap(null);
                mImgView.setVisibility(GONE);
                mRemoveImgView.setVisibility(GONE);
                mAnsText.setVisibility(View.VISIBLE);
                mAnsText.setEndIconVisible(true);
            }
        });
    }

    private void addImgtoAnswer() {
        answerListAdapter.addImage(this.bitmap);
        mImgView.setImageBitmap(null);
        mImgView.setVisibility(GONE);
        mRemoveImgView.setVisibility(GONE);
        mAnsText.setVisibility(View.VISIBLE);
        mAnsText.setEndIconVisible(true);
//        mPostAnswer.setVisibility(VISIBLE);
        //call firebase cloud storage
    }

    private void addEndIconListenerOnQuestionEditor() {
        mQuestionTextView.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionTextView.getEditText().getText().length() == 0) {
                    mQuestionTextView.setError("Please enter question first");
                    return;
                }
                questionTitle.setTitle(mQuestionTextView.getEditText().getText().toString());
                mQuestionTextView.setVisibility(GONE);
                mQuestionTitle.setText(questionTitle.getTitle());
                mQuestionTitle.setVisibility(VISIBLE);
                mPostAnswer.setVisibility(VISIBLE);
                isAsking = false;
            }
        });
    }

    private void addEndIconListenerOnAnswerEditor() {

        mAnsText.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select an image
//                Toast.makeText(PublishQuestionActivity.this,"End icon clicked",Toast.LENGTH_LONG).show();
                mAnsText.setVisibility(GONE);
                ChooseOptionDialog dialog = new ChooseOptionDialog();
                dialog.show(getSupportFragmentManager(), null);
            }
        });
    }

    private void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mMainView.getWindowToken(), 0);
    }

    @Override
    public void onAnswerListUpdated(boolean isAnswerEmpty) {
        if (isAnswerEmpty) {
            mPostAnswer.setVisibility(GONE);
        } else {
            mPostAnswer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onImageListUpdated() {
        this.imgUploadedCount++;
    }

    private void postQuestion() {
        if (imgUploadedCount != answerListAdapter.getImages().size()) {
            Toast.makeText(this, "Please wait some images are uploading", Toast.LENGTH_SHORT).show();
            return;
        }
//        Answer answer = new Answer(questionTitle.getTitle(),"Poloman", "Poloman", "Anything right now", answerListAdapter.getTextList());
        questionTitle.setAnswerMap(new HashMap<>(answerListAdapter.getTextList()));
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        Timestamp timestamp = new Timestamp(new Date());
        fb.collection("question_titles")
                .document("QuestionById" + timestamp.toString())
                .set(questionTitle, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PublishQuestionActivity.this, "Question added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PublishQuestionActivity.this, "Exception occured: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("DATA_POST_Q", e.getMessage());
                    }
                });

    }


    private void postAnswer() {
        if (imgUploadedCount != answerListAdapter.getImages().size()) {
            Toast.makeText(this, "Please wait some images are uploading", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        Answer answer = new Answer(questionTitle.getTitle(), "Poloman", "Poloman", "Anything right now", answerListAdapter.getTextList());
        fb.collection("discussions")
                .document(questionTitle.getId())//q_doc_id for modular exponentiation
                .collection("answers")
                .add(answer)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(PublishQuestionActivity.this, "Answer added successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PublishQuestionActivity.this, "Exception occured: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("DATA_FAIL", e.getMessage());
                    }
                });
    }

    @Override
    public void onDialogOptionSelected(int item) {
        Intent intent = null;

        switch (item) {
            case ChooseOptionDialog.OPTION_CAMERA:
                 ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                Uri imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                startActivityForResult(intent, 1);
                return;
            case ChooseOptionDialog.OPTION_GALLERY:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
                return;
            case ChooseOptionDialog.OPTION_CANCEL:
                mAnsText.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 0:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    break;
                case 1:
                    Uri imgURI = data.getData();
                    if (imgURI != null) {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgURI);
                        } catch (IOException e) {
                            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    break;
            }
            this.bitmap = bitmap;
            Log.d("DATA_IMG",bitmap.getWidth()+" "+bitmap.getHeight());
            mImgView.setVisibility(View.VISIBLE);
            mRemoveImgView.setVisibility(View.VISIBLE);
//            mImgView.setImageBitmap(bitmap);
            Glide.with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.app_logo)
                    .into(mImgView);
            mAnsText.setEndIconVisible(false);
            mAnsText.clearOnEndIconChangedListeners();
        } else if (resultCode == RESULT_CANCELED) {
            mAnsText.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "File can not be selected", Toast.LENGTH_SHORT).show();
        }
    }
}
