package com.example.mytaskmanagement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileUser extends Fragment {

    TextView userNameView,emailView,telephoneView;
    TextView nameTextview,occupationTextview,workplaceTextview;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    public ProfileUser() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_profile_user, container, false);
        userNameView = rootview.findViewById(R.id.user_textview);
        emailView = rootview.findViewById(R.id.email_textview);
        telephoneView = rootview.findViewById(R.id.phone_textview);
        nameTextview = rootview.findViewById(R.id.name_textview);
        occupationTextview = rootview.findViewById(R.id.occupation_textview);
        workplaceTextview = rootview.findViewById(R.id.workplace_textview);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        DocumentReference docRef = db.collection("User").document(mAuth.getCurrentUser().getEmail());
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    String userName,email,telephone;
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                userName = document.getString("Username");
                                email = document.getString("Email");
                                telephone = document.getString("Telephone");

                                userNameView.setText(userName != null?userName:"");
                                emailView.setText(email != null?email:"");
                                telephoneView.setText(telephone != null?telephone:"");
                                nameTextview.setText(userName != null?userName:"");
                                occupationTextview.setText(userName != null?userName:"");
                                workplaceTextview.setText(email != null?email:"");
                            }else {
                                Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getContext(), "Error fetching user data: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return rootview;
    }
}