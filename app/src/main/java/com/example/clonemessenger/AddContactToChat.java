package com.example.clonemessenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clonemessenger.Adapters.AddContactToChatAdapter;
import com.example.clonemessenger.Models.UserModel;
import com.example.clonemessenger.Models.UserModelWithRef;
import com.example.clonemessenger.Models.UserSharedPref;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class AddContactToChat extends Fragment implements AddContactToChatAdapter.OnContactListener {

    RecyclerView recyclerView;
    AddContactToChatAdapter addContactToChatAdapter;
    Context ctx;
    FirebaseFirestore db;
    List<Task<DocumentSnapshot>> tasks;
    UserSharedPref userSharedPref;
    List<UserModelWithRef> userModelWithRefList;
    Set<UserModelWithRef> userToAddToChat;
    FloatingActionButton confirmButton;
    String idChatCreated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_contact_to_chat, container, false);
        SharedPreferences preferences=getActivity().getSharedPreferences("Settings",MODE_PRIVATE);
        String language= preferences.getString("Lang","");
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        getActivity().setTitle(getResources().getString(R.string.addUserToChat));
        confirmButton = v.findViewById(R.id.confirmButton);
        db = FirebaseFirestore.getInstance();
        userSharedPref = SharedPrefUser.getInstance(v.getContext()).getUser();
        addContactToChatAdapter = new AddContactToChatAdapter(this);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext());
        recyclerView = v.findViewById(R.id.addContactToChatRecycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(addContactToChatAdapter);
        userModelWithRefList = new ArrayList<>();
        userToAddToChat = new HashSet<>();
        tasks = new ArrayList<>();
        db.collection("user")
                .document(userSharedPref.getId())
                .collection("contacts")
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentSnapshots =
                                        queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot ds :
                                        documentSnapshots) {
                                    Optional<DocumentReference> optionalDocumentReference =
                                            Optional.ofNullable(ds.getDocumentReference(
                                                    "refToUser"));
                                    DocumentReference dr;
                                    if (optionalDocumentReference.isPresent()) {
                                        dr = optionalDocumentReference.get();

                                        tasks.add(dr.get().addOnSuccessListener(
                                                new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(
                                                            DocumentSnapshot documentSnapshot) {
                                                        userModelWithRefList.add(
                                                                new UserModelWithRef(
                                                                        documentSnapshot.getReference()
                                                                                .getPath(),
                                                                        documentSnapshot.toObject(
                                                                                UserModel.class)));
                                                        makeToast((String) ds.get("name"));
                                                    }
                                                }));
                                    }
                                }
                                Tasks.whenAll(tasks).addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                addContactToChatAdapter.updateRecycleView(
                                                        userModelWithRefList);
                                                tasks.clear();
                                            }
                                        });
                            }
                        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogConfirm();
            }
        });
        return v;
    }

    @Override
    public void onContactClick(int position) {
        //tutaj wykonac metody po kliknieciu itemu w recyclerze
        if (userToAddToChat.contains(userModelWithRefList.get(position))) {
            if (userToAddToChat.remove(userModelWithRefList.get(position)))
                recyclerView.setBackgroundColor(321312);
            makeToast("juz jest w zbiorze do dodania wiec usune");
        } else {
            userToAddToChat.add(userModelWithRefList.get(position));
        }
        makeToast("dodano to zbioru, position:" + position);
    }

    private void showDialogConfirm() {
        new AlertDialog.Builder(ctx)
                .setTitle("Chcesz dodać tych użytkowników do czatu?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addContactToChat();
                        addChatToContact();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, new ListChatFragment()).commit();
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

    private void addContactToChat() {

        for (UserModelWithRef user :
                userToAddToChat) {
            Map<String, Object> toAdd = new HashMap<>();
            DocumentReference dr = db.document(user.getPathToDocument());
            toAdd.put("refToUser", dr);
            db.collection("listChat")
                    .document(idChatCreated)
                    .collection("users")
                    .add(toAdd)
                    .addOnSuccessListener(
                            new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    makeToast("successAddedContactToChat");
                                }
                            });
        }
        // db.document(refToChatCreated)
    }

    private void addChatToContact() {
        for (UserModelWithRef user :
                userToAddToChat) {
            Map<String, Object> toAdd = new HashMap<>();
            DocumentReference dr = db.document(user.getPathToDocument());
            toAdd.put("LastMessage", "");
            toAdd.put("LastMessageDate", Calendar.getInstance().getTime());
            toAdd.put("refToChat", db.document("listChat/" + idChatCreated));
            dr.collection("refToChat")
                    .document(idChatCreated.trim())
                    .set(toAdd)
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    makeToast("success added contact to Chat");
                                }
                            });
        }
    }

    public void setReference(String refToChat) {
        idChatCreated = refToChat;
    }

    private void makeToast(String word) {
        Toast.makeText(ctx, word, Toast.LENGTH_LONG).show();
    }
}
