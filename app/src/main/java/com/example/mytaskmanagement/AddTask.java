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

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class AddTask extends AppCompatActivity {

    EditText editTitle;
    EditText editDescription;
    EditText editDeadline;
    ImageView imageTask;
    AppCompatButton addButton;
    Uri uri;
    String imageUri;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editDeadline = findViewById(R.id.editDeadline);
        imageTask = findViewById(R.id.imageTask);
        addButton = findViewById(R.id.addButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            imageTask.setImageURI(uri);
                        } else {
                            Toast.makeText(AddTask.this, "No Image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imageTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    private void saveData() {
        String uploadTitle = editTitle.getText().toString();
        String uploadDesc = editDescription.getText().toString();
        String uploadDeadline = editDeadline.getText().toString();

        if (TextUtils.isEmpty(uploadTitle) || TextUtils.isEmpty(uploadDesc) || TextUtils.isEmpty(uploadDeadline) || uri == null) {
            Toast.makeText(AddTask.this, "Please fill all the required fields and select an image", Toast.LENGTH_LONG).show();
            return;
        }

        uploadImage();
    }

    private void uploadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Task Image").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTask.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete());
            Uri urlImage = uriTask.getResult();
            imageUri = urlImage.toString();
            uploadData();
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(AddTask.this, "Failed to add task", Toast.LENGTH_LONG).show();
        });
    }

    private void uploadData() {
        String uploadTitle = editTitle.getText().toString();
        String uploadDesc = editDescription.getText().toString();
        String uploadDeadline = editDeadline.getText().toString();

        HashMap<String, Object> user = new HashMap<>();
        user.put("title", uploadTitle);
        user.put("description", uploadDesc);
        user.put("deadline", uploadDeadline);
        user.put("image", imageUri);
        user.put("owner",mAuth.getCurrentUser().getEmail());
        user.put("done",false);

        db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Tasks").document(uploadTitle)
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddTask.this, "Task added", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddTask.this, MyTasks.class);
                        startActivity(intent); // Navigate back to the home page
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddTask.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
