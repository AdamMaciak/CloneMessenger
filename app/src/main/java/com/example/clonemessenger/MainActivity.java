package com.example.clonemessenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    Button toLogin;
    Button secondFragmentButton;

    AuthenticationFragment authenticationFragment;
    FragmentManager fragmentManager;
    SecondFragment secondFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toLogin = findViewById(R.id.toLogin);
        secondFragmentButton =findViewById(R.id.button);
        fragmentManager= getSupportFragmentManager();
        authenticationFragment=new AuthenticationFragment();
        secondFragment= new SecondFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer,authenticationFragment).commit();
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,authenticationFragment).commit();
            }
        });
        secondFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,secondFragment).commit();
            }
        });
    }


}
