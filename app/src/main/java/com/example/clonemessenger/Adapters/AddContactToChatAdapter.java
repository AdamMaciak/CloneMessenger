package com.example.clonemessenger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clonemessenger.Models.UserModel;
import com.example.clonemessenger.Models.UserModelWithRef;
import com.example.clonemessenger.Models.UserSharedPref;
import com.example.clonemessenger.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddContactToChatAdapter extends RecyclerView.Adapter<AddContactToChatAdapter.ViewHolder> {

    List<UserModelWithRef> userModelWithRefList;
    private OnContactListener onContactListener;
    Context ctx;

    public AddContactToChatAdapter(
            OnContactListener onContactListener) {
        this.onContactListener = onContactListener;
        userModelWithRefList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.label_for_contact, parent, false);
        return new ViewHolder(v, onContactListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userName.setText(userModelWithRefList.get(position).getUserModel().getName());
    }


    @Override
    public int getItemCount() {
        return userModelWithRefList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userName;
        CircleImageView imageUser;
        OnContactListener onContactListener;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView, OnContactListener onContactListener) {
            super(itemView);
            userName = itemView.findViewById(R.id.placeForUserName);
            imageUser = itemView.findViewById(R.id.imageUser);
            constraintLayout = itemView.findViewById(R.id.contentContactLabel);
            this.onContactListener = onContactListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onContactListener.onContactClick(getAdapterPosition());
        }
    }

    public void updateRecycleView(List<UserModelWithRef> listUserModels) {
        userModelWithRefList = listUserModels;
        notifyDataSetChanged();
    }

    public interface OnContactListener {
        void onContactClick(int position);
    }
}
