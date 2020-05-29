package com.example.clonemessenger.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clonemessenger.Models.UserModel;
import com.example.clonemessenger.Models.UserModelWithRef;
import com.example.clonemessenger.Models.UserSharedPref;
import com.example.clonemessenger.R;
import com.example.clonemessenger.SharedPrefUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddContactAdapter extends RecyclerView.Adapter<AddContactAdapter.ViewHolder> {

    List<UserModelWithRef> userModelList;
    FirebaseFirestore db;
    UserSharedPref userSharedPref;
    Context ctx;

    public AddContactAdapter() {
        userModelList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        userSharedPref = SharedPrefUser.getInstance(ctx).getUser();
    }

    @NonNull
    @Override
    public AddContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
        ctx = parent.getContext();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.label_for_contact, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userName.setText(userModelList.get(position).getUserModel().getName());
        holder.userModelWithRef.setPathToDocument(userModelList.get(position).getPathToDocument());
        holder.userModelWithRef.setUserModel(userModelList.get(position).getUserModel());
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
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
                    requestPermission(userModelWithRef.getPathToDocument());
                }
            });
        }
    }

    public void updateRecycleView(List<UserModelWithRef> listUserModels) {
        userModelList.clear();
        for (UserModelWithRef um :
                listUserModels) {
            userModelList.add(new UserModelWithRef(um.getPathToDocument(),
                    new UserModel(um.getUserModel().getName(),
                            um.getUserModel().getImagePath(),
                            um.getUserModel().getImageCompressPath(),
                            um.getUserModel().isFullVersion(), um.getUserModel().isOnline(),
                            um.getUserModel().getLastOnline())));
        }

        notifyDataSetChanged();
    }

    private void addContact(DocumentReference referenceToContact) {
        Map<String, Object> contacts = new HashMap<>();
        contacts.put("noteAboutThisPerson", "");
        contacts.put("refToUser", referenceToContact);

        db.collection("user")
                .document(userSharedPref.getId())
                .collection("contacts")
                .add(contacts)
                .addOnSuccessListener(
                        new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                makeToast("User added");
                            }
                        });
    }

    private void requestPermission(String reference) {
        new AlertDialog.Builder(ctx)
                .setTitle("Chcesz dodać tego użytkownika?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DocumentReference ds = db.document(reference);
                        addContact(ds);
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((AppCompatActivity) ctx).finish();
                    }
                })
                .create().show();
    }

    public void makeToast(String word) {
        Toast.makeText(ctx, word, Toast.LENGTH_LONG).show();
    }
}
