package com.example.clonemessenger;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clonemessenger.Adapters.ListChatAdapter;
import com.example.clonemessenger.Models.ListChatModel;
import com.example.clonemessenger.Models.UserSharedPref;
import com.example.clonemessenger.ViewModels.ListChatViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.common.base.Optional;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.DocumentKey;
import com.google.firebase.firestore.model.ResourcePath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ListChatFragment extends Fragment {

    private Button toChat;
    private Button getRef;
    private OpenChatFragment openChatFragment;
    private int PERMISSION = 1000;
    private Context ctx;

    FirebaseFirestore db;
    ListChatAdapter listChatAdapter;
    RecyclerView recyclerView;

    List<ListChatModel> listChatModels;
    List<ListChatViewModel> listChatViewModels;
    List<Task<DocumentSnapshot>> tasks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        db = FirebaseFirestore.getInstance();
        getRef = view.findViewById(R.id.button3);
        listChatModels = new ArrayList<>();
        //RECYCLER VIEW INIT
        recyclerView = view.findViewById(R.id.recViewForListChat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        listChatAdapter = new ListChatAdapter();
        recyclerView.setAdapter(listChatAdapter);
        ctx = getContext();
        tasks = new ArrayList<>();
        final UserSharedPref userSharedPref = SharedPrefUser.getInstance(getContext()).getUser();

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }

        getRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> dane = new HashMap<>();
                dane.put("LastMessage", "wiadomosc do odczytania");
                dane.put("LastMessageDate", Calendar.getInstance().getTime());
                dane.put("ref", "");
                db.collection("user")
                        .document(userSharedPref.getId())
                        .collection("refToChat")
                        .add(dane)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                makeToast("success");
                            }
                        });
            }
        });

        listChatViewModels = new ArrayList<>();


        db.collection("user")
                .document(userSharedPref.getId())
                .collection("refToChat")
                .orderBy("LastMessageDate").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> documentSnapshots =
                        queryDocumentSnapshots.getDocuments();
                listChatModels.clear();
                for (DocumentSnapshot ds :
                        documentSnapshots) {
                    Optional<DocumentReference> opDocRef = Optional.fromNullable(
                            ds.getDocumentReference("ref"));
                    if (opDocRef.isPresent()) {
                        DocumentReference dr = opDocRef.get();
                        tasks.add(dr.get());
                    }
                }

                Tasks.whenAllSuccess(tasks).addOnSuccessListener(
                        new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> objects) {
                                for (Object ds :
                                        objects) {
                                    ListChatModel listChatModel =
                                            ((DocumentSnapshot) ds).toObject(ListChatModel.class);
                                    listChatModels.add(listChatModel);
                                    listChatViewModels.add(
                                            new ListChatViewModel(listChatModel.getTitle(),
                                                    "Test", listChatModel.getImageChat(),
                                                    "test",
                                                    ((DocumentSnapshot) ds).getId()));
                                    listChatAdapter.updateChatListView(listChatViewModels);
                                }
                            }
                        });
            }
        });
        return view;
    }

    private void requestPermission() {
        new AlertDialog.Builder(getContext())
                .setTitle("Potrzebne uprawnienia")
                .setMessage("Uprawnienia są potrzebne do działania aplikacji")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA},
                                PERMISSION);
                    }
                })
                .setNegativeButton("wyjdź", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermission();
            }
        }
    }

    public void makeToast(String word) {
        Toast.makeText(ctx, word, Toast.LENGTH_LONG).show();
    }
}