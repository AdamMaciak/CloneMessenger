package com.example.clonemessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    Button toLogin;
    Button toChat;
//    Button secondFragmentButton;

    AuthenticationFragment authenticationFragment;
    ContactFragment contactFragment;
    ChatsFragment chatsFragment;
    SettingsFragment settingsFragment;
    static public BottomNavigationView bottomBar;
    OpenChatFragment openChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authenticationFragment = new AuthenticationFragment();
        openChatFragment=new OpenChatFragment();

        contactFragment = new ContactFragment();
        chatsFragment = new ChatsFragment();
        settingsFragment = new SettingsFragment();
        //  Optional<GoogleSignInAccount> account= Optional.ofNullable(GoogleSignIn.getLastSignedInAccount(getApplicationContext()));
        bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_contacts:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, contactFragment).commit();
                        return true;
                    case R.id.action_chats:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, chatsFragment).commit();
                        return true;
                    case R.id.action_settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, settingsFragment).commit();
                        return true;
                }
                return true;
            }
        });

        toLogin=findViewById(R.id.button1);

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, authenticationFragment).commit();
            }
        });
        toChat=findViewById(R.id.button2);

        toChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, openChatFragment).commit();
            }
        });
    }

    static public BottomNavigationView getBottomBar() {
        return bottomBar;
    }
}
