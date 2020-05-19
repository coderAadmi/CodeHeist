package com.prady.codeheist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.prady.codeheist.adaptors.AnswerEditorListAdapter;
import com.prady.codeheist.adaptors.AnswersListAdapter;
import com.prady.codeheist.datamodels.Answer;
import com.prady.codeheist.datamodels.QuestionTitle;
import com.prady.codeheist.datamodels.Reaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TopicProblemsActivity extends AppCompatActivity implements AnswersListAdapter.OnAnswerCardClickedListener {

    @BindView(R.id.question_title)
    TextView mQuestionTitle;

    @BindView(R.id.answer_count)
    TextView mAnswerCount;

    @BindView(R.id.question_list)
    RecyclerView mQuestionListView;

    @BindView(R.id.answers_list)
    RecyclerView mAnswersList;

    @BindView(R.id.fab)
    ExtendedFloatingActionButton mFab;

    AnswersListAdapter answersListAdapter;

    QuestionTitle questionTitle;

    ListenerRegistration listenerRegistration;

    List<Answer> answerList;

    AnswerEditorListAdapter questionListAdapter;

    HashMap<String,Boolean> reactionMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_problems);
        getWindow().setStatusBarColor(Color.BLACK);
        ButterKnife.bind(this);
        answerList = new ArrayList<>();
        if (questionTitle == null) {
            questionTitle = getIntent().getParcelableExtra("Q_TITLE");
        }
        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        reactionMap = new HashMap<>();

        mQuestionTitle.setText(questionTitle.getTitle());
        mQuestionListView.setLayoutManager(new LinearLayoutManager(this));
        questionListAdapter = new AnswerEditorListAdapter(questionTitle.getAnswerMap(), this, true);
        mQuestionListView.setAdapter(questionListAdapter);

        mAnswerCount.setText("Answers");

        answersListAdapter = new AnswersListAdapter(answerList, this);
        mAnswersList.setLayoutManager(new LinearLayoutManager(this));
        mAnswersList.setAdapter(answersListAdapter);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPublishAnswerActivity();
            }
        });
    }

    private void openPublishAnswerActivity() {
        Intent intent = new Intent(this, PublishQuestionActivity.class);
        intent.putExtra("Q_TITLE", questionTitle);
        startActivity(intent);
    }


    private void getAnswers() {
        listenerRegistration = FirebaseFirestore.getInstance()
                .collection("discussions")
                .document(questionTitle.getId())
                .collection("answers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(TopicProblemsActivity.this, "Some Error occured", Toast.LENGTH_SHORT).show();
                            Log.d("DATA_ANS_FETCH", "ERROR: " + e.getMessage());
                            return;
                        }
                        Log.d("DATA_UPDATED_OR_FETCHED", "FETCHED");
                        answerList = new ArrayList<>();
                        answersListAdapter.setAnswerList(answerList);
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Answer answer = doc.toObject(Answer.class);
                            answer.setId(doc.getId());
                            answerList.add(answer);
                        }
                        answersListAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (listenerRegistration != null)
            listenerRegistration.remove();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAnswers();
    }


    @Override
    public void onAnswerCardClicked(Answer answer, SwitchMaterial liked, SwitchMaterial disliked) {
        if(reactionMap.containsKey(answer.getId()))
            return;

        if (!Controller.isNetworkAvailable(TopicProblemsActivity.this)) {
            Toast.makeText(TopicProblemsActivity.this, "No Internet Connection. Reactions won't work.", Toast.LENGTH_SHORT).show();
        }

        reactionMap.put(answer.getId(),true);

        //increase the card size
        FirebaseFirestore.getInstance()
                .collection("reactions")
                .document("x5LXPkwEIIWtvA7fOgxF")//user id
                .collection("likes_dislikes")
                .document(answer.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(TopicProblemsActivity.this,"Some error occured fetching reactions",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DocumentSnapshot doc = task.getResult();
                        if(doc==null)
                            return;
                        Reaction reaction = doc.toObject(Reaction.class);
                        if(reaction==null)
                            return;
                        liked.setChecked(reaction.liked);
                        disliked.setChecked(reaction.disliked);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DATA_REACTION","ERROR: "+e.getMessage());
                Toast.makeText(TopicProblemsActivity.this,"Some error occured",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAnswerLiked(Answer answer, boolean isAnsDisliked, boolean isLiked) {
        if (!Controller.isNetworkAvailable(TopicProblemsActivity.this)) {
            Toast.makeText(TopicProblemsActivity.this, "No Internet Connection. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("discussions")
                .document(questionTitle.getId())
                .collection("answers").document(answer.getId());


        DocumentReference ref2 = FirebaseFirestore.getInstance()
                .collection("reactions").document("x5LXPkwEIIWtvA7fOgxF")//
                .collection("likes_dislikes").document(answer.getId());

        Reaction reaction = new Reaction();

        if (isLiked) {
            if (isAnsDisliked) {
//                writeBatch.update(ref2, "isDisliked", false);
                reaction.setDisliked(false);
                writeBatch.update(ref, "dislikes", FieldValue.increment(-1L));
            }
            reaction.setLiked(true);
            writeBatch.update(ref2, "isLiked", true);
            writeBatch.update(ref, "likes", FieldValue.increment(1L));
        } else {
//            answer.setLiked(false);
//            writeBatch.update(ref2,"isLiked",false);
            reaction.setLiked(false);
            writeBatch.update(ref, "likes", FieldValue.increment(-1L));
        }

        writeBatch.set(ref2,reaction,SetOptions.merge());
        writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DATA_ANS_LIKED", "DONE: " + answer.getId());
                    return;
                }
                Log.d("DATA_ANS_LIKED", "NOT DONE");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DATA_ANS_LIKED", "ERROR: " + e.getMessage());
            }
        });
//        writeBatch.update()
    }

    @Override
    public void onAnswerDisliked(Answer answer, boolean isAnsLiked, boolean isDisliked) {
        if (!Controller.isNetworkAvailable(TopicProblemsActivity.this)) {
            Toast.makeText(TopicProblemsActivity.this, "No Internet Connection. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }


        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("discussions")
                .document(questionTitle.getId())
                .collection("answers").document(answer.getId());

        DocumentReference ref2 = FirebaseFirestore.getInstance()
                .collection("reactions").document("x5LXPkwEIIWtvA7fOgxF")//user id
                .collection("likes_dislikes").document(answer.getId());

        Reaction reaction = new Reaction();

        if (isDisliked) {
            if (isAnsLiked) {
//                answer.setLiked(false);
                reaction.setLiked(false);
//                writeBatch.update(ref2, "isLiked", false);
                writeBatch.update(ref, "likes", FieldValue.increment(-1L));
            }
//            answer.setDisliked(true);
            reaction.setDisliked(true);
//            writeBatch.update(ref2, "isDisliked", true);
            writeBatch.update(ref, "dislikes", FieldValue.increment(1L));
        } else {
//            answer.setDisliked(false);
//            writeBatch.update(ref2,"isDisliked",false);
            reaction.setDisliked(false);
            writeBatch.update(ref, "dislikes", FieldValue.increment(-1L));
        }
        writeBatch.set(ref2,reaction);
        writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("DATA_ANS_DISLIKED", "DONE: " + answer.getId());
                    return;
                }
                Log.d("DATA_ANS_DISLIKED", "NOT DONE");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DATA_ANS_DISLIKED", "ERROR: " + e.getMessage());
            }
        });
    }


}
