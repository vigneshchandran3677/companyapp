package com.example.sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class UserLoginActivity2 extends AppCompatActivity {
EditText e1,e2;
    int i,j;
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
        if (e1.getText().toString().equals("") || e2.getText().toString().equals("") ) {
            Toast.makeText(UserLoginActivity2.this, "Fill all the columns", Toast.LENGTH_SHORT).show();
        } else{
        final FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(e1.getText().toString()).build();
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserLoginActivity2.this, "User profile updated.",Toast.LENGTH_LONG).show();
                }
            }
        }); i=0;j=0;
        FirebaseDatabase.getInstance().getReference().child("my_users").child(user.getUid()).child("username").setValue(e1.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference().child("my_users").child(user.getUid()).child("currentlocationlatitude").setValue("13.077");
                FirebaseDatabase.getInstance().getReference().child("my_users").child(user.getUid()).child("currentlocationlongitude").setValue("80.180");
                FirebaseDatabase.getInstance().getReference().child("my_users").child(user.getUid()).child("currentlocationtime").setValue("0.0");
                FirebaseDatabase.getInstance().getReference().child("my_users").child(user.getUid()).child("phonenumber").setValue(e2.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Intent indent=new Intent(UserLoginActivity2.this,UserScreenActivity.class);
                        startActivity(indent);
                    }
                });
            }
        });


    }}
    public void layoutclicked(View v)
    {try {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    }
}
