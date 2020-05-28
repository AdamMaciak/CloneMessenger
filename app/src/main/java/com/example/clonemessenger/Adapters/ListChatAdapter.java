package com.example.clonemessenger.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clonemessenger.MainActivity;
import com.example.clonemessenger.OpenChatFragment;
import com.example.clonemessenger.R;
import com.example.clonemessenger.ViewModels.ListChatViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ViewHolder> {
    final private int VIEWS_ON_BEGINNING = 16;
    private int VIEWS_COUNT;
    private List<ListChatViewModel> listChatViewModels;
    FirebaseFirestore db;
    private Context ctx;
    private OpenChatFragment openChatFragment;

    public ListChatAdapter() {
        this.listChatViewModels = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        openChatFragment = new OpenChatFragment();
        VIEWS_COUNT = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_info, parent, false);
        ctx = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.viewForTitle.setText(listChatViewModels.get(position)
                .getTitle() + getItemCount() + "position: " + position);
        holder.viewForLastMessage.setText(listChatViewModels.get(position).getLastMessage());
        holder.listChatViewModel = listChatViewModels.get(position);
    }

    @Override
    public int getItemCount() {
        return VIEWS_COUNT;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView viewForTitle;
        CircleImageView circleImageView;
        TextView viewForLastMessage;
        ListChatViewModel listChatViewModel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.imageChat);
            viewForLastMessage = itemView.findViewById(R.id.lastMessage);
            viewForTitle = itemView.findViewById(R.id.titleChat);
            listChatViewModel = new ListChatViewModel();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openChatFragment.setListChatViewModel(listChatViewModel);
                    ((AppCompatActivity) ctx).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, openChatFragment)
                            .commit();
                    MainActivity.getBottomBar().setVisibility(View.GONE);
                }
            });
        }
    }

    public void updateChatListView(List<ListChatViewModel> updated) {
        listChatViewModels.clear();;
        listChatViewModels=updated;
        VIEWS_COUNT = updated.size();
        notifyDataSetChanged();
    }

    public void makeToast(String word) {
        Toast.makeText(ctx, word, Toast.LENGTH_LONG).show();
    }
}