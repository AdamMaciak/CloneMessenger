package com.example.clonemessenger;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SettingsFragment extends Fragment{
    Button toLogin;
    AuthenticationFragment authenticationFragment;
    MainActivity mainActivity;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        authenticationFragment = new AuthenticationFragment();
        toLogin = view.findViewById(R.id.button);
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, authenticationFragment)
                        .commit();
            }
        });
        mainActivity = (MainActivity) getActivity();
        context = getActivity();
        //Toast.makeText(context,mainActivity.getCostam(),Toast.LENGTH_LONG).show();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(context, "OnResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(context, "OnStart", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(context, "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(context, "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(context, "onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Toast.makeText(context, "onDetach", Toast.LENGTH_LONG).show();
    }
}
