package com.example.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {
    ArrayList<String> time,date;
    ArrayList<LatLng> latLngs;
    private GoogleMap mMap;
    int j;
    int i;
    String idd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        j=0;
        Intent ind=getIntent();
        idd=ind.getStringExtra("userid");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        time=new ArrayList<>();
        date=new ArrayList<>();
        latLngs=new ArrayList<>();
i=0;
        final ProgressDialog po = new ProgressDialog(this);
        po.setMessage("Signing In " );
        po.show();
        FirebaseDatabase.getInstance().getReference().child("my_users").child(idd).child("LocationList").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                i++;
                Log.v("uvan","ghghghg");
            //    for (DataSnapshot postSnapshot: dataSnapshot.child("LocationList").getChildren()) {
                    String currentlocationdate= dataSnapshot.child("Date").getValue().toString();
                    String currentlocationlatitude=  dataSnapshot.child("Lat").getValue().toString();
                    String currentlocationlongitude=    dataSnapshot.child("Long").getValue().toString();
                    String currentlocationtime= dataSnapshot.child("Time").getValue().toString();
                    time.add(currentlocationtime);
                    date.add(currentlocationtime);
                    LatLng nnn = new LatLng(Double.parseDouble(currentlocationlatitude),Double.parseDouble(currentlocationlongitude));
                    latLngs.add(nnn);
          //  }



                //populate(nnn,usern,currentlocationtime);
                String userd=dataSnapshot.getKey();
                reloadMap();
                //   .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon))
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
        po.dismiss();;
    }
    public void populate(LatLng nnn,String nme ,String bbb){
        Log.v("uvan","populate");
        Log.v("uvan",nnn.toString());
        Log.v("uvan",nme);Log.v("uvan",bbb);
        mMap.addMarker(new MarkerOptions().position(nnn).title(nme).snippet("Updated on : "+bbb));
        reloadMap();

    }

    /*
     *
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void reloadMap(){
        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this,"This shows all the places traveled the User. Please zoom and see",Toast.LENGTH_LONG).show();
        if(i==0)
            Toast.makeText(this,"No Travel History",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,Integer.toString(i)+" Travel History,Please zooms and see",Toast.LENGTH_LONG).show();

        Log.v("Uvan name lenght",""+Integer.toString(time.size()));
        Log.v("Uvan mapready count",""+Integer.toString(j++));


        Log.v("uvan","start");
        mMap = googleMap;
        for(int i=0;i<time.size();i++)
        {Log.v("Uvan",""+i);
            Log.v("uvan","populate");
            Log.v("uvan",latLngs.get(i).toString());
            Log.v("uvan",time.get(i));Log.v("uvan",date.get(i));

            mMap.addMarker(new MarkerOptions().position(latLngs.get(i)).title(Integer.toString(i+1)+"").snippet("Time"+time.get(i)+"Date : "+date.get(i))).showInfoWindow();
        }
        if(latLngs.size()!=0)
        {
        LatLng Mogappair = new LatLng(latLngs.get(0).latitude,latLngs.get(0).longitude);
       //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0).latitude,latLngs.get(0).longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Mogappair));
        mMap.setMinZoomPreference(4);
        Log.v("uvan","end");
    }}
}
