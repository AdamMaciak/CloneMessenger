package com.example.clonemessenger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clonemessenger.Models.UserModel;
import com.example.clonemessenger.Models.UserModelWithRef;
import com.example.clonemessenger.Models.UserSharedPref;
import com.example.clonemessenger.R;
import com.example.clonemessenger.SharedPrefUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    List<UserModelWithRef> userModelWithRefList;
    FirebaseFirestore db;
    UserSharedPref userSharedPref;
    Context ctx;

    public ContactsAdapter() {
        userModelWithRefList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        userSharedPref = SharedPrefUser.getInstance(ctx).getUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.label_for_contact, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //TODO
        holder.userName.setText(userModelWithRefList.get(position).getUserModel().getName());
        holder.userModelWithRef.setPathToDocument(
                userModelWithRefList.get(position).getPathToDocument());
        holder.userModelWithRef.setUserModel(userModelWithRefList.get(position).getUserModel());
    }

    @Override
    public int getItemCount() {
        return userModelWithRefList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        CircleImageView imageUser;
        UserModelWithRef userModelWithRef;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.placeForUserName);
            imageUser = itemView.findViewById(R.id.imageUser);
            userModelWithRef = new UserModelWithRef();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public void updateRecycleView(List<UserModelWithRef> listUserModels) {
        userModelWithRefList.clear();
        for (UserModelWithRef um :
                listUserModels) {
            userModelWithRefList.add(new UserModelWithRef(um.getPathToDocument(),
                    new UserModel(um.getUserModel().getName(),
                            um.getUserModel().getImagePath(),
                            um.getUserModel().getImageCompressPath(),
                            um.getUserModel().isFullVersion(), um.getUserModel().isOnline(),
                            um.getUserModel().getLastOnline())));
        }

        notifyDataSetChanged();
    }
}
