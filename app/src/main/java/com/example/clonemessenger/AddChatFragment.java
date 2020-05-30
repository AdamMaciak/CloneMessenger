package com.example.clonemessenger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.clonemessenger.Adapters.AddContactToChatAdapter;
import com.example.clonemessenger.Models.ListChatModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddChatFragment extends Fragment {
    MaterialButton buttonAddChatFragment;
    FirebaseFirestore db;
    Context ctx;
    EditText chatNameEditText;
    EditText descriptionEditText;
    AddContactToChat addContactToChat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_chat, container, false);
        chatNameEditText = v.findViewById(R.id.chatName);
        descriptionEditText = v.findViewById(R.id.descriptionChat);
        buttonAddChatFragment = v.findViewById(R.id.addChat);

        buttonAddChatFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewChat(chatNameEditText.getText().toString(),
                        descriptionEditText.getText().toString());
                addChatToUser();
                addContactToChat = new AddContactToChat();
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        addContactToChat).commit();

            }
        });

        db = FirebaseFirestore.getInstance();
        ctx = v.getContext();
        return v;
    }

    public void addNewChat(String title, String description) {
        ListChatModel listChatModel = new ListChatModel(title, "", false
                , false, description);
        db.collection("listChat").add(listChatModel).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //TODO
                        final DocumentReference dr = documentReference;
                        Map<String, Object> refToChat = new HashMap<>();
                        refToChat.put("LastMessage", "");
                        refToChat.put("LastMessageDate", Calendar.getInstance().getTime());
                        refToChat.put("refToChat", documentReference);
                        db.collection("user")
                                .document(SharedPrefUser.getInstance(ctx).getUser().getId())
                                .collection(
                                        "refToChat")
                                .add(refToChat)
                                .addOnSuccessListener(
                                        new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(
                                                    DocumentReference documentReference) {
                                                setReferenceToChat(dr.getPath());
                                                makeToast("New Chat was created");
                                            }
                                        });
                        Map<String, Object> toAdd = new HashMap<>();
                        DocumentReference drUser =
                                db.collection("user")
                                        .document(
                                                SharedPrefUser.getInstance(ctx).getUser().getId());

                        toAdd.put("refToUser", drUser);
                        dr.collection("users").add(toAdd).addOnSuccessListener(
                                new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        makeToast("dodano usera do chatu");
                                    }
                                });

                    }
                });
    }

    private void addChatToUser() {

    }

    private void setReferenceToChat(String referenceToChat) {
        addContactToChat.setReference(referenceToChat);
    }

    public void makeToast(String word) {
        Toast.makeText(ctx, word, Toast.LENGTH_SHORT).show();
    }
}
