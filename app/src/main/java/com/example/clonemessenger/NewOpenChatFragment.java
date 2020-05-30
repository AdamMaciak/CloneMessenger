package com.example.clonemessenger;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clonemessenger.Adapters.ChatAdapter;
import com.example.clonemessenger.Models.ChatModel;
import com.example.clonemessenger.ViewModels.ListChatViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NewOpenChatFragment extends Fragment {

    ListChatFragment listChatFragment;
    List<ChatModel> chat = new ArrayList<>();
    RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ImageButton btn_sendMessage, btn_getFromGallery, btn_takePhoto;
    private EditText et_message;
    private FirebaseUser fUser;
    private int GALLERY_REQUEST_CODE = 1, CAMERA_REQUEST_CODE = 2;
    private String userId;
    FirebaseFirestore db;
    String filename;
    Uri filePath;
    boolean selected_image = false;
    boolean take_photo = false;
    private Parcelable recyclerViewState;
    LinearLayoutManager linearLayoutManager;
    FirebaseStorage storage;

    private ListChatViewModel listChatViewModel;

    private String cameraFilePath = "";

    public void setListChatViewModel(
            ListChatViewModel listChatViewModel) {
        this.listChatViewModel = new ListChatViewModel();
        this.listChatViewModel.setIdChat(listChatViewModel.getIdChat());
        this.listChatViewModel.setImageChatPath(listChatViewModel.getImageChatPath());
        this.listChatViewModel.setLastMessage(listChatViewModel.getLastMessage());
        this.listChatViewModel.setTitle(listChatViewModel.getTitle());
        this.listChatViewModel.setLastMessageDate(listChatViewModel.getLastMessageDate());
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location
        // of Camera photos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!cameraFilePath.equals("")) {
            File file = new File(cameraFilePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void captureFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chat.clear();
        View root = inflater.inflate(R.layout.fragment_open_chat, container, false);
        recyclerView = root.findViewById(R.id.rvChat);
        recyclerView.setHasFixedSize(true);

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
//        System.out.println(account.getPhotoUrl());
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        listChatFragment = new ListChatFragment();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, listChatFragment)
                        .commit();
                //TODO temporary solution
                MainActivity.getBottomBar().setVisibility(View.VISIBLE);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        btn_sendMessage = root.findViewById(R.id.btn_sendMessage);
        btn_getFromGallery = root.findViewById(R.id.btn_getFromGallery);
        btn_takePhoto = root.findViewById(R.id.btn_takePhoto);
        et_message = root.findViewById(R.id.et_message);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = fUser.getUid();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        getDataFromFirestore();

        btn_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = et_message.getText().toString();
                boolean photo = false;
                if (take_photo == true || selected_image == true) {
                    photo = true;
                }
                if (!message.equals("") || photo == true) {
                    chat.clear();
                    final Date currentTime = Calendar.getInstance().getTime();
                    if (!message.equals("") && photo == false) {
                        ChatModel chatModel = new ChatModel(userId, "",
                                message, "", currentTime);
                        db.collection("listChat")
                                .document(
                                        listChatViewModel.getIdChat())
                                .collection("messages")
                                .add(chatModel);
                        et_message.setText("");
                    } else if (!message.equals("") && photo == true) {
                        StorageReference riversRef = storage.getReference()
                                .child("images/" + filename);
                        final String filename1 = filename;
                        System.out.println(filePath);
                        UploadTask uploadTask = riversRef.putFile(filePath);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Uploaded photo", Toast.LENGTH_SHORT);
                                System.out.println(
                                        "------------------------------FILENAME" + filename1);
                                ChatModel chatModel = new ChatModel(userId,
                                        "", message, filename1,
                                        currentTime);
                                db.collection("listChat")
                                        .document(listChatViewModel.getIdChat())
                                        .collection("messages")
                                        .add(chatModel);
                                if (!cameraFilePath.equals("")) {
                                    File file = new File(cameraFilePath);
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                }
                            }
                        });
                        et_message.setText("");
                        filename = "";
                        filePath = null;
                        selected_image = false;
                        take_photo = false;
                        btn_takePhoto.setImageResource(R.drawable.ic_photo_camera);
                        btn_getFromGallery.setImageResource(R.drawable.ic_insert_photo);
                        btn_takePhoto.setVisibility(View.VISIBLE);
                        btn_getFromGallery.setVisibility(View.VISIBLE);
                    } else if (message.equals("") && photo == true) {
                        StorageReference riversRef = storage.getReference()
                                .child("images/" + filename);
                        final String filename1 = filename;
                        System.out.println(filePath);
                        UploadTask uploadTask = riversRef.putFile(filePath);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Uploaded photo", Toast.LENGTH_SHORT);
                                System.out.println(
                                        "------------------------------FILENAME" + filename1);
                                ChatModel chatModel = new ChatModel(userId,
                                        "", "", filename1, currentTime);
                                db.collection("listChat")
                                        .document(
                                                listChatViewModel.getIdChat())
                                        .collection("messages")
                                        .add(chatModel);
                                if (!cameraFilePath.equals("")) {
                                    File file = new File(cameraFilePath);
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                }
                            }
                        });
                        et_message.setText("");
                        filename = "";
                        filePath = null;
                        selected_image = false;
                        take_photo = false;
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
                if (selected_image == false) {
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
                } else {
                    selected_image = false;
                    filename = "";
                    filePath = null;
                    btn_getFromGallery.setImageResource(R.drawable.ic_insert_photo);
                    btn_takePhoto.setVisibility(View.VISIBLE);
                    btn_getFromGallery.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureFromCamera();
            }
        });

        return root;
    }

    private void getDataFromFirestore() {
        db.collection("listChat")
                .document(listChatViewModel.getIdChat())
                .collection("messages")
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
                        for (ChatModel ch : chat) {
                            System.out.println(ch.getMessage() + "  " + ch.getSender());
                        }
                        //System.out.println(account.getPhotoUrl());
//                        mAdapter = new ChatAdapter(getContext(), chat, account.getPhotoUrl());
                        mAdapter = new ChatAdapter(getContext(), chat, null);
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.notifyItemInserted(chat.size() - 1);
                        linearLayoutManager.scrollToPosition(chat.size() - 1);
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                filePath = Uri.fromFile(new File(getRealPathFromURI(selectedImage)));
                filename = filePath.getLastPathSegment();
                btn_getFromGallery.setImageResource(R.drawable.ic_cancel);
                btn_takePhoto.setVisibility(View.INVISIBLE);
                selected_image = true;
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                btn_takePhoto.setImageResource(R.drawable.ic_cancel);
                btn_getFromGallery.setVisibility(View.INVISIBLE);
                filePath = Uri.fromFile(new File(getRealPathFromURI(Uri.parse(cameraFilePath))));
                filename = filePath.getLastPathSegment();
                take_photo = true;
            }
        }
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
