package com.example.clonemessenger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ChatsFragment extends Fragment {

    private Button toChat;
    private OpenChatFragment openChatFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        toChat = view.findViewById(R.id.button);
        openChatFragment = new OpenChatFragment();
        toChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, openChatFragment).commit();
            }
        });
        return view;
    }

}
