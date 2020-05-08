package com.example.clonemessenger;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clonemessenger.Models.ChatModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static final int msg_left = 0;
    public static final int msg_right = 1;
    private Context mContext;
    private List<ChatModel> mChat;
    private Uri profilePhotoUrl;
    FirebaseUser fUser;
    FirebaseStorage storage= FirebaseStorage.getInstance();

    public ChatAdapter(Context mContext, List<ChatModel> mChat, Uri profilePhotoUrl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.profilePhotoUrl = profilePhotoUrl;
        fUser= FirebaseAuth.getInstance().getCurrentUser();
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
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, int position) {
        final ChatModel chat=mChat.get(position);
        //System.out.println(fUser);
        if(!fUser.getUid().equals(chat.getSender())) {
            Glide.with(mContext).load(profilePhotoUrl).into(holder.profile_image);
        }
        if(!chat.getImage().equals("")){
            holder.show_image.setVisibility(View.VISIBLE);
            storage.getReference().child("images/"+chat.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(mContext).asBitmap()
                            .fitCenter().load(uri).into(holder.show_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        if(!chat.getMessage().equals("")) {
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
        System.out.println(fUser.getUid());
        System.out.println("POSITION "+position);
        System.out.println(mChat.get(position).getSender());
        if(mChat.get(position).getSender().equals(fUser.getUid())){
            System.out.println("-----------RIGHT");
            return msg_right;
        } else {
            System.out.println("-------------LEFT");
            return msg_left;
        }
    }
}
