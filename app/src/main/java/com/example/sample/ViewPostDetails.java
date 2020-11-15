package com.example.sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewPostDetails extends AppCompatActivity {
EditText e1,e2,e3,e4,e5,e13,e14;
TextView t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_details);
        String postid=getIntent().getStringExtra("postid");
        String userid=getIntent().getStringExtra("userid");
        e1=findViewById(R.id.editText5);
        e2=findViewById(R.id.editText7);
        e3=findViewById(R.id.editText8);
        e4=findViewById(R.id.editText9);
        e5=findViewById(R.id.editText10);
        e13=findViewById(R.id.editText13);
        e14 =findViewById(R.id.editText14);
        t1=findViewById(R.id.textView6);
        final ProgressDialog po = new ProgressDialog(this);
        po.setMessage("Loading");
        po.show();
        FirebaseDatabase.getInstance().getReference().child("my_users").child(userid).child("Updated_Post").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t1.setText(dataSnapshot.child("Address").getValue().toString());
                e5.setText(dataSnapshot.child("Allow").getValue().toString());
            e1.setText(dataSnapshot.child("CompanyName").getValue().toString());
               e2.setText( dataSnapshot.child("Date").getValue().toString());
                e3.setText(dataSnapshot.child("Time").getValue().toString());
                e4.setText(dataSnapshot.child("Des").getValue().toString());
                e13.setText(dataSnapshot.child("compintime").getValue().toString());
                e14.setText(dataSnapshot.child("compouttime").getValue().toString());

                po.dismiss();
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

}
