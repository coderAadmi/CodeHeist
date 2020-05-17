package com.prady.codeheist.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.prady.codeheist.R;
import com.prady.codeheist.datamodels.Answer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnswersListAdapter extends RecyclerView.Adapter<AnswersListAdapter.AnswerCardViewHolder> {

    List<Answer> answerList;
    Context context;

    public interface OnAnswerCardClickedListener {
        public void onAnswerCardClicked(Answer answer);
    }

    private OnAnswerCardClickedListener listener;

    public AnswersListAdapter(List<Answer> answerList, Context context) {
        this.answerList = answerList;
        this.context = context;
        listener = (OnAnswerCardClickedListener) context;
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

        holder.mAnswerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAnswerCardClicked(answerList.get(holder.getAdapterPosition()));
                holder.mAnsRel.setVisibility(View.VISIBLE);
                holder.mAnswerLL.setVisibility(View.GONE);
                AnswerEditorListAdapter answerMapAdapter = new AnswerEditorListAdapter(
                        answerList.get(
                                holder.getAdapterPosition()).getMap(),
                        context,
                        true
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
    }

    @Override
    public int getItemCount() {
        if (answerList == null)
            return 0;
        return answerList.size();
    }

    class AnswerCardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.relative_layout)
        LinearLayout mRel;

        @BindView(R.id.img_view)
        ImageView mImg;

        @BindView(R.id.ans_text)
        TextView mText;

        @BindView(R.id.answer_card)
        MaterialCardView mAnswerCard;

        @BindView(R.id.answer_rel)
        RelativeLayout mAnsRel;

        @BindView(R.id.answer_linear)
        LinearLayout mAnswerLL;

        @BindView(R.id.answer_collapse_button)
        MaterialRadioButton mAnswerCollapseButton;

        @BindView(R.id.answer_map_list)
        RecyclerView mAnswerMapList;

        public AnswerCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
