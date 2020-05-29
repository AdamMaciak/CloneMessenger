package com.example.clonemessenger;

import android.os.Bundle;

import androidx.core.app.BundleCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;


public class AddChatFragment extends Fragment {
    MaterialButton toAddChatFragment;
    AddChatFragment addChatFragment;
    Bundle bundle;
    public AddChatFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_chat, container, false);

        return v;
    }
}
