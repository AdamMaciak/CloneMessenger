package com.example.clonemessenger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
    List<Boolean> wybrany=new ArrayList<>();

    public AddContactToChatAdapter(
            OnContactListener onContactListener) {
        this.onContactListener = onContactListener;
        userModelWithRefList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        wybrany.add(false);
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_layout, parent, false);
        return new ViewHolder(v, onContactListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userName.setText(userModelWithRefList.get(position).getUserModel().getName());
        Glide.with(ctx).load(userModelWithRefList.get(position).getUserModel().getImageCompressPath()).into(holder.imageUser);
    }


    @Override
    public int getItemCount() {
        return userModelWithRefList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userName;
        CircleImageView imageUser;
        OnContactListener onContactListener;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView, OnContactListener onContactListener) {
            super(itemView);
            userName = itemView.findViewById(R.id.txname);
            imageUser = itemView.findViewById(R.id.imimage);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            this.onContactListener = onContactListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(wybrany.get(getAdapterPosition())==false){
                wybrany.set(getAdapterPosition(),true);
                relativeLayout.setBackground(ctx.getResources().getDrawable(R.drawable.rounded_edge_selected));
            } else {
                wybrany.set(getAdapterPosition(),false);
                relativeLayout.setBackground(ctx.getResources().getDrawable(R.drawable.rounded_edge));
            }
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
