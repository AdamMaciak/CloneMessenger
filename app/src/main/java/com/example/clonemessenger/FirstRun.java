package com.example.clonemessenger;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.clonemessenger.Models.UserModel;
import com.example.clonemessenger.Models.UserSharedPref;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class FirstRun extends Fragment {
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private Button btnSignOut;
    private int RC_SIGN_IN = 1;
    Spinner spinnerLanguage, spinnerTheme;
    ArrayAdapter arrayLanguage, arrayTheme;
    List<String> languageList = new ArrayList<>();
    List<String> themeList = new ArrayList<>();
    TextView tx1;
    Button save;
    FirebaseFirestore db;
    UserSharedPref userSharedPref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        SharedPreferences preferences = getActivity().getSharedPreferences("Settings",
                MODE_PRIVATE);
        String language = preferences.getString("Lang", "");
        String color = preferences.getString("ColorInterface", "");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        View view = inflater.inflate(R.layout.fragment_first_run, container, false);
        db = FirebaseFirestore.getInstance();
        signInButton = view.findViewById(R.id.sign_in);
        save = (Button) view.findViewById(R.id.btnSave);
        tx1 = (TextView) view.findViewById(R.id.textView7);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(
                getContext().getApplicationContext());
        if (account != null) {
            signInButton.setVisibility(View.GONE);
            save.setVisibility(View.VISIBLE);
            tx1.setText(
                    getResources().getString(R.string.welcome1) + " " + SharedPrefUser.getInstance(
                            view.getContext()).getUser().getName());
        }

        mAuth = FirebaseAuth.getInstance();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("Settings",
                        MODE_PRIVATE).edit();
                editor.putBoolean("firstStart", false);
                editor.apply();
                MainActivity.bottomBar.setVisibility(View.VISIBLE);
                ListChatFragment listChatFragment = new ListChatFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer,
                                listChatFragment)
                        .commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);


        languageList.add(getResources().getString(R.string.english));
        languageList.add(getResources().getString(R.string.polish));
        themeList.add(getResources().getString(R.string.purple));
        themeList.add(getResources().getString(R.string.red));


        spinnerLanguage = (Spinner) view.findViewById(R.id.spinner);
        spinnerTheme = (Spinner) view.findViewById(R.id.spinner2);
        arrayLanguage = new ArrayAdapter(getContext().getApplicationContext(),
                R.layout.spinner_item, languageList);
        arrayLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(arrayLanguage);
        if (language.equals("en")) {
            spinnerLanguage.setSelection(0);
        } else if (language.equals("pl")) {
            spinnerLanguage.setSelection(1);
        }


        arrayTheme = new ArrayAdapter(getContext().getApplicationContext(), R.layout.spinner_item,
                themeList);
        arrayTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(arrayTheme);
        if (color.equals("purple")) {
            spinnerTheme.setSelection(0);
        } else if (color.equals("red")) {
            spinnerTheme.setSelection(1);
        }
        final int currentSelectTheme = spinnerTheme.getSelectedItemPosition();
        final int currentSelectLanguage = spinnerLanguage.getSelectedItemPosition();

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                if (position == 0 && currentSelectLanguage != position) {
                    setLocate("en");
                    getActivity().recreate();
                } else if (position == 1 && currentSelectLanguage != position) {
                    setLocate("pl");
                    getActivity().recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                       int position, long id) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("Settings",
                        MODE_PRIVATE).edit();
                if (position == 0 && currentSelectTheme != position) {
                    editor.putString("ColorInterface", "purple");
                    editor.apply();
                    getActivity().recreate();
                } else if (position == 1 && currentSelectTheme != position) {
                    editor.putString("ColorInterface", "red");
                    editor.apply();
                    getActivity().recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        return view;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("ELO!");
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            System.out.println("ELO?");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(getActivity(), "Sign In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),
                    null);
            mAuth.signInWithCredential(authCredential)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "acc failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(final FirebaseUser fUser) {
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(
                getContext().getApplicationContext());
        if (account != null) {
            db.collection("user")
                    .document(fUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                userModel.setOnline(true);
                                db.collection("user").document(fUser.getUid()).set(userModel);
                                tx1.setText(getResources().getString(
                                        R.string.welcome1) + " " + userModel.getName());
                                UserSharedPref userSharedPref = new UserSharedPref(
                                        userModel.getName(), userModel.getImagePath(),
                                        userModel.getImageCompressPath(), fUser.getUid(),
                                        userModel.isFullVersion());
                                SharedPrefUser.getInstance(getContext()).userLogin(userSharedPref);

                            } else {
                                UserModel user = new UserModel(account.getDisplayName(), "null",
                                        account.getPhotoUrl().toString(), false, true);
                                db.collection("user").document(fUser.getUid()).set(user);
                                tx1.setText(getResources().getString(
                                        R.string.welcome1) + " " + account.getDisplayName());
                                UserSharedPref userSharedPref = new UserSharedPref(
                                        account.getDisplayName(), "null",
                                        account.getPhotoUrl().toString(), fUser.getUid(), false);
                                SharedPrefUser.getInstance(getContext()).userLogin(userSharedPref);
                            }
                            signInButton.setVisibility(View.GONE);
                            save.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    private void setLocate(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getContext().getResources()
                .updateConfiguration(configuration,
                        getContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getContext().getSharedPreferences("Settings",
                MODE_PRIVATE).edit();
        editor.putString("Lang", lang);
        editor.apply();
    }

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

}