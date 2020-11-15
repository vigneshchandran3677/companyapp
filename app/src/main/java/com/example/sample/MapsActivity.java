package com.example.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
ArrayList<String> name,date;
ArrayList<LatLng> latLngs;
    private GoogleMap mMap;
    int j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        j=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        name=new ArrayList<>();
        date=new ArrayList<>();
        latLngs=new ArrayList<>();
        final ProgressDialog po = new ProgressDialog(this);
        po.setMessage("Signing In " );
        po.show();

        FirebaseDatabase.getInstance().getReference().child("my_users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.v("uvan","ghghghg");
                String currentlocationlatitude=dataSnapshot.child("currentlocationlatitude").getValue().toString();
                String currentlocationlongitude=dataSnapshot.child("currentlocationlongitude").getValue().toString();
                String currentlocationtime=dataSnapshot.child("currentlocationtime").getValue().toString();
                String usern=dataSnapshot.child("username").getValue().toString();
                name.add(usern);
                date.add(currentlocationtime);
                LatLng nnn = new LatLng(Double.parseDouble(currentlocationlatitude),Double.parseDouble(currentlocationlongitude));
                latLngs.add(nnn);
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
        po.dismiss();
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
        Log.v("Uvan name lenght",""+Integer.toString(name.size()));
        Log.v("Uvan mapready count",""+Integer.toString(j++));
        Toast.makeText(this,"This shows the current locaiton of all the users. Please zoom and see",Toast.LENGTH_LONG).show();


       Log.v("uvan","start");
        mMap = googleMap;
        for(int i=0;i<name.size();i++)
        {Log.v("Uvan",""+i);
            Log.v("uvan","populate");
            Log.v("uvan",latLngs.get(i).toString());
            Log.v("uvan",name.get(i));Log.v("uvan",date.get(i));

            mMap.addMarker(new MarkerOptions().position(latLngs.get(i)).title(name.get(i)).snippet("Updated on : "+date.get(i))).showInfoWindow();

        }
        LatLng Mogappair = new LatLng(13.0771288, 80.1807061);
       mMap.addMarker(new MarkerOptions().position(Mogappair).title("Marker in Mogappair"));
        //mMap.addMarker(new MarkerOptions().position(new LatLng(15.0771288, 80.1807061)).title("Marker in Mogappair").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
       //
        //
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(Mogappair));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(Mogappair));
        Log.v("uvan","end");
    }
}
