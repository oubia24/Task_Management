package com.example.mytaskmanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddContact extends Fragment {

    ImageView imageAdded;
    EditText userName;
    EditText email;
    EditText telephone;
    EditText description;
    String imageUrl;
    Uri uri;
    Button addContact;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public AddContact() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_add_contact, container, false);
        imageAdded = rootview.findViewById(R.id.uploadImage);
        userName = rootview.findViewById(R.id.name);
        email = rootview.findViewById(R.id.email);
        telephone = rootview.findViewById(R.id.telephone);
        description = rootview.findViewById(R.id.description);
        addContact = rootview.findViewById(R.id.addButton);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            imageAdded.setImageURI(uri);
                        }else {
                            Toast.makeText(getContext(),"No image selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imageAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addConatact();
            }
        });

        return  rootview;
    }

    private void addConatact() {
        String addedUsername = userName.getText().toString();
        String addedEmail = email.getText().toString();
        String addedTelephone = telephone.getText().toString();
        String addedDescription = description.getText().toString();

        if (TextUtils.isEmpty(addedUsername) || TextUtils.isEmpty(addedEmail) || TextUtils.isEmpty(addedTelephone) || TextUtils.isEmpty(addedDescription)){
            Toast.makeText(getActivity(),"Please fill all the required fields and select an image",Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImage();
    }

    private void uploadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Contact Image").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Failed to add task", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadData() {
        String uploadUsername = userName.getText().toString();
        String uploadEmail = email.getText().toString();
        String uploadTelephone = telephone.getText().toString();
        String uploadDescription = description.getText().toString();

        HashMap<String,Object> user = new HashMap<>();

        user.put("userName",uploadUsername);
        user.put("Email",uploadEmail);
        user.put("Telephone",uploadTelephone);
        user.put("Description",uploadDescription);
        user.put("Image",imageUrl);
        user.put("owner",mAuth.getCurrentUser().getEmail());
        user.put("isFavoris",false);

        db.collection("User").document(mAuth.getCurrentUser().getEmail()).collection("Contacts").document(uploadEmail)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),"contact added successfully",Toast.LENGTH_SHORT).show();
                            Fragment contctFragment = new ListofContacts();
                            FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                            fm.replace(R.id.fragment_container,contctFragment).commit();
                        }
                      }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"failed to add the contact"+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }
}