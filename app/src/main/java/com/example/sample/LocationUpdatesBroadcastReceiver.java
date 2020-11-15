package com.example.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O
 * {@link android.app.PendingIntent#getBroadcast(Context, int, Intent, int)} should be used when
 * requesting location updates. Due to limits on background services,
 * {@link android.app.PendingIntent#getService(Context, int, Intent, int)} should not be used.
 *
 *  Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 *  less frequently than the interval specified in the
 *  {@link com.google.android.gms.location.LocationRequest} when the app is no longer in the
 *  foreground.
 */
public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LUBroadcastReceiver";
    ArrayList<String> time,date;
    static final String ACTION_PROCESS_UPDATES =
            "com.google.android.gms.location.sample.locationupdatespendingintent.action" +
                    ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    Utils.setLocationUpdatesResult(context, locations);
                    Utils.sendNotification(context, Utils.getLocationResultTitle(context, locations));
                    Log.i(TAG, Utils.getLocationUpdatesResult(context));
                    Log.d("uvan", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    Log.d("uvan","receive");


                    //////////////
                    final String curlon =Double.toString(result.getLastLocation().getLongitude());
                    final String curlat =Double.toString(result.getLastLocation().getLatitude());
try {
    FirebaseDatabase.getInstance().getReference().child("my_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentlocationlatitude").setValue(curlat);
    FirebaseDatabase.getInstance().getReference().child("my_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentlocationlongitude").setValue(curlon);
}catch (Exception e)
{}final String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    final String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    FirebaseDatabase.getInstance().getReference().child("my_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("currentlocationtime").setValue(currentDate+currentTime);
                    Log.d("uvan",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    Log.d("uvan",curlat+curlon+"receiver");
                    HashMap<String,String> map=new HashMap<>();
                    map.put("Date",currentDate);
                    map.put("Time",currentTime);
                    map.put("Lat",curlat);
                    map.put("Long",curlon);

                    FirebaseDatabase.getInstance().getReference().child("my_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("LocationList").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("uvan","UPloaded");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("uvan","failed");
                        }
                    });
                    time=new ArrayList<>(); date=new ArrayList<>();
                    FirebaseDatabase.getInstance().getReference().child("my_users").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Log.v("uvan","ghghghg");
                            String namee="uuu";
                            try {
                               namee = dataSnapshot.child("username").getValue().toString();
                           }catch(Exception e){}
                           for (DataSnapshot postSnapshot: dataSnapshot.child("LocationList").getChildren()) {
                                try {
                                    String currentlocationdate = postSnapshot.child("Date").getValue().toString();
                                    String currentlocationlatitude = postSnapshot.child("Lat").getValue().toString();
                                    String currentlocationlongitude = postSnapshot.child("Long").getValue().toString();

                                    String currentlocationtime = postSnapshot.child("Time").getValue().toString();
                                    time.add(currentlocationtime);
                                    date.add(currentlocationtime);
                                    DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

                                    SimpleDateFormat parser = new SimpleDateFormat("HH:mm");


                                    try {
                                        Date d = dateFormat.parse(currentlocationtime);
                                        Date ds = dateFormat.parse(currentTime);
                                        long diff = d.getTime() - ds.getTime();

                                        if (distance(Double.parseDouble(currentlocationlatitude), Double.parseDouble(currentlocationlongitude), Double.parseDouble(curlat), Double.parseDouble(curlon)) < 0.5) {
                                            if (diff <= 600000 && !(namee.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()))) {

                                                HashMap<String, String> map = new HashMap<>();
                                                map.put("Name", (dataSnapshot.child("username").getValue()).toString());
                                                map.put("Meeting date", currentDate);
                                                map.put("Meeting time", currentTime);
                                                FirebaseDatabase.getInstance().getReference().child("my_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Friends").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.d("uvan", "UPloaded");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("uvan", "failed");
                                                    }
                                                });
                                            }

                                        }

                                    } catch (ParseException e) {
                                        Log.v("uvann","hh");
                                        // Invalid date was entered
                                    }
                                }
                                catch (Exception e)
                                {
                                    Log.v("uvann","h");
                                }
                                }

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
            }
        }
    }
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }
}