package com.example.clonemessenger.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clonemessenger.Models.ChatModel;
import com.example.clonemessenger.R;
import com.example.clonemessenger.SharedPrefUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.opencensus.internal.Utils;

import static android.widget.Toast.LENGTH_SHORT;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static final int msg_left = 0;
    public static final int msg_right = 1;
    private Context mContext;
    private List<ChatModel> mChat;
    FirebaseUser fUser;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private ConcurrentHashMap<String, Bitmap> userImages;

    public ChatAdapter(Context mContext, List<ChatModel> mChat, ConcurrentHashMap userImages) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.userImages = userImages;
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mChat.get(viewType).getSender().equals(fUser.getUid())) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ChatAdapter.ViewHolder(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ChatAdapter.ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ChatModel chat = mChat.get(position);
        //System.out.println(fUser);
        /*if(!fUser.getUid().equals(chat.getSender())) {
            Glide.with(mContext).load(profilePhotoUrl).into(holder.profile_image);
        }*/
        if (!chat.getImage().equals("")) {
            holder.show_image.setVisibility(View.VISIBLE);
            storage.getReference()
                    .child("images/" + chat.getImage())
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(mContext).asBitmap()
                                    .fitCenter().load(uri).into(holder.show_image);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
        }
        if (!chat.getMessage().equals("")) {
            holder.show_message.setVisibility(View.VISIBLE);
            holder.show_message.setText(chat.getMessage());
        }if(!mChat.get(position).getSender().equals(SharedPrefUser.getInstance(mContext).getUser().getId())){
            holder.profile_image.setImageBitmap(userImages.get(mChat.get(position).getSender()));
        }
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
        holder.time.setText(localDateFormat.format(chat.getTimeSend()));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;
        public ImageView show_image;
        public TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_image = itemView.findViewById(R.id.show_image);
            time = itemView.findViewById(R.id.time);
            profile_image = itemView.findViewById(R.id.profile_image);
            show_message = itemView.findViewById(R.id.show_message);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "AAAAAAAAAAAAAAAAAAAAAAAAAAAA", LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
