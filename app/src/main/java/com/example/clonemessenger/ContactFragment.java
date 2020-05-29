package com.example.clonemessenger;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ContactFragment extends Fragment {

    FirebaseFirestore db;
    private String userId;
    private FirebaseUser fUser;
    FloatingActionButton toAddContactFragment;
    AddContactFragment addContactFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fUser= FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();
        db.collection("koncepcjaChatu").whereEqualTo("uczestnicy", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        System.out.println("---------------------"+value);
                    }
                });

        View v=inflater.inflate(R.layout.fragment_contact, container, false);
        toAddContactFragment=v.findViewById(R.id.navigateToAddContactFragment);

        toAddContactFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactFragment=new AddContactFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        addContactFragment).commit();
            }
        });

        return v;
    }
}
