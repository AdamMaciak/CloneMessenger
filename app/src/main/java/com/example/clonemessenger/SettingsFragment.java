package com.example.clonemessenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.clonemessenger.Models.ChatModel;
import com.example.clonemessenger.Models.UserModel;
import com.example.clonemessenger.Models.UserSharedPref;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class SettingsFragment extends Fragment{
    Button toLogin;
    AuthenticationFragment authenticationFragment;
    MainActivity mainActivity;
    Context context;
    GoogleSignInAccount account;
    TextView tx_userName,tx_log;
    ImageView im_userPhoto,im_log;
    LinearLayout ll_log, ll_chLanguange,ll_chInterfaceColor;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;
    AdView mAdView;
    View view;
    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        db = FirebaseFirestore.getInstance();
        account = GoogleSignIn.getLastSignedInAccount(getContext());
        tx_userName=(TextView) view.findViewById(R.id.tx_userName);
        im_userPhoto=(ImageView) view.findViewById(R.id.im_profilePhoto);
        tx_log=(TextView) view.findViewById(R.id.tx_log);
        im_log=(ImageView) view.findViewById(R.id.im_log);
        ll_log=(LinearLayout) view.findViewById(R.id.ll_sign);
        ll_chLanguange=(LinearLayout) view.findViewById(R.id.ll_chLang);
        ll_chInterfaceColor=(LinearLayout) view.findViewById(R.id.ll_chInterfColor);
        ll_chLanguange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguangeDialog();
            }
        });
        ll_chInterfaceColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeInterfaceColor();
            }
        });
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                mAdView = view.findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        });

        if(SharedPrefUser.getInstance(getContext()).isLoggedIn()){
            UserSharedPref userSharedPref=SharedPrefUser.getInstance(getContext()).getUser();
            Glide.with(getContext()).load(Uri.parse(userSharedPref.getImagePath())).into(im_userPhoto);
            tx_userName.setText(userSharedPref.getName());
            im_log.setImageResource(R.drawable.ic_logout);
            tx_log.setText(getResources().getString(R.string.sign_out));
            tx_userName.setVisibility(View.VISIBLE);
            im_userPhoto.setVisibility(View.VISIBLE);
            ll_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   signOut();
                }
            });
        } else {
            ll_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }
        mainActivity = (MainActivity) getActivity();
        context = getActivity();
        return view;
    }
    private void signOut() {
        mGoogleSignInClient.signOut();
        SharedPrefUser.getInstance(getContext()).logout();
        Toast.makeText(getActivity(), "You are Logged Out", Toast.LENGTH_SHORT).show();
        im_log.setImageResource(R.drawable.ic_login);
        tx_log.setText(getResources().getString(R.string.sign_in));
        tx_userName.setVisibility(View.GONE);
        im_userPhoto.setVisibility(View.GONE);
        ll_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
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
            Toast.makeText(getActivity(), "Signed In Successfully", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT)
                                        .show();
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
                    .document(fUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {
                        UserModel userModel = documentSnapshot.toObject(UserModel.class);
                        tx_userName.setText(userModel.getName());
                        Glide.with(getContext()).load(userModel.getImagePath()).into(im_userPhoto);
                        UserSharedPref userSharedPref=new UserSharedPref(userModel.getName(),userModel.getImagePath(),fUser.getUid());
                        SharedPrefUser.getInstance(getContext()).userLogin(userSharedPref);

                    } else {
                        UserModel user=new UserModel(account.getDisplayName(),account.getPhotoUrl().toString());
                        db.collection("user").document(fUser.getUid()).set(user);
                        Glide.with(getContext()).load(account.getPhotoUrl()).into(im_userPhoto);
                        tx_userName.setText(account.getDisplayName());
                        UserSharedPref userSharedPref=new UserSharedPref(account.getDisplayName(),account.getPhotoUrl().toString(),fUser.getUid());
                        SharedPrefUser.getInstance(getContext()).userLogin(userSharedPref);
                        }
                    }
                });
            }
            im_log.setImageResource(R.drawable.ic_logout);
            tx_log.setText(getResources().getString(R.string.sign_out));
            tx_userName.setVisibility(View.VISIBLE);
            im_userPhoto.setVisibility(View.VISIBLE);
            ll_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
        }
    public void showChangeLanguangeDialog(){
        final String[] listItems={"English","Polish"};
        AlertDialog.Builder mBuilder= new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Change Language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    setLocate("en");
                    getActivity().recreate();
                } else if(i==1){
                    setLocate("pl");
                    getActivity().recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog=mBuilder.create();
        mDialog.show();
    }

    private void setLocate(String lang) {
        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getContext().getResources().updateConfiguration(configuration,getContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor= getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("Lang",lang);
        editor.apply();
    }
    public void showChangeInterfaceColor(){
        final String[] listItems={"Czerwony","Fioletowy"};
        AlertDialog.Builder mBuilder= new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Change Language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor= getContext().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
                if(i==0){
                    editor.putString("ColorInterface","red");
                    editor.apply();
                    getActivity().recreate();
                } else if(i==1){
                    editor.putString("ColorInterface","purple");
                    editor.apply();
                    getActivity().recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog=mBuilder.create();
        mDialog.show();
    }
}
