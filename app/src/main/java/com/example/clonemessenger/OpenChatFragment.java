package com.example.clonemessenger;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OpenChatFragment extends Fragment {

    ChatAdapter chatAdapter;
    List<ChatModel> chat=new ArrayList<>();
    RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ImageButton btn_sendMessage,btn_getFromGallery,btn_takePhoto;
    private EditText et_message;
    private FirebaseUser fUser;
    private int GALLERY_REQUEST_CODE=1;
    private String userId;
    FirebaseFirestore db;
    String filename;
    Uri filePath;
    boolean selected_image=false;
    FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chat.clear();
        View root = inflater.inflate(R.layout.fragment_open_chat, container, false);
        recyclerView=(RecyclerView) root.findViewById(R.id.rvChat);
        recyclerView.setHasFixedSize(true);
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        System.out.println(account.getPhotoUrl());
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        btn_sendMessage= (ImageButton) root.findViewById(R.id.btn_sendMessage);
        btn_getFromGallery= (ImageButton) root.findViewById(R.id.btn_getFromGallery);
        btn_takePhoto= (ImageButton) root.findViewById(R.id.btn_takePhoto);
        et_message=(EditText) root.findViewById(R.id.et_message);
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        userId=fUser.getUid();
        storage = FirebaseStorage.getInstance();

        db = FirebaseFirestore.getInstance();
        db.collection("messages")
                .document("Bqe17YUMVOc87njQktphxar85R63-cjvJnAr9dubyVrGZ7avyfAyaGFy1").collection("ChatModel")
                .orderBy("timeSend")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            ChatModel city = doc.toObject(ChatModel.class);
                            chat.add(city);
                        }


                        System.out.println("----------------------UWAGA");
                        for(ChatModel ch: chat){
                            System.out.println(ch.getMessage()+ "  "+ch.getSender());
                        }
                        //System.out.println(account.getPhotoUrl());
                        mAdapter = new ChatAdapter(getContext(), chat,account.getPhotoUrl());
                        recyclerView.setAdapter(mAdapter);
                    }
                });
        btn_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String message=et_message.getText().toString();
               if(!message.equals("") || selected_image==true) {
                   chat.clear();
                   final Date currentTime = Calendar.getInstance().getTime();
                   if(!message.equals("") && selected_image==false) {
                       ChatModel chatModel = new ChatModel(userId, "cjvJnAr9dubyVrGZ7avyfAyaGFy1", message, "", currentTime);
                       db.collection("messages").document("Bqe17YUMVOc87njQktphxar85R63-cjvJnAr9dubyVrGZ7avyfAyaGFy1").collection("ChatModel").add(chatModel);
                       et_message.setText("");
                   } else if(!message.equals("") && selected_image==true){
                       StorageReference riversRef = storage.getReference().child("images/"+filename);
                       final String filename1=filename;
                       System.out.println(filePath);
                       UploadTask uploadTask = riversRef.putFile(filePath);
                       uploadTask.addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception exception) {
                           }
                       }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                               Toast.makeText(getContext(),"Uploaded photo",Toast.LENGTH_SHORT);
                               System.out.println("------------------------------FILENAME"+ filename1);
                               ChatModel chatModel = new ChatModel(userId, "cjvJnAr9dubyVrGZ7avyfAyaGFy1", message, filename1, currentTime);
                               db.collection("messages").document("Bqe17YUMVOc87njQktphxar85R63-cjvJnAr9dubyVrGZ7avyfAyaGFy1").collection("ChatModel").add(chatModel);



                           }
                       });
                       et_message.setText("");
                       filename="";
                       filePath=null;
                       selected_image=false;
                       btn_takePhoto.setImageResource(R.drawable.ic_photo_camera);
                       btn_getFromGallery.setImageResource(R.drawable.ic_insert_photo);
                       btn_takePhoto.setVisibility(View.VISIBLE);
                       btn_getFromGallery.setVisibility(View.VISIBLE);
                   } else if(message.equals("") && selected_image==true){

                       btn_takePhoto.setImageResource(R.drawable.ic_photo_camera);
                       btn_getFromGallery.setImageResource(R.drawable.ic_insert_photo);
                       btn_takePhoto.setVisibility(View.VISIBLE);
                       btn_getFromGallery.setVisibility(View.VISIBLE);
                   }
               }

            }
        });
        btn_getFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                // Sets the type as image/*. This ensures only components of type image are selected
                intent.setType("image/*");
                //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                // Launching the Intent
                startActivityForResult(intent,GALLERY_REQUEST_CODE);
            }
        });

        return root;
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode==GALLERY_REQUEST_CODE) {
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                filePath=Uri.fromFile(new File(getRealPathFromURI(selectedImage)));
                filename=filePath.getLastPathSegment();
                btn_getFromGallery.setImageResource(R.drawable.ic_cancel);
                btn_takePhoto.setVisibility(View.INVISIBLE);
                selected_image=true;
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
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

}
