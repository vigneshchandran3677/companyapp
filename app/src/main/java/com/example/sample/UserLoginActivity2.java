package com.example.sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class UserLoginActivity2 extends AppCompatActivity {
EditText e1,e2;
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login2);
        e1=findViewById(R.id.Loginusername);
        e2=findViewById(R.id.loginuserphone);
        mAuth=FirebaseAuth.getInstance();
    }

    public void Nextbtn(View view) {
        FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(e1.getText().toString()).build();
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserLoginActivity2.this, "User profile updated.",Toast.LENGTH_LONG).show();
                }
            }
        });
        FirebaseDatabase.getInstance().getReference().child("my_users").child(user.getUid()).child("username").setValue(e1.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("my_users").child(user.getUid()).child("phonenumber").setValue(e2.getText().toString());
        Intent indent=new Intent(UserLoginActivity2.this,UserScreenActivity.class);
        startActivity(indent);
    }
}
