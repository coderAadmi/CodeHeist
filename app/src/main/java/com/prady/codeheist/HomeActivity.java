package com.prady.codeheist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;

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

    TextView mProfileName;
    TextView mUsername;

    String name, username, userImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setStatusBarColor(Color.BLACK);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mDrawer.isDrawerOpen(GravityCompat.START))
        {
            mDrawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu,menu);
        return true;
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
                Intent intent = null;
               switch (item.getItemId())
               {
                   case R.id.signout_menu:
                       signOut();
                       break;
                   case R.id.profile_menu:
                       intent = new Intent(HomeActivity.this,MyProfileActivity.class);
                       startActivity(intent);
                       break;
                   case R.id.about_menu:
                       break;
                   case R.id.my_ans_menu:
                       intent = new Intent(HomeActivity.this, MyAnswersActivity.class);
                       startActivity(intent);
                       break;
                   case R.id.my_questions_menu:
                       intent = new Intent(HomeActivity.this,MyQuestionsActivity.class);
                       startActivity(intent);
                       break;
                   case R.id.help:
                       break;

               }
                return false;
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        View headerLayout = mNavigationView.getHeaderView(0);
        mProfileImg = headerLayout.findViewById(R.id.profile_img);
        if(user.getPhotoUrl()!=null) {
            Log.d("USER_IN","IMG: "+user.getPhotoUrl().toString());
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.app_logo)
                    .into(mProfileImg);
        }

        mProfileName = headerLayout.findViewById(R.id.profile_name);
        if(user.getDisplayName()!=null)
        {
            mProfileName.setText(user.getDisplayName());
        }
    }

    private void signOut()
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this,AuthActivity.class);
        startActivity(intent);
        finishAfterTransition();
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
            if(mDrawer.isDrawerOpen(GravityCompat.START))
                mDrawer.closeDrawer(GravityCompat.START);
            else mDrawer.openDrawer(GravityCompat.START);
        }
        if(item.getItemId()==R.id.ask_question_menu)
        {
//            Toast.makeText(HomeActivity.this,"Adding feature",Toast.LENGTH_SHORT).show();
            askQuestion();
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
        Intent intent = new Intent(this, AnswerActivity.class);
        intent.putExtra("Q_TITLE",questionTitle);
        startActivity(intent);
    }



}
