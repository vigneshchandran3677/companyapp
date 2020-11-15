package com.example.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ViewFriendsActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> arrayList,postid;
    ArrayAdapter arrayAdapter;
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    String idd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);
        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        listView=findViewById(R.id.frndlistview);
        arrayList=new ArrayList<>();
        postid=new ArrayList<>();
        Intent ind=getIntent();
        idd=ind.getStringExtra("userid");
        final ProgressDialog po = new ProgressDialog(this);
        po.setMessage("Loading" );
        po.show();
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in=new Intent(ViewFriendsActivity.this,ViewFriendsDetails.class);
                Log.d("uvann id",idd);
                Log.d("uvann post",postid.get(position));
                in.putExtra("postid",postid.get(position));
                in.putExtra("userid",idd);
                startActivity(in);
            }
        });
        listView.setAdapter(arrayAdapter);
        firebaseDatabase.getReference().child("my_users").child(idd).child("Friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    String usern = dataSnapshot.child("Name").getValue().toString();
                    String usertime = dataSnapshot.child("Meeting time").getValue().toString();
                    String userdate = dataSnapshot.child("Meeting date").getValue().toString();

                    String userd = dataSnapshot.getKey();
if(usern.compareTo(mAuth.getCurrentUser().toString())!=0) {
    postid.add(usern);
    arrayList.add(usern + " Date " + userdate + " Time " + usertime);
    arrayAdapter.notifyDataSetChanged();
}                    po.dismiss();
                }
                catch(Exception e)
                {}
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

        po.dismiss();

    }

}