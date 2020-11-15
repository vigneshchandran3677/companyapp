package com.example.sample;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
GoogleApiClient googleApiClient;
FusedLocationProviderClient fusedLocationProviderClient;
TextView t1;
LocationRequest locationRequest;
LocationCallback callback;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleApiClient= new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(MainActivity.this).addOnConnectionFailedListener(this).build();
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();
        Boolean Registered;
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Registered = sharedPref.getBoolean("Registered", false);
        final String ema= sharedPref.getString("Username","aa");
        String pas=sharedPref.getString("Password","aa");
        if (!Registered)
        {
            Log.v("prefuvan",ema);
            Log.v("prefuvan",pas);
            Log.v("prefuvan","kjh");
            //startActivity(new Intent(this,Splash.class));
        }else {
            final ProgressDialog po = new ProgressDialog(this);
            po.setMessage("Signing In " );
            po.show();
            Log.v("prefuvan","else");
            Log.v("prefuvan",pas);
            Log.v("prefuvan",ema);
            mAuth.signInWithEmailAndPassword(ema, pas)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                //String Username = e1.getText().toString();

                                //String Password = e2.getText().toString();

                                //   final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UserLoginActivity.this);

                                // SharedPreferences.Editor editor = sharedPref.edit();

                                //   editor.putBoolean("Registered", true);

                                // editor.putString("Username", Username);
                                // editor.putString("Password", Password);
                                // editor.apply();if(
                                po.dismiss();
                                if(ema.compareTo("admin@gmail.com")==0) {
                                    Intent intent = new Intent(MainActivity.this, AdminScreenActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Intent intent = new Intent(MainActivity.this, UserScreenActivity.class);
                                    startActivity(intent);
                                }
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e != null)
                        Toast.makeText(MainActivity.this, e.getMessage() + e.getCause(), Toast.LENGTH_LONG).show();
                }
            });
            // startActivity(new Intent(this,MainActivity.class));
        }

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

    public void userbtn(View view) {
        Intent indent=new Intent(MainActivity.this,UserLoginActivity.class);
        startActivity(indent);
    }

    public void adminbtn(View view) {
        Intent indent=new Intent(MainActivity.this,AdminLoginActivity.class);
        startActivity(indent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(Build.VERSION.SDK_INT>=23&& ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
        }
        else{

         fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
             @Override
             public void onSuccess(Location location) {
                 if(location!=null)
                 {
                 //    t1.setText("latitude= "+Double.toString(location.getLatitude())+ "Latitude="+Double.toString(location.getLongitude()));
                 }
                else
                 {
                   ///  Toast.makeText(MainActivity.this,"Turn on Location",Toast.LENGTH_LONG).show();

                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(MainActivity.this,e.getMessage()+"..."+e.toString(),Toast.LENGTH_LONG).show();
             }
         });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        new AlertDialog.Builder(MainActivity.this)
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

    public void docbtn(View view) {
        Intent intent=new Intent(MainActivity.this,DocActivity.class);
        startActivity(intent);
    }
}
