package com.prady.codeheist.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.prady.codeheist.R;
import com.prady.codeheist.datamodels.Answer;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAnswerListAdapter extends RecyclerView.Adapter<MyAnswerListAdapter.MyAnswerCardViewHolder> {


    List<Answer> myAnswersList;
    Context context;

    public interface OnMyAnswerCardClickedListener{
        public void onMyAnswerCardClicked(Answer answer);
    }

    OnMyAnswerCardClickedListener listener;

    public MyAnswerListAdapter(List<Answer> myAnswersList, Context context) {
        this.myAnswersList = myAnswersList;
        this.context = context;
        listener = (OnMyAnswerCardClickedListener) context;
    }



    @NonNull
    @Override
    public MyAnswerCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_answer_card_view,parent,false);
        return new MyAnswerCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAnswerCardViewHolder holder, int position) {

        holder.mQuestiontitle.setText(myAnswersList.get(holder.getAdapterPosition()).getqTitle());
        String type = myAnswersList.get(position).getMap().get("0");
        if (type.startsWith("tex:")) {
            holder.mImg.setVisibility(View.GONE);
            holder.mText.setVisibility(View.VISIBLE);
            holder.mText.setText(type.substring(5));
        } else {
            holder.mText.setVisibility(View.GONE);
            holder.mImg.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(type.substring(5))
                    .placeholder(R.drawable.app_logo)
                    .into(holder.mImg);
        }
        Answer answer = myAnswersList.get(holder.getAdapterPosition());

        SimpleDateFormat sfd = new SimpleDateFormat("dd / MM / yyyy    HH : mm : ss");
        String date = sfd.format(answer.getTimestamp().toDate());
        holder.mPostedAtDate.setText(date);//see this if weird date comes

        holder.mLikes.setText(answer.getLikes() + "");


        holder.mDislikes.setText(answer.getDislikes() + "");

        holder.mAnswerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Clicked answer",Toast.LENGTH_SHORT).show();
                listener.onMyAnswerCardClicked(myAnswersList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(myAnswersList==null)
            return 0;
        return myAnswersList.size();
    }

    class MyAnswerCardViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.img_view)
        ImageView mImg;

        @BindView(R.id.ans_text)
        TextView mText;

        @BindView(R.id.posted_at_date)
        TextView mPostedAtDate;

        @BindView(R.id.likes)
        TextView mLikes;

        @BindView(R.id.dislikes)
        TextView mDislikes;

        @BindView(R.id.answer_card)
        MaterialCardView mAnswerCard;

        @BindView(R.id.question_title)
        TextView mQuestiontitle;

        public MyAnswerCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
