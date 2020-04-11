package com.example.clonemessenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static final int msg_left = 0;
    public static final int msg_right = 1;
    private Context mContext;
    private List<ChatModel> mChat;
    private String profilePhotoUrl;
    FirebaseUser fUser;

    public ChatAdapter(Context mContext, List<ChatModel> mChat, String profilePhotoUrl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == msg_right) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ChatAdapter.ViewHolder(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ChatAdapter.ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ChatModel chat=mChat.get(position);
        Glide.with(mContext).load(profilePhotoUrl).into(holder.profile_image);
        if(chat.getImage()!=null){
            holder.show_image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(chat.getImage()).into(holder.show_image);
        }
        if(chat.getMessage()!=null) {
            holder.show_message.setVisibility(View.VISIBLE);
            holder.show_message.setText(chat.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;
        public ImageView show_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_image = itemView.findViewById(R.id.show_image);
            profile_image = itemView.findViewById(R.id.profile_image);
            show_message = itemView.findViewById(R.id.show_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fUser.getUid())){
            return msg_right;
        } else {
            return msg_left;
        }
    }
}
