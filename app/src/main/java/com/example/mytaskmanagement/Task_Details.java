package com.example.mytaskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Task_Details extends AppCompatActivity {

    ImageView detailImage;
    TextView detailTitle;
    TextView detailDescrip;
    TextView detailDeadline;
    FloatingActionButton deleteButton,editButton,doneButton;
    String key = "";
    String imageUrl = "";
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailDescrip = findViewById(R.id.detailDesc);
        detailDeadline = findViewById(R.id.detailDeadline);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        doneButton = findViewById(R.id.doneButton);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
            detailTitle.setText(bundle.getString("title"));
            detailDescrip.setText(bundle.getString("description"));
            detailDeadline.setText(bundle.getString("deadline"));
            imageUrl = bundle.getString("Image");
        }
        checkIsdone();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("User").document(mAuth.getCurrentUser().getEmail()).collection("Tasks").document(detailTitle.getText().toString()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Task_Details.this,"task deleted",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Task_Details.this,MyTasks.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Task_Details.this, "Error deleting contact", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Task_Details.this,UpdateteTask.class);
                intent.putExtra("Etitle",detailTitle.getText().toString());
                intent.putExtra("Edescription",detailDescrip.getText().toString());
                intent.putExtra("Edealine",detailDeadline.getText().toString());
                intent.putExtra("Eimage",imageUrl);
                startActivity(intent);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskdone();
            }
        });
    }

    private void checkIsdone() {
        String uploadTitle = detailTitle.getText().toString();
        db.collection("User").document(mAuth.getCurrentUser().getEmail()).collection("Tasks").document(uploadTitle)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Boolean isDone = documentSnapshot.getBoolean("done");
                        if (isDone != null && isDone){
                            doneButton.setImageResource(R.drawable.baseline_done_all_24 );
                        }else {
                            doneButton.setImageResource(R.drawable.baseline_done_24);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Task_Details.this, "Probleme", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void taskdone() {
        String uploadTitle = detailTitle.getText().toString();
        db.collection("User").document(mAuth.getCurrentUser().getEmail()).collection("Tasks").document(uploadTitle)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Boolean isDone = documentSnapshot.getBoolean("done");
                            db.collection("User").document(mAuth.getCurrentUser().getEmail()).collection("Tasks").document(uploadTitle)
                                    .update("done",!isDone)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                if (!isDone){
                                                    Toast.makeText(Task_Details.this,"Task done",Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(Task_Details.this,"undone the task",Toast.LENGTH_SHORT).show();
                                                }
                                                Intent intent = getIntent();
                                                finish();
                                                overridePendingTransition(0,0);
                                                startActivity(intent);
                                                overridePendingTransition(0,0);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Task_Details.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                    }
                });
    }


}