package com.daniel.employeetracker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by djablonski on 1/11/17.
 */
@IgnoreExtraProperties
public class EmployeeInfo {

    public double latitude;
    public double longitude;
    public String name, email;
    DatabaseReference mdatabase;
    public EmployeeInfo()
    {

    }

    public EmployeeInfo(double latitude1, double longitude1, String name1, String email1)
    {
        latitude = latitude1;
        longitude = longitude1;
        name = name1;
        email = email1;
    }

    @Exclude
    public void saveToDatabase()
    {
//        mdatabase = FirebaseDatabase.getInstance().getReference();
//        EmployeeInfo post = new EmployeeInfo(latitude, longitude, name, email);
//        Map<String, Object> postValues = post;
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/" + FirebaseAuth.getInstance().getCurrentUser().getEmail() , postValues);
//
//        mdatabase.updateChildren(childUpdates);

    }
}
