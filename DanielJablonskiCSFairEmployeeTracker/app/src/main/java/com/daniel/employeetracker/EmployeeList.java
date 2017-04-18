package com.daniel.employeetracker;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    Button addEmployee, checkLocationBtn;
    String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",",");
    EditText emailOfEmployee;
    DatabaseReference mRef;
    String previousEmployee, employeeEmail;
    DataSnapshot employeeNumber;
    TextView displayName;
    Spinner dropdown;
    CardView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        items = new ArrayList<>();
        dropdown = (Spinner)findViewById(R.id.spinner);
        items.add("Employees");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EmployeeList.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        emailOfEmployee = (EditText) findViewById(R.id.employeeEmail);
        previousEmployee = "";
        displayName = (TextView) findViewById(R.id.displayName);
        logout = (CardView) findViewById(R.id.logoutCardview1);
        checkLocationBtn = (Button) findViewById(R.id.checkGps);
        mRef = FirebaseDatabase.getInstance().getReference().child("employer").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",","));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(EmployeeList.this, MainActivity.class);
                startActivity(logoutIntent);
                finish();
                Toast.makeText(EmployeeList.this, "You have been logged out", Toast.LENGTH_SHORT).show();
            }
        });

        checkLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef.child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        Intent intent = new Intent(EmployeeList.this, MapsActivity.class);
                        intent.putExtra("latitude", dataSnapshot.child(dropdown.getSelectedItem().toString()).child("latitude").getValue().toString());
                        intent.putExtra("longitude", dataSnapshot.child(dropdown.getSelectedItem().toString()).child("longitude").getValue().toString());
                        intent.putExtra("marker", dataSnapshot.child(dropdown.getSelectedItem().toString()).child("name").getValue().toString());
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        displayName.setText("Hi " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + ",");

        mRef.child("employees").orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    items.add(snapshot.child("name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addEmployee = (Button) findViewById(R.id.addEmployeeBtn);
        addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                employeeEmail = emailOfEmployee.getText().toString();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            employeeNumber = dataSnapshot.child("employer").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",",")).child("employees");
//                            System.out.println(dataSnapshot.child("employer").child(user).child("employees").child("employee" + String.valueOf(employeeNumber.getChildrenCount())).child("name").getValue().toString());
                            if (dataSnapshot.child("employee").child(emailOfEmployee.getText().toString().replace(".", ",")).getValue().toString() != null) {

                                DataSnapshot employee =dataSnapshot.child("employee").child(emailOfEmployee.getText().toString().replace(".", ","));
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("/employees/" + employee.child("name").getValue().toString() + "/", toMap(employee.child("name"), employee.child("latitude"), employee.child("longitude")));
                                mRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    }
                                });
                                Log.d("Employee", employeeEmail);
                                Log.d("EmployeePrevious", previousEmployee);
                                if (!employeeEmail.equals(previousEmployee))
                                {
                                    items.add(dataSnapshot.child("employer").child(user).child("employees").getValue().toString());
                                    Toast.makeText(EmployeeList.this, "Employee has been added", Toast.LENGTH_SHORT).show();
                                    previousEmployee = emailOfEmployee.getText().toString();
                                }
                                else
                                {
                                    Toast.makeText(EmployeeList.this, "Employee already has been added", Toast.LENGTH_SHORT).show();
                                }


                            }

                        }
                        catch (Exception e) {
                            Toast.makeText(EmployeeList.this, "User does not exist", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DONKEY", "FAILED");
                    }
                });


            }
        });

    }

    public Map<String, Object> toMap(DataSnapshot name, DataSnapshot latitude, DataSnapshot longitude)
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name.getValue().toString());
        result.put("latitude", latitude.getValue().toString());
        result.put("longitude", longitude.getValue().toString());
        return result;
    }


}
