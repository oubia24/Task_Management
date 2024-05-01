package com.example.mytaskmanagement;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class SettingFragment extends Fragment {

    TextView logout;
    TextView userNameView;
    TextView EmailUser;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public SettingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View rootview = inflater.inflate(R.layout.fragment_setting, container, false);
        logout = rootview.findViewById(R.id.logout_setting);
        userNameView = rootview.findViewById(R.id.userName);
        EmailUser = rootview.findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent loginPage = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginPage);
            }
        });
        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth != null){
            EmailUser.setText(mAuth.getCurrentUser().getEmail());
            DocumentReference docRef = db.collection("User").document(mAuth.getCurrentUser().getEmail());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                String username;
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()){
                            username = document.getString("Username");

                            userNameView.setText(username != null? username:" ");
                        }else {
                            Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(getContext(), "Error fetching user data: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}