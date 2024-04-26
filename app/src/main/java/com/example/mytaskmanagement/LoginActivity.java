package com.example.mytaskmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText LogEmail;
    private EditText Logpassword;
    private Button btnLogin;
    private TextView textSignUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LogEmail = (EditText) findViewById(R.id.email_login);
        Logpassword = (EditText) findViewById(R.id.password_login);
        btnLogin = (Button) findViewById(R.id.buttonLoginId);
        textSignUp = (TextView) findViewById(R.id.signUp);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = LogEmail.getText().toString().trim();
                String password = Logpassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    LogEmail.setError("please Enter the email");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Logpassword.setError("please Enter the password");
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Authentication Suceess.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this,MyTasks.class));
                                    finish();
                                } else {

                                    Toast.makeText(LoginActivity.this, "Authentication failed."+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, Register.class));
            }
        });
    }
}