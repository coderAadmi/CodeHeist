package com.prady.codeheist.adaptors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prady.codeheist.Controller;
import com.prady.codeheist.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnswerEditorListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnAnswerListUpdatedListener {
        public void onAnswerListUpdated(boolean isAnswerEmpty);
        public void onImageListUpdated();
    }

    List<Bitmap> images;
    OnAnswerListUpdatedListener mAnsListListener;
    TreeMap<String, String> textList;
    HashMap<Integer,Integer> ansImageMap;
    Context context;
    boolean isQuestionListView;

    private void saveImgToDB(Bitmap image,final int pos){
        Timestamp timestamp = new Timestamp(new Date());
        StorageReference storage = FirebaseStorage.getInstance().getReference("AnswerById/"+timestamp.toString()+"/");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();
        StorageReference ref = storage.child(pos+".jpg");
        UploadTask uploadTask = ref.putBytes(data);
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful())
                {
                    Log.d("DATA_IMG_FAIL",pos+" upload failed");
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();
                    Log.d("DATA_ADDED",downloadUri.toString());
                    textList.remove(pos+"");
                    textList.put(pos+"","img: "+downloadUri.toString());
                    mAnsListListener.onImageListUpdated();
                }
                else
                {
                    Log.d("DATA_IMG_EX",pos+" : "+task.getException().getMessage());
                }
            }
        });
    }


    public List<Bitmap> getImages() {
        return images;
    }

    public void addText(String s) {
        textList.put(textList.size() + "", s);
        mAnsListListener.onAnswerListUpdated(textList.size() == 0);
    }

    public void addImage(Bitmap bitmap) {
        saveImgToDB(bitmap,textList.size());
        ansImageMap.put(textList.size(),images.size());
        textList.put(textList.size() + "", "img: " + images.size());
        images.add(bitmap);
        mAnsListListener.onAnswerListUpdated(textList.size() == 0);
    }

    public TreeMap<String, String> getTextList() {
        return textList;
    }

    public AnswerEditorListAdapter(TreeMap<String, String> textList, Context context, boolean isQuestionListView) {
        this.textList = textList;
        this.context = context;
        this.isQuestionListView = isQuestionListView;
        ansImageMap = new HashMap<>();
//        mAnsListListener = (OnAnswerListUpdatedListener) context;

    }

    public AnswerEditorListAdapter(Context context) {
        this.textList = new TreeMap<>();
        this.images = new ArrayList<>();
        mAnsListListener = (OnAnswerListUpdatedListener) context;
        ansImageMap = new HashMap<>();
        if (mAnsListListener == null) {
            Log.d("ERROR_AnswerListAdapter", "AnswerList Listener null");
            return;
        }
        mAnsListListener.onAnswerListUpdated(textList.size() == 0);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0)
            return new AnswerTextViewHolder(inflater.inflate(R.layout.ans_text_view, parent, false));
        else
            return new AnswerImageViewHolder(inflater.inflate(R.layout.ans_img_view, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (textList.get(position + "").substring(0, 4).equals("tex:"))
            ((AnswerTextViewHolder) holder).mAnsTex.setText(textList.get(position + "").substring(5));
        else {
//            ((AnswerImageViewHolder)holder).mImgView.setImageBitmap(images.get(Integer.parseInt(textList.get(position+"").substring(5))));
            if (isQuestionListView) {
                Log.d("DATA_IMG_Q",textList.get(position+"").substring(5));
                Glide.with(holder.itemView.getContext())
                        .load(textList.get(position+"").substring(5))
                        .placeholder(R.drawable.app_logo)
                        .into(((AnswerImageViewHolder) holder).mImgView);
            } else {

                Glide.with(holder.itemView.getContext())
                        .load(images.get(ansImageMap.get(position)))
                        .placeholder(R.drawable.app_logo)
                        .into(((AnswerImageViewHolder) holder).mImgView);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (textList == null) {
//            Log.d("DATA_LIST","NULL");
            return 0;
        }
//        Log.d("DATA_LIST",textList.get("0"));
        return textList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (textList.get(position + "").substring(0, 4).equals("tex:"))
            return 0;
        else
            return 1;
    }

    class AnswerTextViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ans_text)
        TextView mAnsTex;

        public AnswerTextViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class AnswerImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_view)
        ImageView mImgView;

        public AnswerImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
