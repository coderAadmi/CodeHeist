package com.prady.codeheist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.prady.codeheist.adaptors.TopicListAdaptor;
import com.prady.codeheist.datamodels.QuestionTitle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyQuestionsActivity extends AppCompatActivity implements TopicListAdaptor.OnTopicClickedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.my_questions_list)
    RecyclerView mQuestionListView;

    @BindView(R.id.relative_layout)
    RelativeLayout mRelativeLayout;

    TopicListAdaptor adaptor;
    List<QuestionTitle> questions;

    FirebaseUser user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_questions);

        getWindow().setStatusBarColor(Color.BLACK);
        ButterKnife.bind(this);

        init();
    }

    private void init()
    {
        setSupportActionBar(mToolbar);

        questions = new ArrayList<>();
        adaptor = new TopicListAdaptor(questions,this);
        mQuestionListView.setAdapter(adaptor);
        mQuestionListView.setLayoutManager(new LinearLayoutManager(this));
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance()
                .collection("question_titles")
                .whereEqualTo("id",user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(MyQuestionsActivity.this,"There's some error in fetching data.Please try after some time.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        questions = new ArrayList<>();
                        adaptor.setTopics(questions);
                        if(task.getResult().size()==0)
                        {
                            mRelativeLayout.setVisibility(View.VISIBLE);
                            return;
                        }
                        mRelativeLayout.setVisibility(View.GONE);
                        for(QueryDocumentSnapshot doc : task.getResult())
                        {
                            QuestionTitle q = doc.toObject(QuestionTitle.class);
                            q.setId(doc.getId());
                            questions.add(q);
                        }
                        adaptor.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MY_Q_FETCH",e.getMessage());
                        Toast.makeText(MyQuestionsActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finishAfterTransition();
        return false;
    }

    @Override
    public void onTopicClicked(QuestionTitle questionTitle) {
        Intent intent = new Intent(this,TopicProblemsActivity.class);
        intent.putExtra("Q_TITLE",questionTitle);
        startActivity(intent);
    }
}
