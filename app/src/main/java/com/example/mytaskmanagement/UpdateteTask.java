package com.example.mytaskmanagement;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateteTask extends AppCompatActivity {

    ImageView updateImage;
    EditText updateTitle;
    EditText updateDescription;
    EditText updateDeadline;
    AppCompatButton updateButton;
    Uri uri;
    String imageUrl;
    String oldImageUrl;
    FirebaseFirestore db;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatete_task);

        updateImage = findViewById(R.id.updateImage);
        updateTitle = findViewById(R.id.updateTitle);
        updateDescription = findViewById(R.id.updateDescription);
        updateDeadline = findViewById(R.id.updateDeadline);
        updateButton = findViewById(R.id.updateButton);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        }else {
                            Toast.makeText(UpdateteTask.this,"No image Selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Glide.with(UpdateteTask.this).load(bundle.getString("Eimage")).into(updateImage);
            updateTitle.setText(bundle.getString("Etitle"));
            updateDescription.setText(bundle.getString("Edescription"));
            updateDeadline.setText(bundle.getString("Edealine"));
            oldImageUrl = bundle.getString("Eimage");
        }

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });
    }

    private void updateData() {
        String editTitle = updateTitle.getText().toString();
        String editDescription = updateDescription.getText().toString();
        String editDeadline = updateDeadline.getText().toString();

        if(TextUtils.isEmpty(editTitle) || TextUtils.isEmpty(editDescription) || TextUtils.isEmpty(editDeadline)){
            Toast.makeText(this,"please fill all the required fields",Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImage();

    }

    private void uploadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Task Image").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateteTask.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri UrlImage = uriTask.getResult();
                imageUrl = UrlImage.toString();
                uploadUpdateData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(UpdateteTask.this, "Failed to add task", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadUpdateData() {

        String editTitle = updateTitle.getText().toString();
        String editDescription = updateDescription.getText().toString();
        String editDeadline = updateDeadline.getText().toString();

        db.collection("User").document(mAuth.getCurrentUser().getEmail()).collection("Tasks").document(updateTitle.getText().toString())
                .update("title",editTitle,"description",editDescription,"deadline",editDeadline)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UpdateteTask.this, "Updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UpdateteTask.this,MyTasks.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateteTask.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}