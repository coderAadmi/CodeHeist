package com.prady.codeheist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prady.codeheist.adaptors.AnswerEditorListAdapter;
import com.prady.codeheist.adaptors.AnswersListAdapter;
import com.prady.codeheist.datamodels.Answer;
import com.prady.codeheist.datamodels.QuestionTitle;

import java.util.ArrayList;
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_problems);
        getWindow().setStatusBarColor(Color.BLACK);
        ButterKnife.bind(this);
        answerList = new ArrayList<>();
        if(questionTitle == null)
        {
            questionTitle = getIntent().getParcelableExtra("Q_TITLE");
        }
        init();
    }

    private void init()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mQuestionTitle.setText(questionTitle.getTitle());
        mQuestionListView.setLayoutManager(new LinearLayoutManager(this));
        questionListAdapter = new AnswerEditorListAdapter(questionTitle.getAnswerMap(),this,true);
        mQuestionListView.setAdapter(questionListAdapter);

        mAnswerCount.setText("Answers");

        answersListAdapter = new AnswersListAdapter(answerList,this);
        mAnswersList.setLayoutManager(new LinearLayoutManager(this));
        mAnswersList.setAdapter(answersListAdapter);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPublishAnswerActivity();
            }
        });
    }

    private void openPublishAnswerActivity()
    {
        Intent intent = new Intent(this, PublishQuestionActivity.class);
        intent.putExtra("Q_TITLE",questionTitle);
        startActivity(intent);
    }


    private void getAnswers()
    {
        listenerRegistration = FirebaseFirestore.getInstance()
                .collection("discussions")
                .document(questionTitle.getId())
                .collection("answers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        answerList = new ArrayList<>();
                        answersListAdapter.setAnswerList(answerList);
                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                        {
                            Answer answer = doc.toObject(Answer.class);
                            answerList.add(answer);
                        }
                        answersListAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(listenerRegistration!=null)
            listenerRegistration.remove();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAnswers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAnswerCardClicked(Answer answer) {
        //increase the card size
    }
}
