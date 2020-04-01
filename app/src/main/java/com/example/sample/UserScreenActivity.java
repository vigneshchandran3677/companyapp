package com.example.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserScreenActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView t1;
    EditText e1,e2,e3,e4;
    Geocoder geocoder=null;
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
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        e2.setText(currentDate);
        e3.setText(currentTime);
        geocoder=new Geocoder(this,Locale.getDefault());
        googleApiClient= new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(UserScreenActivity.this).addOnConnectionFailedListener(this).build();
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(googleApiClient!=null)
            googleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient!=null)
            googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(Build.VERSION.SDK_INT>=23&& ActivityCompat.checkSelfPermission(UserScreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
        if(Build.VERSION.SDK_INT>=23&& ActivityCompat.checkSelfPermission(UserScreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
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
}
