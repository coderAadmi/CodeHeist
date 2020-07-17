package com.prady.codeheist.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.prady.codeheist.R;
import com.prady.codeheist.datamodels.QuestionTitle;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicListAdaptor extends RecyclerView.Adapter<TopicListAdaptor.TopicViewHolder> {

    public interface OnTopicClickedListener{
        public void onTopicClicked(QuestionTitle questionTitle);
    }

    private List<QuestionTitle> topics;
    OnTopicClickedListener listener;

    private Context context;

    public TopicListAdaptor(List<QuestionTitle> topics, Context context) {
        this.topics = topics;
        this.context = context;
        this.listener = (OnTopicClickedListener) context;
    }

    public void setTopics(List<QuestionTitle> topics) {
        this.topics = topics;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TopicViewHolder(inflater.inflate(R.layout.topic_box_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        holder.mTitle.setText(topics.get(holder.getAdapterPosition()).getTitle());
        holder.mFromName.setText(topics.get(holder.getAdapterPosition()).getFromName());
        holder.mQTime.setText(topics.get(holder.getAdapterPosition()).getTimestamp());

        Glide.with(context)
                .load(topics.get(holder.getAdapterPosition()).getFromImg())
                .placeholder(R.drawable.ic_profile)
                .into(holder.mFromImg);

        holder.mTopicCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTopicClicked(topics.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    class TopicViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.title)
        TextView mTitle;

        @BindView(R.id.topic_card)
        MaterialCardView mTopicCard;

        @BindView(R.id.question_time)
        TextView mQTime;

        @BindView(R.id.author_name)
        TextView mFromName;

        @BindView(R.id.author_img)
        CircularImageView mFromImg;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
