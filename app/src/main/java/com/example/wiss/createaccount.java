package com.example.wiss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class createaccount extends AppCompatActivity {
    Button mRegister;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    TextView loginBtn;
    EditText mName,mEmail,mPhone,mAge;
    EditText mPassword;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPassword= findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        mAge = findViewById(R.id.age);
        loginBtn = findViewById(R.id.loginBtn);
        mRegister= findViewById(R.id.register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),login.class));
            finish();
        }



        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email= mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String name = mName.getText().toString().trim();
                final String phone =mPhone.getText().toString().trim();
                final int age = Integer.parseInt(mAge.getText().toString());



                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required!");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required!");
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError("Password length must be greater than 6");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //registering

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser vUser = fAuth.getCurrentUser();
                            ((FirebaseUser) vUser).sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(createaccount.this, "Verification Email has been sent, Check mail", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(createaccount.this, "Failure!!", Toast.LENGTH_SHORT).show();
                                }
                            });


                            Toast.makeText(createaccount.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userId = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("usersDetails").document(userId);
                            Map<String,Object> user = new HashMap<>();
                            user.put("name",name);
                            user.put("email",email);
                            user.put("phone",phone);
                            user.put("age",age);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(createaccount.this, "Details Inserted.", Toast.LENGTH_LONG).show();

                                }
                            });
                            startActivity(new Intent(getApplicationContext(),login.class));
                        }else{
                            Toast.makeText(createaccount.this, "Error!!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                });


            }
        });

    }

    public void login(View view){
        startActivity(new Intent(getApplicationContext(),login.class));
    }
}

