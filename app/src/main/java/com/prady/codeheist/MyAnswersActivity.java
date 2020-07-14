package com.prady.codeheist;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.prady.codeheist.adaptors.MyAnswerListAdapter;
import com.prady.codeheist.datamodels.Answer;


import java.util.ArrayList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAnswersActivity extends AppCompatActivity implements MyAnswerListAdapter.OnMyAnswerCardClickedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.my_questions_list)
    RecyclerView mAnswersListView;

    @BindView(R.id.relative_layout)
    RelativeLayout mRelativeLayout;

    @BindView(R.id.tv1)
    TextView mTv;

    MyAnswerListAdapter myAnswersListAdapter;

    List<Answer> myAnswersList;

    FirebaseUser user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_questions);

        getWindow().setStatusBarColor(Color.BLACK);

        ButterKnife.bind(this);
        init();
    }

    private void init() {

        setSupportActionBar(mToolbar);
        mToolbar.setTitle("My Answers");

        mTv.setText("You haven't answered any question.");
        user = FirebaseAuth.getInstance().getCurrentUser();

        mAnswersListView.setLayoutManager(new LinearLayoutManager(this));

        myAnswersList = new ArrayList<>();
        myAnswersListAdapter = new MyAnswerListAdapter(myAnswersList, this);
        mAnswersListView.setAdapter(myAnswersListAdapter);
        FirebaseFirestore.getInstance()
                .collectionGroup("answers")
                .whereEqualTo("fromId", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MyAnswersActivity.this, "Some error occured.Please try after some time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (task.getResult().size() == 0) {
                            mRelativeLayout.setVisibility(View.VISIBLE);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Answer answer = doc.toObject(Answer.class);
                            myAnswersList.add(answer);
                        }
                        myAnswersListAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ANSWER_FETCH_ERROR", e.getMessage());
                        Toast.makeText(MyAnswersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finishAfterTransition();
        return false;
    }


    @Override
    public void onMyAnswerCardClicked(Answer answer) {
        
    }
}
