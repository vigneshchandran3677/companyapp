package com.example.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ViewUserActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> arrayList,postid;
    ArrayAdapter arrayAdapter;
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    String idd;
    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        b1=(Button)findViewById(R.id.button8);
        mAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        listView=findViewById(R.id.List);
        Toast.makeText(this,"This map shows the current location of all the users.Zoom and see the locations",Toast.LENGTH_LONG);
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
                Intent in=new Intent(ViewUserActivity.this,ViewPostDetails.class);
                Log.d("uvann id",idd);
                Log.d("uvann post",postid.get(position));
                in.putExtra("postid",postid.get(position));
                in.putExtra("userid",idd);
                startActivity(in);
            }
        });
        listView.setAdapter(arrayAdapter);
        firebaseDatabase.getReference().child("my_users").child(idd).child("Updated_Post").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String usern=dataSnapshot.child("CompanyName").getValue().toString();
                String userdate=dataSnapshot.child("Date").getValue().toString();

                String userd=dataSnapshot.getKey();

                postid.add(userd);
                arrayList.add(usern+" Date "+userdate);
                arrayAdapter.notifyDataSetChanged();
                po.dismiss();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);

        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.logoutadmin)
        {  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ViewUserActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent in=new Intent(ViewUserActivity.this,AdminLoginActivity.class);
            startActivity(in);
        }
else if(item.getItemId()==R.id.Itemmap)
        {
            Intent intent=new Intent(ViewUserActivity.this,MapsActivity2.class);
            intent.putExtra("userid",idd);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void frndbtn(View view) {
        Intent in=new Intent(ViewUserActivity.this,ViewFriendsActivity.class);
        Log.d("uvann id",idd);
        in.putExtra("userid",idd);
        startActivity(in);
    }
}
