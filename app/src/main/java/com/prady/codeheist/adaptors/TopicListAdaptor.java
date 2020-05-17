package com.prady.codeheist.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prady.codeheist.R;
import com.prady.codeheist.datamodels.QuestionTitle;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopicListAdaptor extends RecyclerView.Adapter<TopicListAdaptor.TopicViewHolder> {

    public interface OnTopicClickedListener{
        public void onTopicClicked(QuestionTitle questionTitle);
    }

    private List<QuestionTitle> topics;
    OnTopicClickedListener listener;

    public TopicListAdaptor(List<QuestionTitle> topics, OnTopicClickedListener listener) {
        this.topics = topics;
        this.listener = listener;
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
        holder.mTitle.setOnClickListener(new View.OnClickListener() {
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

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
