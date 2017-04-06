package com.daniel.employeetracker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CheckInActivity extends AppCompatActivity{

    TextView greeting, checkInText, checkOutText, showLocationText;
    FirebaseUser user;
    CardView checkIn, showLocation;
    public double latitude, longitude;
    LocationManager locationManager;
    String latString, longString;
    Button logout;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        checkIn = (CardView) findViewById(R.id.checkIn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        greeting = (TextView) findViewById(R.id.helloEmployeeText);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        showLocation = (CardView) findViewById(R.id.showLocation);
        logout = (Button) findViewById(R.id.logOutBtn2);

        AssetManager am = CheckInActivity.this.getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "timeburnerbold.ttf"));
        TextView checkInText = (TextView) findViewById(R.id.checkInText);
        TextView checkOutText = (TextView) findViewById(R.id.checkOutText);
        TextView showLocationText = (TextView) findViewById(R.id.showLocationText);

        checkInText.setTypeface(typeface);

        //Logs user out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CheckInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                locationManager.removeUpdates(locationListener);
            }
        });

        //Shows user's name on layout
        if(user != null)
        {
            greeting.setText("Hi " + user.getDisplayName() + ",");
        }

        //Waits for user to check in and runs the following code
        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Button clicked");

                // Define a listener that responds to location updates
                locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Called when a new location is found by the network location provider.
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("LOCATIONCHANGE", "The location changed successfully");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("employee").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",","));
                        ref.updateChildren(toMap(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });

                        Toast.makeText(CheckInActivity.this,"Your location has been sent",Toast.LENGTH_SHORT).show();

                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }
                };

                //Check for location permissions
                if (ActivityCompat.checkSelfPermission(CheckInActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CheckInActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CheckInActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }

                //Request location updates every 10 seconds
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
            }
        });

        //Launches maps activity that shows location
        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocation();
            }
        });

    }

    //Updates location to firebase
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        return result;
    }

    //Collects location of user and launches MapsActivity
    public void checkLocation()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("employee").child(user.getEmail().replace(".", ","));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = new Intent(CheckInActivity.this, MapsActivity.class);
                intent.putExtra("latitude", dataSnapshot.child("latitude").getValue().toString());
                intent.putExtra("longitude", dataSnapshot.child("longitude").getValue().toString());
                intent.putExtra("marker", "You");
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
