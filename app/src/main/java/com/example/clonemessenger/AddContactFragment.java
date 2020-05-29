package com.example.clonemessenger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.clonemessenger.Adapters.AddContactAdapter;
import com.example.clonemessenger.Adapters.ContactsAdapter;
import com.example.clonemessenger.Models.UserSharedPref;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class AddContactFragment extends Fragment {

    FirebaseFirestore db;
    MaterialButton searchButton;
    EditText searchContact;
    RecyclerView recView;
    AddContactAdapter addContactAdapter;
    UserSharedPref userSharedPref;
    public AddContactFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_contact, container, false);
        searchButton = v.findViewById(R.id.searchButton);
        searchContact =(EditText) v.findViewById(R.id.NameUser);


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
                db.collection("user")
                        .document(searchContact.getText().toString())
                        .get()
                        .addOnSuccessListener(
                                new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    }
                                });
            }
        });
        return v;
    }

    private void addContact(List<DocumentReference> referenceToContact) {

    }
}