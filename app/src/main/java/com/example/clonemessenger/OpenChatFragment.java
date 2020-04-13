package com.example.clonemessenger;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OpenChatFragment extends Fragment {

    ChatAdapter chatAdapter;
    List<ChatModel> chat=new ArrayList<>();
    RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ImageView btn_sendMessage;
    private EditText et_message;
    private FirebaseUser fUser;
    private String userId;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_open_chat, container, false);
        recyclerView=(RecyclerView) root.findViewById(R.id.rvChat);
        recyclerView.setHasFixedSize(true);
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        System.out.println(account.getPhotoUrl());
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        btn_sendMessage= (ImageView) root.findViewById(R.id.btn_sendMessage);
        et_message=(EditText) root.findViewById(R.id.et_message);
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        userId=fUser.getUid();

        db = FirebaseFirestore.getInstance();
        db.collection("messages")
                .document("Bqe17YUMVOc87njQktphxar85R63-cjvJnAr9dubyVrGZ7avyfAyaGFy1").collection("ChatModel")
                .orderBy("timeSend")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            ChatModel city = doc.toObject(ChatModel.class);
                            chat.add(city);
                        }


                        System.out.println("----------------------UWAGA");
                        for(ChatModel ch: chat){
                            System.out.println(ch.getMessage()+ "  "+ch.getSender());
                        }
                        //System.out.println(account.getPhotoUrl());
                        mAdapter = new ChatAdapter(getContext(), chat,account.getPhotoUrl());
                        recyclerView.setAdapter(mAdapter);
                    }
                });
        btn_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String message=et_message.getText().toString();
               chat.clear();
               Date currentTime = Calendar.getInstance().getTime();
               ChatModel chatModel=new ChatModel(userId,"cjvJnAr9dubyVrGZ7avyfAyaGFy1",message,"",currentTime);
               db.collection("messages").document("Bqe17YUMVOc87njQktphxar85R63-cjvJnAr9dubyVrGZ7avyfAyaGFy1").collection("ChatModel").add(chatModel);
               et_message.setText("");

            }
        });

        return root;
    }

}
