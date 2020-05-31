package com.example.clonemessenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.clonemessenger.Adapters.ContactsAdapter;
import com.example.clonemessenger.Models.UserModel;
import com.example.clonemessenger.Models.UserModelWithRef;
import com.example.clonemessenger.Models.UserSharedPref;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class ContactFragment extends Fragment {

    FirebaseFirestore db;
    private String userId;
    FloatingActionButton toAddContactFragment;
    AddContactFragment addContactFragment;
    RecyclerView recyclerView;
    ContactsAdapter contactsAdapter;
    List<UserModelWithRef> userModelWithRefs;
    UserSharedPref userSharedPref;
    List<Task<DocumentSnapshot>> tasks;
    Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ctx = getActivity();
        SharedPreferences preferences=getActivity().getSharedPreferences("Settings",MODE_PRIVATE);
        String language= preferences.getString("Lang","");
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        getActivity().setTitle(getResources().getString(R.string.contacts));
        userSharedPref = SharedPrefUser.getInstance(getContext()).getUser();
        tasks = new ArrayList<>();
        userModelWithRefs = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
       /* db.collection("koncepcjaChatu").whereEqualTo("uczestnicy", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        System.out.println("---------------------" + value);
                    }
                });*/

        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        toAddContactFragment = v.findViewById(R.id.navigateToAddContactFragment);

        contactsAdapter = new ContactsAdapter();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext());

        recyclerView = v.findViewById(R.id.forContacts);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contactsAdapter);

        db.collection("user")
                .document(userSharedPref.getId())
                .collection("contacts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentSnapshot> documentSnapshots =
                                queryDocumentSnapshots.getDocuments();
                        userModelWithRefs.clear();
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
                                                userModelWithRefs.add(new UserModelWithRef(
                                                        documentSnapshot.getReference()
                                                                .getPath(),
                                                        documentSnapshot.toObject(UserModel.class)));
                                            }
                                        }));
                            }
                        }
                        Tasks.whenAll(tasks).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        contactsAdapter.updateRecycleView(
                                                userModelWithRefs);
                                        tasks.clear();
                                    }
                                });
                    }
                });
        toAddContactFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactFragment = new AddContactFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        addContactFragment).commit();
            }
        });

        return v;
    }

    public void makeToast(String word) {
        Toast.makeText(ctx, word, Toast.LENGTH_LONG).show();
    }
}
