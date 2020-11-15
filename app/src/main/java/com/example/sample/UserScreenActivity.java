package com.example.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserScreenActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {
    GoogleApiClient googleApiClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView t1;
    EditText e1,e2,e3,e4,e5,e11,e12;
    PendingIntent inp;
    Geocoder geocoder=null;
    LocationRequest locationRequest;
    String addaftergeocoding,lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        addaftergeocoding="";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_screen);
        t1=findViewById(R.id.textView);
        e1=findViewById(R.id.editText);
        e2=findViewById(R.id.editText2);
        e3=findViewById(R.id.editText3);
        e4=findViewById(R.id.editText4);
        e5=findViewById(R.id.editText6);
        e11=findViewById(R.id.editText11);
        e12=findViewById(R.id.editText12);
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        e2.setText(currentDate);
        e3.setText(currentTime);
        geocoder=new Geocoder(this,Locale.getDefault());
        googleApiClient= new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(UserScreenActivity.this).addOnConnectionFailedListener(this).build();
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
        FusedLocationProviderApi fusedLocationProviderApi=LocationServices.FusedLocationApi;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(googleApiClient!=null)
            googleApiClient.connect();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener( this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient!=null)
            googleApiClient.disconnect();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(Build.VERSION.SDK_INT>=23&& ActivityCompat.checkSelfPermission(UserScreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(UserScreenActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION},1000);
        }
locationRequest=new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(1);
        if(googleApiClient.isConnected())
        {
            Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
            intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
            inp=PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            fusedLocationProviderClient.requestLocationUpdates(locationRequest,inp);
        }
        else {
            googleApiClient.connect();
            Utils.setRequestingLocationUpdates(this, false);
        } }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

      if(  connectionResult.hasResolution()){
          try {
              connectionResult.startResolutionForResult(UserScreenActivity.this, 8);
          }
          catch(Exception e)
          {e.printStackTrace();
              Toast.makeText(UserScreenActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
          }
          }
else{


        new AlertDialog.Builder(UserScreenActivity.this)
                .setTitle("Location connection not established")
                .setMessage("Connection failed")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==8&&resultCode==RESULT_OK){
            googleApiClient.connect();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1000){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null)
                        {
                            //t1.setText("latitude= "+Double.toString(location.getLatitude())+ "Latitude="+Double.toString(location.getLongitude()));
                        }
                    }
                });
            }
        }
    }

    public void getlocbtn(View view) {
        if(Build.VERSION.SDK_INT>=23&& ActivityCompat.checkSelfPermission(UserScreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(UserScreenActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION},1000);
        }
        else{

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null)
                    {
                      //  t1.setText("latitude= "+Double.toString(location.getLatitude())+ "Latitude="+Double.toString(location.getLongitude()));
                        lat=Double.toString(location.getLatitude());
                        lon=Double.toString(location.getLongitude());
                        new LookUpAddress().execute(location);

                    }
                    else
                    {
                        Toast.makeText(UserScreenActivity.this,"Turn on Location",Toast.LENGTH_LONG).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserScreenActivity.this,e.getMessage()+"..."+e.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void uploadbtn(View view) {


        HashMap<String,String> map=new HashMap<>();
        map.put("CompanyName", e1.getText().toString());
        map.put("Date",e2.getText().toString());
        map.put("Time",e3.getText().toString());
        map.put("Des",e4.getText().toString());
        map.put("Allow",e5.getText().toString());
        map.put("compintime",e11.getText().toString());
        map.put("compouttime",e12.getText().toString());
        map.put("Lat",lat);
        map.put("Long",lon);
        map.put("Address",addaftergeocoding);
        FirebaseDatabase.getInstance().getReference().child("my_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Updated_Post").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UserScreenActivity.this,"Uploaded",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserScreenActivity.this,e.getMessage()+e.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public class LookUpAddress extends AsyncTask<Location,Void, ArrayList<String>>
    {

        @Override
        protected ArrayList<String> doInBackground(Location... locations) {
            Location loc=locations[0];
            List<Address> address=null;
            Log.d("uvanisgeocode",Boolean.toString(Geocoder.isPresent()));
            if(Geocoder.isPresent())
            {
                    try{
                        address=geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),3);
                    }
                    catch (Exception e)
                    {
                    Log.d("uvan",e.getMessage());
                    }



                if(address==null||address.size()==0)
                    {
                        Toast.makeText(UserScreenActivity.this,"Error duuring conversion",Toast.LENGTH_LONG).show();

                    }
                    else{
                        Address add=address.get(0);
                       ArrayList<String> arrayList=new ArrayList<String>();

                        for(int i=0;i<=add.getMaxAddressLineIndex();i++)
                        {
                            arrayList.add(add.getAddressLine(i));
                        }
                        return arrayList;
                    }
            }
            else{
                Toast.makeText(UserScreenActivity.this,"Geocoder not available",Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {

            super.onPostExecute(strings);
            if(strings!=null)
            {   Log.d("uvan",Integer.toString(strings.size()));
                t1.append("add=");

                for(String temp:strings) {
                    addaftergeocoding = addaftergeocoding + temp;
                //t1.append(temp);

                }
                t1.setText(addaftergeocoding);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fusedLocationProviderClient.removeLocationUpdates(inp);
        FirebaseAuth.getInstance().signOut();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2,menu);

        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.logoutuser)
        {  fusedLocationProviderClient.removeLocationUpdates(inp);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UserScreenActivity.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent in=new Intent(UserScreenActivity.this,UserLoginActivity.class);
            startActivity(in);

        }else  if(item.getItemId()==R.id.Itemmap);
        {
            Intent intent=new Intent(UserScreenActivity.this,ViewUserActivity.class);
            intent.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());

            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    public void layoutclicked(View v) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        ///////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////backgroundlocation github
        /////////////////////////////////////////////

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Utils.KEY_LOCATION_UPDATES_RESULT)) {
            {
                t1.setText(Utils.getLocationUpdatesResult(this));

                Log.d("uvan", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                Log.d("uvan","serveice");


                //////////////
/*
                String curlon =Double.toString(result.getLastLocation().getLongitude());
                String curlat =Double.toString(result.getLastLocation().getLatitude());

                FirebaseDatabase.getInstance().getReference().child("my_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentlocationlatitude").setValue(curlat);
                FirebaseDatabase.getInstance().getReference().child("my_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentlocationlongitude").setValue(curlon);
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                FirebaseDatabase.getInstance().getReference().child("my_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentlocationtime").setValue(currentDate+currentTime);
                Log.d("uvan",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                Log.d("uvan",curlat+curlon+"serveice");
  */              //
            }} else if (s.equals(Utils.KEY_LOCATION_UPDATES_REQUESTED)) {

        }
    }




}
