package chenliu.madcourse.neu.edu.numad18s_chenliu.AnimalSudoku;

/***
 * Excerpted from "Hello, Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/eband4 for more book information.
 ***/

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import chenliu.madcourse.neu.edu.numad18s_chenliu.GlobalClass;
import chenliu.madcourse.neu.edu.numad18s_chenliu.R;
import chenliu.madcourse.neu.edu.numad18s_chenliu.models.ASUser;

public class AS_MainActivity extends Activity {
    public MediaPlayer mMediaPlayer;
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int LOCATION_REQUEST_CODE = 101;
    private DatabaseReference mDatabase;
    private String token;
    private LocationRequest mLocationRequest;


    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.as_activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();

        getUpdatedLocation();
        updateUserLocation();

    }

    private void updateUserLocation() {

        if (GlobalClass.updatedLocation != null) {
            DatabaseReference tokenRef = mDatabase.child("asusers").child(token);

            tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ASUser user = dataSnapshot.getValue(ASUser.class);
                        double latitude = GlobalClass.updatedLocation.getLatitude();
                        double longitude = GlobalClass.updatedLocation.getLongitude();
                        user.setLatitude(latitude);
                        user.setLongitude(longitude);
                        Log.d("Location", String.valueOf(user.getLatitude()));
                        Log.d("Location", String.valueOf(user.getLongitude()));
                        mDatabase.child("asusers").child(token).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void getUpdatedLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getApplicationContext(),
                        "Location permission is needed",
                        Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);

        } else {
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    List<Location> locationList = locationResult.getLocations();

                    if (locationList.size() > 0) {
                        //The last location in the list is the newest
                        Location location = locationList.get(locationList.size() - 1);
                        Log.d("Location", String.valueOf(location.getLatitude()));
                        Log.d("Location", String.valueOf(location.getLongitude()));
                        GlobalClass.updatedLocation = location;
                    }
                }
            };

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

            updateUserLocation();

//            mFusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            // Got last known location. In some rare situations this can be null.
//                            Log.d("Location", String.valueOf(location == null));
//                            Log.d("Location", String.valueOf(GlobalClass.updatedLocation == null));
//                            if (location != null) {
//                                // Logic to handle location object
//                                GlobalClass.updatedLocation = location;
//                                Log.d("Location", String.valueOf(location.getLatitude()));
//                                Log.d("Location", String.valueOf(location.getLongitude()));
//                                updateUserLocation();
//                            }
//                        }
//                    });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUpdatedLocation();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Location permission is not granted",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.background_wg);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }
}