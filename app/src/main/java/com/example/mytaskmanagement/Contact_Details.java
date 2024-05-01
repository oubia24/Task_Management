package com.example.mytaskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Magnifier;
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

public class Contact_Details extends AppCompatActivity {

    ImageView detailImage;
    TextView detailUsername;
    TextView detailEmail;
    TextView detailTelephone;
    TextView detailDescription;
    ImageView returnHome;
    FloatingActionButton deleteButton;
    FloatingActionButton favorisButton;
    FloatingActionButton callphoneButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    static int PERMISSION_CODE= 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        detailImage = findViewById(R.id.detailImage);
        returnHome = findViewById(R.id.returnhome);
        detailUsername = findViewById(R.id.detailUsername);
        detailEmail = findViewById(R.id.detailEmail);
        detailTelephone = findViewById(R.id.detailTelephone);
        detailDescription = findViewById(R.id.detailDescription);
        deleteButton = findViewById(R.id.deleteButton);
        favorisButton = findViewById(R.id.favorirt);
        callphoneButton = findViewById(R.id.phone_call);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
            detailUsername.setText(bundle.getString("Username"));
            detailEmail.setText(bundle.getString("Email"));
            detailTelephone.setText(bundle.getString("Telephone"));
            detailDescription.setText(bundle.getString("Description"));
        }

        checkIsfavoris();
        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("User").document(mAuth.getCurrentUser().getEmail()).collection("Contacts")
                        .document(detailEmail.getText().toString()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Contact_Details.this,"Contact deleted",Toast.LENGTH_SHORT).show();
                                ListofContacts listofContactsFragment = (ListofContacts) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                if(listofContactsFragment != null){
                                    listofContactsFragment.refreshContacts();
                                }
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Contact_Details.this,e.getMessage()+"error to delete",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        favorisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favorisContact();
            }
        });


        if(ContextCompat.checkSelfPermission(Contact_Details.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Contact_Details.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);

        }

        callphoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = detailTelephone.getText().toString();
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(i);
            }
        });
    }

    private void checkIsfavoris() {
        String uploadEmail = detailEmail.getText().toString();
        db.collection("User").document(mAuth.getCurrentUser().getEmail()).collection("Contacts")
                .document(uploadEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Boolean isFavoris = documentSnapshot.getBoolean("isFavoris");
                        if (isFavoris != null && isFavoris){
                            favorisButton.setImageResource(R.drawable.round_favorite_24);
                        }else {
                            favorisButton.setImageResource(R.drawable.round_favorite_border_24);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Contact_Details.this,"something went wrong"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void favorisContact() {
        String uploadEmail = detailEmail.getText().toString();
        db.collection("User").document(mAuth.getCurrentUser().getEmail()).collection("Contacts")
                .document(uploadEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Boolean isFavoris = documentSnapshot.getBoolean("isFavoris");
                        db.collection("User").document(mAuth.getCurrentUser().getEmail()).collection("Contacts")
                                .document(uploadEmail)
                                .update("isFavoris",!isFavoris)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!isFavoris){
                                            Toast.makeText(Contact_Details.this,"contact favoris",Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(Contact_Details.this,"unfavoris the contact",Toast.LENGTH_SHORT).show();
                                        }
                                        Intent intent = getIntent();
                                        finish();
                                        overridePendingTransition(0,0);
                                        startActivity(intent);
                                        overridePendingTransition(0,0);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Contact_Details.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }
}