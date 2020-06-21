package com.prady.codeheist.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.prady.codeheist.Controller;
import com.prady.codeheist.R;
import com.prady.codeheist.datamodels.Answer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnswersListAdapter extends RecyclerView.Adapter<AnswersListAdapter.AnswerCardViewHolder> {

    List<Answer> answerList;
    Context context;

    boolean wasOneChecked;

    public interface OnAnswerCardClickedListener {
        public void onAnswerCardClicked(Answer answer, SwitchMaterial liked, SwitchMaterial disliked);

        public void onAnswerLiked(Answer answer, boolean isAnsDisLiked, boolean isAnsLiked);

        public void onAnswerDisliked(Answer answer, boolean isAnsLiked, boolean isAnsDisliked);
    }

    private OnAnswerCardClickedListener listener;

    public AnswersListAdapter(List<Answer> answerList, Context context) {
        this.answerList = answerList;
        this.context = context;
        listener = (OnAnswerCardClickedListener) context;
        wasOneChecked = false;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }


    @NonNull
    @Override
    public AnswerCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ans_layout_view, parent, false);
        return new AnswerCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerCardViewHolder holder, int position) {
        String type = answerList.get(position).getMap().get("0");
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
        Answer answer = answerList.get(holder.getAdapterPosition());

        holder.mAuthorName.setText(answer.getFromName());
        holder.mAnswerBy.setText(answer.getFromName());

        SimpleDateFormat sfd = new SimpleDateFormat("dd / MM / yyyy    HH : mm : ss");
        String date = sfd.format(answer.getTimestamp().toDate());
        holder.mPostedAtDate.setText(date);//see this if weird date comes
        holder.mAnswerDate.setText(date);

        holder.mLikes.setText(answer.getLikes() + "");
        holder.mAnsLikesCount.setText(answer.getLikes() + "");

//        holder.mAnsLikeButton.setChecked(answer.isLiked());
//        holder.mAnsDislikeButton.setChecked(answer.isDisliked());

        holder.mDislikes.setText(answer.getDislikes() + "");
        holder.mAnsDislikesCount.setText(answer.getDislikes() + "");

        holder.mAnswerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAnswerCardClicked(answerList.get(holder.getAdapterPosition()),holder.mAnsLikeButton,holder.mAnsDislikeButton);
                holder.mAnsRel.setVisibility(View.VISIBLE);
                holder.mAnswerLL.setVisibility(View.GONE);
                AnswerEditorListAdapter answerMapAdapter = new AnswerEditorListAdapter(
                        answerList.get(
                                holder.getAdapterPosition()).getMap(),
                        context,
                        false,true
                );

                holder.mAnswerMapList.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                holder.mAnswerMapList.setAdapter(answerMapAdapter);

            }
        });

        holder.mAnswerCollapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mAnswerLL.setVisibility(View.VISIBLE);
                holder.mAnsRel.setVisibility(View.GONE);
            }
        });

        holder.mAnsLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context != null && !Controller.isNetworkAvailable(context)) {
                    Toast.makeText(context, "No Internet connection available.", Toast.LENGTH_SHORT).show();
                    holder.mAnsLikeButton.setChecked(!holder.mAnsLikeButton.isChecked());
                    return;
                }
                if (holder.mAnsLikeButton.isChecked()) {
                    wasOneChecked = true;
                    boolean isDisLiked = false;
                    if (holder.mAnsDislikeButton.isChecked()) {
                        holder.mAnsDislikeButton.setChecked(false);
                        isDisLiked = true;
                    }
                    listener.onAnswerLiked(answerList.get(holder.getAdapterPosition()), isDisLiked, true);
                    wasOneChecked = false;
                } else if (!wasOneChecked)
                    listener.onAnswerLiked(answerList.get(holder.getAdapterPosition()), false, false);
            }
        });


        holder.mAnsDislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context != null && !Controller.isNetworkAvailable(context)) {
                    Toast.makeText(context, "No Internet connection available.", Toast.LENGTH_SHORT).show();
                    holder.mAnsDislikeButton.setChecked(!holder.mAnsDislikeButton.isChecked());
                    return;
                }
                if (holder.mAnsDislikeButton.isChecked()) {
                    wasOneChecked = true;
                    boolean isAnsLiked = false;
                    if (holder.mAnsLikeButton.isChecked()) {
                        holder.mAnsLikeButton.setChecked(false);
                        isAnsLiked = true;
                    }
                    listener.onAnswerDisliked(answerList.get(holder.getAdapterPosition()), isAnsLiked, true);
                    wasOneChecked = false;
                } else if (!wasOneChecked)
                    listener.onAnswerDisliked(answerList.get(holder.getAdapterPosition()), false, false);
//                listener.onAnswerDisliked(answerList.get(holder.getAdapterPosition()),holder.mAnsLikeButton,holder.mAnsDislikeButton);
            }
        });

        holder.mAnsCommentButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (context != null)
                        Toast.makeText(context, "Feature under development", Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (answerList == null)
            return 0;
        return answerList.size();
    }

    public class AnswerCardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.relative_layout)
        LinearLayout mRel;

        @BindView(R.id.img_view)
        ImageView mImg;

        @BindView(R.id.ans_text)
        TextView mText;

        @BindView(R.id.answer_card)
        MaterialCardView mAnswerCard;

        @BindView(R.id.answer_linear)
        LinearLayout mAnswerLL;

        //following fields are of collapsed view;
        @BindView(R.id.author_name)
        TextView mAuthorName;

        @BindView(R.id.posted_at_date)
        TextView mPostedAtDate;

        @BindView(R.id.likes)
        TextView mLikes;

        @BindView(R.id.dislikes)
        TextView mDislikes;

        //following fields are of expanded view;
        @BindView(R.id.answer_rel)
        RelativeLayout mAnsRel;

        @BindView(R.id.answer_collapse_button)
        MaterialRadioButton mAnswerCollapseButton;

        @BindView(R.id.answer_map_list)
        RecyclerView mAnswerMapList;

        @BindView(R.id.answer_like_button)
        SwitchMaterial mAnsLikeButton;

        @BindView(R.id.answer_dislike_buttton)
        SwitchMaterial mAnsDislikeButton;

        @BindView(R.id.answer_comment_button)
        MaterialRadioButton mAnsCommentButton;

        @BindView(R.id.answer_by)
        TextView mAnswerBy;

        @BindView(R.id.answer_date)
        TextView mAnswerDate;

        @BindView(R.id.likes_count)
        TextView mAnsLikesCount;

        @BindView(R.id.dislikes_count)
        TextView mAnsDislikesCount;


        public AnswerCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
