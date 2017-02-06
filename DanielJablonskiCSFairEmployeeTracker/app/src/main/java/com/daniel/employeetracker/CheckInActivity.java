package com.daniel.employeetracker;

import android.*;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckInActivity extends AppCompatActivity{

    TextView greeting;
    FirebaseUser user;
    Button checkInBtn, showLocationBtn;
    public double latitude, longitude;
    LocationManager locationManager;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        checkInBtn = (Button) findViewById(R.id.checkInBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        greeting = (TextView) findViewById(R.id.helloEmployeeText);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        showLocationBtn = (Button) findViewById(R.id.showLocationBtn);

        if(user != null)
        {
            greeting.setText("Hi " + user.getDisplayName() + ",");
        }
        checkInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


// Define a listener that responds to location updates
                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider.
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("LOCATIONCHANGE", "The location changed successfully");

                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }
                };
                if (ActivityCompat.checkSelfPermission(CheckInActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CheckInActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CheckInActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
                while(latitude == 0 || longitude == 0)
                {}
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("employee");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("/" + FirebaseAuth.getInstance().getCurrentUser().getUid()+ "/", toMap());
                ref.updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    }
                });

                Toast.makeText(CheckInActivity.this,"Your location has been sent",Toast.LENGTH_LONG).show();


            }
        });

        showLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckInActivity.this, MapsActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });

    }

    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        return result;
    }



}
