package com.prady.codeheist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.prady.codeheist.adaptors.TopicListAdaptor;
import com.prady.codeheist.datamodels.QuestionTitle;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements TopicListAdaptor.OnTopicClickedListener {

    @BindView(R.id.topic_list_view)
    RecyclerView mTopicListView;

    @BindView(R.id.fab)
    ExtendedFloatingActionButton mFab;

    TopicListAdaptor adaptor;

    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;

    List<QuestionTitle> questions;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.navigation)
    NavigationView mNavigationView;

    CircularImageView mProfileImg;

    String name, username, userImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setStatusBarColor(Color.BLACK);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        name = sharedPreferences.getString("NAME","");
        username = sharedPreferences.getString("U_NAME","");
        userImg = sharedPreferences.getString("U_IMG","https://images.pexels.com/photos/333850/pexels-photo-333850.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTopicListView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        questions = new ArrayList<>();
        adaptor = new TopicListAdaptor(questions,this);
        mTopicListView.setAdapter(adaptor);
        listenerRegistration = getListenerRegistration();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this,"Adding feature",Toast.LENGTH_SHORT).show();
                askQuestion();
            }
        });
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast.makeText(HomeActivity.this,"Feature under development",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                return false;
            }
        });
        View headerLayout = mNavigationView.getHeaderView(0);
        mProfileImg = headerLayout.findViewById(R.id.profile_img);
        Glide.with(this)
                .load(userImg)
                .placeholder(R.drawable.app_logo)
                .into(mProfileImg);
    }

    private void askQuestion()
    {
        Intent intent = new Intent(HomeActivity.this,PublishQuestionActivity.class);
        intent.putExtra("IS_ASKING",true);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            if(mDrawer.isDrawerOpen(Gravity.LEFT))
                mDrawer.closeDrawer(Gravity.LEFT);
            else mDrawer.openDrawer(Gravity.LEFT);
        }
        return false;
    }

    private ListenerRegistration getListenerRegistration()
    {
        return db.collection("question_titles")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        questions = new ArrayList<>();
                        adaptor.setTopics(questions);
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots)
                        {
                            QuestionTitle q = doc.toObject(QuestionTitle.class);
                            q.setId(doc.getId());
                            questions.add(q);
                        }
                        adaptor.notifyDataSetChanged();
                    }
                });

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
        getListenerRegistration();
    }

    @Override
    public void onTopicClicked(QuestionTitle questionTitle) {
        Intent intent = new Intent(this,TopicProblemsActivity.class);
        intent.putExtra("Q_TITLE",questionTitle);
        startActivity(intent);
    }
}
