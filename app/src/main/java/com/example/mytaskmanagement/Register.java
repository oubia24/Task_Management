package com.example.mytaskmanagement;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText Regusername;
    private EditText RegTel;
    private EditText RegEmail;
    private EditText Regpasswrd;
    private Button btnReg;
    private TextView textLogin;
    private FirebaseAuth mAuth;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Regusername = (EditText) findViewById(R.id.username);
        RegTel = (EditText) findViewById(R.id.phone);
        RegEmail = (EditText) findViewById(R.id.email);
        Regpasswrd = (EditText) findViewById(R.id.password);
        btnReg = (Button) findViewById(R.id.buttonRegisterId);
        textLogin = (TextView) findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(Register.this,MyTasks.class));
            finish();
        }
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = RegEmail.getText().toString().trim();
                String password = Regpasswrd.getText().toString().trim();
                String phone = RegTel.getText().toString().trim();
                String username = Regusername.getText().toString().trim();

                if (TextUtils.isEmpty(username)){
                    Regusername.setError("please Enter the username");
                    return;
                }
                if (TextUtils.isEmpty(phone)){
                    RegTel.setError("please Enter the phone");
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    RegEmail.setError("please Enter the email");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Regpasswrd.setError("Please Enter the passwrd");
                    return;
                }
                if (password.length() < 6){
                    Regpasswrd.setError("the password should be at lease 6 chractrs");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "User created successfully", Toast.LENGTH_LONG).show();
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("Username",username);
                                    user.put("Telephone",phone);
                                    user.put("Email",email);
                                    db.collection("User").document(mAuth.getCurrentUser().getEmail()).set(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(Register.this, "saved successfully", Toast.LENGTH_LONG).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG,"On Failure to save on firestore"+e.getMessage().toString());
                                                    Toast.makeText(Register.this, "Note save au firestore"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });

                                    startActivity(new Intent(Register.this,MyTasks.class));


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed."+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,LoginActivity.class));
            }
        });
    }
}