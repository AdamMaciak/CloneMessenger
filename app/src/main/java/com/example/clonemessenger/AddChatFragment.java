package com.example.clonemessenger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.clonemessenger.Models.ListChatModel;
import com.example.clonemessenger.Models.UserSharedPref;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class AddChatFragment extends Fragment {
    MaterialButton buttonAddChatFragment;
    FirebaseFirestore db;
    Context ctx;
    ImageView imageView;
    EditText chatNameEditText;
    EditText descriptionEditText;
    AddContactToChat addContactToChat;
    byte[] compressedBitmap;
    String photoUrl;
    int GALLERY_REQUEST_CODE = 1;
    String filename;
    FirebaseStorage storage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_chat, container, false);
        SharedPreferences preferences=getActivity().getSharedPreferences("Settings",MODE_PRIVATE);
        String language= preferences.getString("Lang","");
        Locale locale=new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration=new Configuration();
        configuration.locale=locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());
        getActivity().setTitle(getResources().getString(R.string.createChat));
        storage = FirebaseStorage.getInstance();
        chatNameEditText = v.findViewById(R.id.chatName);
        descriptionEditText = v.findViewById(R.id.descriptionChat);
        buttonAddChatFragment = v.findViewById(R.id.addChat);
        imageView = (ImageView) v.findViewById(R.id.chatPhoto);
        UserSharedPref userSharedPref = SharedPrefUser.getInstance(getContext()).getUser();
        Glide.with(getContext())
                .load(Uri.parse(userSharedPref.getImageCompressPath()))
                .into(imageView);
        photoUrl = userSharedPref.getImageCompressPath();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                // Sets the type as image/*. This ensures only components of type image are
                // selected
                intent.setType("image/*");
                //We pass an extra array with the accepted mime types. This will ensure only
                // components with these MIME types as targeted.
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                // Launching the Intent
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });
        buttonAddChatFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog
                        = new ProgressDialog(getContext());
                progressDialog.setMessage(getResources().getString(R.string.uploadPhoto));
                progressDialog.show();
                StorageReference riversRef = storage.getReference()
                        .child("chatPhoto/" + filename);
                UploadTask uploadTask = riversRef.putBytes(compressedBitmap);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        StorageReference riversRef2 = storage.getReference();
                        riversRef2.child("chatPhoto/" + filename)
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        progressDialog.dismiss();
                                        addNewChat(chatNameEditText.getText().toString(),
                                                descriptionEditText.getText().toString(),
                                                uri.toString());
                                        addChatToUser();
                                        addContactToChat = new AddContactToChat();
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.fragmentContainer,
                                                        addContactToChat)
                                                .commit();
                                    }
                                });
                    }
                });

            }
        });

        db = FirebaseFirestore.getInstance();
        ctx = v.getContext();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((resultCode == Activity.RESULT_OK || requestCode == GALLERY_REQUEST_CODE) && data != null) {
            Uri selectedImage = data.getData();
            Uri filePath = Uri.fromFile(new File(getRealPathFromURI(selectedImage)));
            filename = filePath.getLastPathSegment();
            Uri imageUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                        imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                compressedBitmap = baos.toByteArray();
                Bitmap bmp = BitmapFactory.decodeByteArray(compressedBitmap, 0,
                        compressedBitmap.length);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(),
                        imageView.getHeight(), false));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void addNewChat(String title, String description, String photoUrl) {
        ListChatModel listChatModel = new ListChatModel(title, photoUrl, false
                , false, description);
        db.collection("listChat").add(listChatModel).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //TODO
                        final DocumentReference dr = documentReference;
                        Map<String, Object> refToChat = new HashMap<>();
                        refToChat.put("LastMessage", "");
                        refToChat.put("LastMessageDate", Calendar.getInstance().getTime());
                        refToChat.put("refToChat", documentReference);
                        refToChat.put("countUnreadMessages", 0);
                        db.collection("user")
                                .document(SharedPrefUser.getInstance(ctx).getUser().getId())
                                .collection(
                                        "refToChat").document(dr.getId())
                                .set(refToChat)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        setReferenceToChat(dr.getId());
                                        makeToast("New Chat was created");
                                    }
                                });
                        Map<String, Object> toAdd = new HashMap<>();
                        DocumentReference drUser =
                                db.collection("user")
                                        .document(
                                                SharedPrefUser.getInstance(ctx).getUser().getId());
                        toAdd.put("refToUser", drUser);
                        dr.collection("users").add(toAdd).addOnSuccessListener(
                                new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        makeToast("dodano usera do chatu");
                                    }
                                });
                    }
                });
    }

    //TODO
    private void addChatToUser() {

    }

    private void setReferenceToChat(String referenceToChat) {
        addContactToChat.setReference(referenceToChat);
    }

    public void makeToast(String word) {
        Toast.makeText(ctx, word, Toast.LENGTH_SHORT).show();
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
}
