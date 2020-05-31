package com.example.clonemessenger.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clonemessenger.MainActivity;
import com.example.clonemessenger.NewOpenChatFragment;
import com.example.clonemessenger.R;
import com.example.clonemessenger.ViewModels.ListChatViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ViewHolder> {
    final private int VIEWS_ON_BEGINNING = 16;
    private int VIEWS_COUNT;
    private List<ListChatViewModel> listChatViewModels;
    FirebaseFirestore db;
    private Context ctx;
    private NewOpenChatFragment newOpenChatFragment;
    private List<String> photoUrl=new ArrayList<>();

    public ListChatAdapter() {
        this.listChatViewModels = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        newOpenChatFragment = new NewOpenChatFragment();
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
        holder.circleImageView.setTag(position);
        holder.viewForTitle.setText(listChatViewModels.get(position).getTitle());
        holder.viewForLastMessage.setText(listChatViewModels.get(position).getLastMessage());
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
        photoUrl.add(listChatViewModels.get(position).getImageChatPath());
        holder.time.setText(localDateFormat.format(listChatViewModels.get(position).getLastMessageDate()));
        Glide.with(ctx).load(listChatViewModels.get(position).getImageChatPath()).into(holder.circleImageView);
        holder.listChatViewModel = listChatViewModels.get(position);

        if(listChatViewModels.get(position).getCountUnreadMessages()>0){
            holder.counter.setVisibility(View.VISIBLE);
            holder.counter.setText(String.valueOf(listChatViewModels.get(position).getCountUnreadMessages()));
            holder.viewForTitle.setTypeface(holder.viewForTitle.getTypeface(), Typeface.BOLD_ITALIC);
            holder.viewForLastMessage.setTypeface(holder.viewForLastMessage.getTypeface(), Typeface.BOLD_ITALIC);
        }
    }

    @Override
    public int getItemCount() {
        return VIEWS_COUNT;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView viewForTitle;
        TextView time;
        TextView counter;
        CircleImageView circleImageView;
        TextView viewForLastMessage;
        ListChatViewModel listChatViewModel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.imageChat);
            viewForLastMessage = itemView.findViewById(R.id.lastMessage);
            counter= itemView.findViewById(R.id.counter);
            viewForTitle = itemView.findViewById(R.id.titleChat);
            time= itemView.findViewById(R.id.txTime);
            listChatViewModel = new ListChatViewModel();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newOpenChatFragment.setListChatViewModel(listChatViewModel);
                    Bundle bundle = new Bundle();
                    bundle.putString("title",viewForTitle.getText().toString());
                    System.out.println("-----------------------"+photoUrl.get((int)circleImageView.getTag()));
                    bundle.putString("photo",photoUrl.get((int)circleImageView.getTag()));
                    newOpenChatFragment.setArguments(bundle);
                    ((AppCompatActivity) ctx).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, newOpenChatFragment)
                            .commit();
                    MainActivity.getBottomBar().setVisibility(View.GONE);
                }
            });
        }
    }

    public void updateChatListView(List<ListChatViewModel> updated) {
        listChatViewModels.clear();
        for (ListChatViewModel l :
                updated) {
            listChatViewModels.add(new ListChatViewModel(l.getTitle(), l.getLastMessage(),
                    l.getImageChatPath(),
                    l.getLastMessageDate(), l.getIdChat(), l.getCountUnreadMessages()));
        }
        VIEWS_COUNT = listChatViewModels.size();
        notifyDataSetChanged();
    }

    public void makeToast(String word) {
        Toast.makeText(ctx, word, Toast.LENGTH_LONG).show();
    }
}
