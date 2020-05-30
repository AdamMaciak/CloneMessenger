package com.example.clonemessenger;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clonemessenger.Adapters.AddContactAdapter;
import com.example.clonemessenger.Models.UserModel;
import com.example.clonemessenger.Models.UserModelWithRef;
import com.example.clonemessenger.Models.UserSharedPref;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.clonemessenger.Adapters.AddContactAdapter;
import com.example.clonemessenger.Adapters.ContactsAdapter;
import com.example.clonemessenger.Models.UserSharedPref;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AddContactFragment extends Fragment {

    FirebaseFirestore db;
    MaterialButton searchButton;
    EditText searchContact;
    RecyclerView recView;
    AddContactAdapter addContactAdapter;
    UserSharedPref userSharedPref;
    Context ctx;
    List<UserModel> userModels;
    List<UserModelWithRef> userModelWithRefs;

    public AddContactFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
        userSharedPref = SharedPrefUser.getInstance(getContext()).getUser();
        userModels = new ArrayList<>();
        ctx = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_contact, container, false);
        searchButton = v.findViewById(R.id.searchButton);
        searchContact = v.findViewById(R.id.nameUser);
        addContactAdapter = new AddContactAdapter();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext());
        recView = v.findViewById(R.id.recViewForContacts);
        recView.setLayoutManager(linearLayoutManager);
        recView.setHasFixedSize(true);
        recView.setAdapter(addContactAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("user").get()
                        .addOnSuccessListener(
                                new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        userModels.clear();
                                        List<DocumentSnapshot> documentSnapshots =
                                                queryDocumentSnapshots
                                                        .getDocuments();
                                        userModelWithRefs =
                                                new ArrayList<>();
                                        for (DocumentSnapshot ds :
                                                documentSnapshots) {
                                            userModelWithRefs.add(new UserModelWithRef(
                                                    ds.getReference().getPath(),
                                                    ds.toObject(UserModel.class)));
                                        }
                                        addContactAdapter.updateRecycleView(
                                                sortByName(userModelWithRefs,
                                                        searchContact.getText().toString()));
                                    }
                                });
            }
        });
        return v;
    }

    private List<UserModelWithRef> sortByName(List<UserModelWithRef> toSort, String name) {
        return toSort.stream()
                .filter(x -> x.getUserModel().getName().contains(name))
                .collect(Collectors.toList());
    }


    public void makeToast(String word) {
        Toast.makeText(ctx, word, Toast.LENGTH_LONG).show();
    }
}
