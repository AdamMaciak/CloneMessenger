package com.example.clonemessenger;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.Optional;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ListChatFragment extends Fragment {

    private OpenChatFragment openChatFragment;
    private int PERMISSION = 1000;
    private Context ctx;

    FirebaseFirestore db;
    ListChatAdapter listChatAdapter;
    RecyclerView recyclerView;

    List<ListChatModel> listChatModels;
    List<ListChatViewModel> listChatViewModels;
    List<Task<DocumentSnapshot>> tasks;

    UserSharedPref userSharedPref;

    FloatingActionButton toAddChatFragment;

    AddChatFragment addChatFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        db = FirebaseFirestore.getInstance();
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
        userSharedPref = SharedPrefUser.getInstance(getContext()).getUser();

        toAddChatFragment = view.findViewById(R.id.navigateToAddChatFragment);

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }

        listChatViewModels = new ArrayList<>();

        //TODO
        db.collection("user")
                .document(userSharedPref.getId())
                .collection("refToChat")
                .orderBy("LastMessageDate").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("FirestoreException", "Listen failed.", e);
                    return;
                }

                List<DocumentSnapshot> documentSnapshots =
                        queryDocumentSnapshots.getDocuments();
                listChatViewModels.clear();
                listChatModels.clear();
                for (DocumentSnapshot ds :
                        documentSnapshots) {
                    Optional<DocumentReference> opDocRef = Optional.fromNullable(
                            ds.getDocumentReference("refToChat"));
                    if (opDocRef.isPresent()) {
                        DocumentReference dr = opDocRef.get();
                        final String LastMessage = (String) ds.get("LastMessage");
                        final Date LastMessageDate =
                                ds.getTimestamp("LastMessageDate").toDate();
                        final long countUnreadMessages = ds.getLong("countUnreadMessages");
                        tasks.add(dr.get().addOnSuccessListener(
                                new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        ListChatModel listChatModel =
                                                documentSnapshot.toObject(
                                                        ListChatModel.class);
                                        listChatModels.add(listChatModel);
                                        if (listChatModel != null) {
                                            listChatViewModels.add(
                                                    new ListChatViewModel(listChatModel.getTitle(),
                                                            LastMessage,
                                                            listChatModel.getImageChat(),
                                                            LastMessageDate.toString(),
                                                            documentSnapshot.getId(),
                                                            countUnreadMessages));
                                        }
                                    }
                                }));
                    }
                }

                Tasks.whenAll(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listChatAdapter.updateChatListView(listChatViewModels);
                        tasks.clear();
                    }
                });
//                Tasks.whenAllSuccess(tasks).addOnSuccessListener(
//                        new OnSuccessListener<List<Object>>() {
//                            @Override
//                            public void onSuccess(List<Object> objects) {
//                                for (Object ds :
//                                        objects) {
//                                    ListChatModel listChatModel =
//                                            ((DocumentSnapshot) ds).toObject(
//                                                    ListChatModel.class);
//                                    listChatModels.add(listChatModel);
//                                    listChatViewModels.add(
//                                            new ListChatViewModel(listChatModel.getTitle(),
//                                                    "Test", listChatModel.getImageChat(),
//                                                    "test",
//                                                    ((DocumentSnapshot) ds).getId()));
//                                }
//                                listChatAdapter.updateChatListView(listChatViewModels);
//                                tasks.clear();
//                            }
//                        });
            }
        });
        toAddChatFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChatFragment = new AddChatFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        addChatFragment).commit();
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
