package com.daniel.employeetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployeeList extends AppCompatActivity {

    ArrayList<String> items;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase fireRef = FirebaseDatabase.getInstance();
    DatabaseReference ref = fireRef.getReference();
    Button addEmployee;
    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);
        items = new ArrayList<>();
        Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        items.add("Employees");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EmployeeList.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        ref.child("employee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(int i =0; i < dataSnapshot.getChildrenCount(); i++)
                {
//                    items.add(dataSnapshot.child("employee" + Integer.toString(i+1)).getValue().toString());
                }
                Log.d("DONKEY", dataSnapshot.child("employee1").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DONKEY", "FAILED");
            }
        });

        addEmployee = (Button) findViewById(R.id.addEmployeeBtn);
        addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("employer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("/" + + "/", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
//                ref.updateChildren(map, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                    }
//                });
            }
        });

    }


}
