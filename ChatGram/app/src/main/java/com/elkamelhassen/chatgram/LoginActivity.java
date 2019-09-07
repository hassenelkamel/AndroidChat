package com.elkamelhassen.chatgram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by enigma on 28/03/2018.
 */


public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPass;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button btnLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail=findViewById(R.id.edit_email_login);
        loginPass=findViewById(R.id.edit_pwd_login);
        btnLogin=findViewById(R.id.btn_login_login);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, pass;
                email = loginEmail.getText().toString().trim();
                pass = loginPass.getText().toString().trim();
                if (!TextUtils.isEmpty(email) &&!TextUtils.isEmpty(pass)) {
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                checkUserExists();
                            }
                        }
                    });
                }

            }
            private void checkUserExists() {
                final String user_id=mAuth.getCurrentUser().getUid();
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id))
                        {
                            Intent loginIntent=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(loginIntent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }


}
