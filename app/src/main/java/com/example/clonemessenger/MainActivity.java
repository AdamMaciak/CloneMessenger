package com.example.clonemessenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clonemessenger.Models.UserModel;
import com.example.clonemessenger.Models.UserSharedPref;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    Button toLogin;
    Button toChat;
//    Button secondFragmentButton;
    AuthenticationFragment authenticationFragment;
    ContactFragment contactFragment;
    ListChatFragment listChatFragment;
    SettingsFragment settingsFragment;
    static public BottomNavigationView bottomBar;
    OpenChatFragment openChatFragment;
    FirebaseFirestore db;
    Context mContext;
    FirstRun firstRun;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       firstRun = new FirstRun();
       SharedPreferences preferences=getSharedPreferences("Settings",MODE_PRIVATE);
       boolean fStart=preferences.getBoolean("firstStart",true);

       getLocale();
       String colour= preferences.getString("ColorInterface","purple");
       switch(colour) {
           case "red":
               setTheme(R.style.RedTheme);
               break;
           case "purple":
               setTheme(R.style.AppTheme);
               break;
       }

        setContentView(R.layout.activity_main);

        mContext=getApplicationContext();
        db = FirebaseFirestore.getInstance();

       MobileAds.initialize(this, new OnInitializationCompleteListener() {
           @Override
           public void onInitializationComplete(InitializationStatus initializationStatus) {
           }
       });

        /*ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));*/
        authenticationFragment = new AuthenticationFragment();
        openChatFragment = new OpenChatFragment();

        contactFragment = new ContactFragment();
        listChatFragment = new ListChatFragment();
        settingsFragment = new SettingsFragment();
        //  Optional<GoogleSignInAccount> account= Optional.ofNullable(GoogleSignIn
        //  .getLastSignedInAccount(getApplicationContext()));
        bottomBar = findViewById(R.id.bottomBar);

       if(fStart) {
           bottomBar.setVisibility(View.GONE);
           getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                   firstRun).commit();
       }else {
           if(!SharedPrefUser.getInstance(getApplicationContext()).isLoggedIn()){
               bottomBar.setVisibility(View.GONE);
               getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                       settingsFragment).commit();
           }
       }
        bottomBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_contacts:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, contactFragment)
                                        .commit();
                                return true;
                            case R.id.action_chats:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, listChatFragment)
                                        .commit();
                                return true;
                            case R.id.action_settings:
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, settingsFragment)
                                        .commit();
                                return true;
                        }
                        return true;
                    }
                });

        toLogin = findViewById(R.id.button1);

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, authenticationFragment)
                        .commit();
            }
        });
        toChat = findViewById(R.id.button2);

        toChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, openChatFragment)
                        .commit();
            }
        });

    }

    static public BottomNavigationView getBottomBar() {
        return bottomBar;
    }

    private void setLocale(String lang) {
        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        SharedPreferences.Editor editor= getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("Lang",lang);
        editor.apply();

    }
    public void getLocale(){
        SharedPreferences preferences=getSharedPreferences("Settings",MODE_PRIVATE);
        String language= preferences.getString("Lang","en");
        setLocale(language);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(SharedPrefUser.getInstance(mContext).isLoggedIn()==true){
            UserSharedPref userSharedPref=SharedPrefUser.getInstance(mContext).getUser();
            UserModel user=new UserModel(userSharedPref.getName(), userSharedPref.getImagePath(),userSharedPref.getImageCompressPath(),userSharedPref.isFullVersion(),true);
            db.collection("user").document(userSharedPref.getId()).set(user);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(SharedPrefUser.getInstance(mContext).isLoggedIn()==true){
            Date currentTime = Calendar.getInstance().getTime();
            UserSharedPref userSharedPref=SharedPrefUser.getInstance(mContext).getUser();
            UserModel user=new UserModel(userSharedPref.getName(), userSharedPref.getImagePath(),userSharedPref.getImageCompressPath(),userSharedPref.isFullVersion(),false,currentTime);
            db.collection("user").document(userSharedPref.getId()).set(user);
        }
    }
}
