package com.example.clonemessenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import android.provider.MediaStore;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class SettingsFragment extends Fragment{
    Button toLogin, save;
    EditText newUserName;
    AuthenticationFragment authenticationFragment;
    MainActivity mainActivity;
    Context context;
    GoogleSignInAccount account;
    TextView tx_userName,tx_log;
    ImageView im_userPhoto,im_log;
    LinearLayout ll_log, ll_chLanguange, ll_chInterfaceColor, ll_chUserName, ll_chUserPhoto, ll_removeAds;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;
    AdView mAdView;
    View view;
    FirebaseFirestore db;
    Dialog chUserName;
    int galleryRequestCode=2;
    FirebaseStorage storage;
    Context mContext;
    public static final int PAYPAL_REQUEST_CODE=7171;
    public static final String PAYPAL_CLIENT_ID= "AZKoWU4wSPYaJ8eyXiz04Ley0tNyBNNnIu7-vdkcr2QbPK-BLVlOwlCJAbDIoWuj7YmB8M0LQ-0bfwrW";
    private PayPalConfiguration config= new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(PAYPAL_CLIENT_ID);

    @Override
    public void onDestroy() {
        getContext().stopService(new Intent(getContext(),PayPalService.class));
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences=getActivity().getSharedPreferences("Settings",MODE_PRIVATE);
        String language= preferences.getString("Lang","");
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        Intent intent= new Intent(getContext(),PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        getContext().startService(intent);
        mContext=getContext();
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        account = GoogleSignIn.getLastSignedInAccount(getContext());
        mAdView = view.findViewById(R.id.adView);
        tx_userName=(TextView) view.findViewById(R.id.tx_userName);
        im_userPhoto=(ImageView) view.findViewById(R.id.im_profilePhoto);
        tx_log=(TextView) view.findViewById(R.id.tx_log);
        im_log=(ImageView) view.findViewById(R.id.im_log);
        ll_log=(LinearLayout) view.findViewById(R.id.ll_sign);
        ll_chLanguange=(LinearLayout) view.findViewById(R.id.ll_chLang);
        ll_chInterfaceColor=(LinearLayout) view.findViewById(R.id.ll_chInterfColor);
        ll_chUserName=(LinearLayout) view.findViewById(R.id.ll_chUserName);
        ll_chUserPhoto=(LinearLayout) view.findViewById(R.id.ll_chUserPhoto);
        ll_removeAds= (LinearLayout) view.findViewById(R.id.ll_removeAds);
        ll_removeAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyFullVersion();
            }
        });
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
            }
        });

        if(SharedPrefUser.getInstance(getContext()).isLoggedIn()){
            UserSharedPref userSharedPref=SharedPrefUser.getInstance(getContext()).getUser();
            if(userSharedPref.isFullVersion()==false){

                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            } else {
                mAdView.setVisibility(View.GONE);
                ll_removeAds.setVisibility(View.GONE);
            }
            Glide.with(getContext()).load(Uri.parse(userSharedPref.getImageCompressPath())).into(im_userPhoto);
            tx_userName.setText(userSharedPref.getName());
            im_log.setImageResource(R.drawable.ic_logout);
            tx_log.setText(getResources().getString(R.string.sign_out));
            tx_userName.setVisibility(View.VISIBLE);
            im_userPhoto.setVisibility(View.VISIBLE);
            ll_chUserPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    startActivityForResult(intent, galleryRequestCode);
                }
            });
            ll_chUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chUserName=new Dialog(getActivity());
                    chUserName.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    chUserName.setContentView(R.layout.dialog_edittext);
                    newUserName=(EditText) chUserName.findViewById(R.id.editUserName);
                    newUserName.setText(SharedPrefUser.getUserName());
                    save= (Button) chUserName.findViewById(R.id.buttonSave);
                    save.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    final String temp = newUserName.getText().toString();
                                                    db.collection("user")
                                                            .document(SharedPrefUser.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                                                userModel.setName(temp);
                                                                db.collection("user").document(SharedPrefUser.getUserId()).set(userModel);
                                                                SharedPrefUser.getInstance(getContext()).setUserName(temp);
                                                                tx_userName.setText(temp);
                                                                chUserName.dismiss();
                                                        }
                                                    });
                                                }
                                            });
                    chUserName.show();
                }
            });
            ll_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   signOut();
                }
            });
        } else {
            mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            ll_chUserName.setVisibility(View.GONE);
            ll_chUserPhoto.setVisibility(View.GONE);
            ll_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
            ll_removeAds.setVisibility(View.GONE);
        }
        mainActivity = (MainActivity) getActivity();
        context = getActivity();
        return view;
    }

    private void buyFullVersion(){
        PayPalPayment payPalPayment=new PayPalPayment(new BigDecimal(5),"PLN",getResources().getString(R.string.fullVersion),PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent=new Intent(getContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    private void signOut() {
        mGoogleSignInClient.signOut();
        MainActivity.bottomBar.setVisibility(View.GONE);
        Date currentTime = Calendar.getInstance().getTime();
        UserSharedPref userSharedPref=SharedPrefUser.getInstance(getContext()).getUser();
        UserModel user=new UserModel(userSharedPref.getName(), userSharedPref.getImagePath(),userSharedPref.getImageCompressPath(),userSharedPref.isFullVersion(),false,currentTime);
        db.collection("user").document(userSharedPref.getId()).set(user);

        SharedPrefUser.getInstance(getContext()).logout();
        //Toast.makeText(getActivity(), "You are Logged Out", Toast.LENGTH_SHORT).show();
        im_log.setImageResource(R.drawable.ic_login);
        tx_log.setText(getResources().getString(R.string.sign_in));
        tx_userName.setVisibility(View.GONE);
        im_userPhoto.setVisibility(View.GONE);
        ll_chUserName.setVisibility(View.GONE);
        ll_chUserPhoto.setVisibility(View.GONE);
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
        } else if(resultCode == RESULT_OK && requestCode==galleryRequestCode){
            final ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setMessage(getResources().getString(R.string.changePhoto));
            progressDialog.show();
            Uri selectedImage = data.getData();
            Uri filePath = Uri.fromFile(new File(getRealPathFromURI(selectedImage)));
            final String filename = filePath.getLastPathSegment();
            Uri imageUri = data.getData();
            String compressFileName = null;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] compressedBitmap = baos.toByteArray();
                compressFileName="c"+filename;
                StorageReference riversRef = storage.getReference()
                        .child("profilePhotos/" + compressFileName);
                UploadTask uploadTask = riversRef.putBytes(compressedBitmap);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            StorageReference riversRef1 = storage.getReference()
                    .child("profilePhotos/" + filename);
            UploadTask uploadTask1 = riversRef1.putFile(filePath);
            final String finalCompressFileName = compressFileName;
            uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference riversRef2 = storage.getReference();
                    final String[] pathToImage = new String[1];
                    final String[] pathToCompressedImage = new String[1];
                    riversRef2.child("profilePhotos/"+filename).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            pathToImage[0] =uri.toString();
                            StorageReference riversRef3 = storage.getReference();
                            riversRef3.child("profilePhotos/"+ finalCompressFileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    pathToCompressedImage[0] =uri.toString();
                                    UserSharedPref userSharedPref=SharedPrefUser.getInstance(getContext()).getUser();
                                    userSharedPref.setImageCompressPath(pathToCompressedImage[0]);
                                    userSharedPref.setImagePath(pathToImage[0]);
                                    SharedPrefUser.getInstance(getContext()).userLogin(userSharedPref);
                                    UserModel user=new UserModel(userSharedPref.getName(), pathToImage[0],pathToCompressedImage[0],userSharedPref.isFullVersion(),true);
                                    db.collection("user").document(userSharedPref.getId()).set(user);
                                    Glide.with(getContext()).load(pathToCompressedImage[0]).into(im_userPhoto);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            });
        } else if(requestCode==PAYPAL_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                PaymentConfirmation confirmation=data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation!=null){
                    SharedPrefUser.getInstance(getContext()).setFullversion(true);
                    UserSharedPref userSharedPref=SharedPrefUser.getInstance(getContext()).getUser();
                    UserModel user=new UserModel(userSharedPref.getName(),userSharedPref.getImagePath(),userSharedPref.getImageCompressPath(),userSharedPref.isFullVersion(),true);
                    db.collection("user").document(userSharedPref.getId()).set(user);
                    mAdView.setVisibility(View.GONE);
                    ll_removeAds.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),getResources().getString(R.string.unlockFullVersion),Toast.LENGTH_LONG);
                }
            } else if(resultCode== Activity.RESULT_CANCELED) {
                Toast.makeText(getContext(),"Cancel",Toast.LENGTH_SHORT).show();
            }
        } else if(resultCode== PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(getContext(), "Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver()
                .query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            //Toast.makeText(getActivity(), "Signed In Successfully", Toast.LENGTH_SHORT).show();
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
                                //Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT)
                                        //.show();
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
                        userModel.setOnline(true);
                        db.collection("user").document(fUser.getUid()).set(userModel);
                        tx_userName.setText(userModel.getName());
                        Glide.with(getContext()).load(userModel.getImageCompressPath()).into(im_userPhoto);
                        UserSharedPref userSharedPref=new UserSharedPref(userModel.getName(),userModel.getImagePath(),userModel.getImageCompressPath(),fUser.getUid(),userModel.isFullVersion());
                        SharedPrefUser.getInstance(getContext()).userLogin(userSharedPref);
                        if(userSharedPref.isFullVersion()==true){
                            mAdView.setVisibility(View.GONE);
                            ll_removeAds.setVisibility(View.GONE);
                        } else {
                            ll_removeAds.setVisibility(View.VISIBLE);
                        }

                    } else {
                        UserModel user=new UserModel(account.getDisplayName(),"null",account.getPhotoUrl().toString(),false,true);
                        db.collection("user").document(fUser.getUid()).set(user);
                        Glide.with(getContext()).load(account.getPhotoUrl()).into(im_userPhoto);
                        tx_userName.setText(account.getDisplayName());
                        UserSharedPref userSharedPref=new UserSharedPref(account.getDisplayName(),"null",account.getPhotoUrl().toString(),fUser.getUid(),false);
                        SharedPrefUser.getInstance(getContext()).userLogin(userSharedPref);
                        ll_removeAds.setVisibility(View.VISIBLE);
                        }
                    }
                });
            MainActivity.bottomBar.setVisibility(View.VISIBLE);
            }
            im_log.setImageResource(R.drawable.ic_logout);
            tx_log.setText(getResources().getString(R.string.sign_out));
            tx_userName.setVisibility(View.VISIBLE);
            im_userPhoto.setVisibility(View.VISIBLE);
            ll_chUserName.setVisibility(View.VISIBLE);
            ll_chUserPhoto.setVisibility(View.VISIBLE);
            ll_removeAds.setVisibility(View.VISIBLE);
            ll_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
        }
    public void showChangeLanguangeDialog(){
        final String[] listItems={getResources().getString(R.string.english),getResources().getString(R.string.polish)};
        AlertDialog.Builder mBuilder= new AlertDialog.Builder(getContext());
        mBuilder.setTitle(getResources().getString(R.string.chooseL));
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
        SharedPreferences.Editor editor= getContext().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("Lang",lang);
        editor.apply();
    }

    public void showChangeInterfaceColor(){
        final String[] listItems={getResources().getString(R.string.red),getResources().getString(R.string.purple)};
        AlertDialog.Builder mBuilder= new AlertDialog.Builder(getContext());
        mBuilder.setTitle(getResources().getString(R.string.chooseC));
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor= getContext().getSharedPreferences("Settings", MODE_PRIVATE).edit();
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
