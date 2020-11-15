package com.example.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminLoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText e1,e2;
    ConstraintLayout l1;
    ArrayList<String>user,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        e1=findViewById(R.id.adminemailid);
        e2=findViewById(R.id.adminpassword);
        mAuth = FirebaseAuth.getInstance();

        user = new ArrayList<>();
        pass = new ArrayList<>();



        FirebaseDatabase.getInstance().getReference().child("admin").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("uvan","onuuuuu");
                user.add(dataSnapshot.child("username").getValue().toString());
                //pass.add("0");
                 pass.add(dataSnapshot.child("pass").getValue().toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
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

    public void loginbtn(View view) {if (e1.getText().toString().equals("") || e2.getText().toString().equals("") ) {
        Toast.makeText(AdminLoginActivity.this, "Fill all the columns", Toast.LENGTH_SHORT).show();
    } else {


        boolean a = false;
        Log.d("uvan user",Integer.toString(user.size()));
        Log.d("uvan pass",Integer.toString(pass.size()));
        for (int i = 0; i < user.size(); i++) {
            Log.d("uvan user",user.get(i)+" "+e1.getText().toString());
            Log.d("uvan user",pass.get(i)+" "+e2.getText().toString());
            if (e1.getText().toString().equals(user.get(i)) && e2.getText().toString().equals(pass.get(i))) {
                a = true;
                break;
            }
        }
if(a)
        {    mAuth.signInWithEmailAndPassword(e1.getText().toString(), e2.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String Username = e1.getText().toString();
                            String Password = e2.getText().toString();
                            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(AdminLoginActivity.this);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("Registered", true);
                            editor.putString("Username", Username);
                            editor.putString("Password", Password);
                            editor.apply();
                            Intent intent = new Intent(AdminLoginActivity.this, AdminScreenActivity.class);
                            startActivity(intent);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e != null)
                    Toast.makeText(AdminLoginActivity.this, e.getMessage() + e.getCause(), Toast.LENGTH_LONG).show();
            }
        });
    }
else
{
    Toast.makeText(AdminLoginActivity.this, "Email or Password is wrong", Toast.LENGTH_LONG).show();
}
    }
    }
}
