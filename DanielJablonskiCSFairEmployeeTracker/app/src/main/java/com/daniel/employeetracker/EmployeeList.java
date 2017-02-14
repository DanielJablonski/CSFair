package com.daniel.employeetracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    FirebaseDatabase fireRef = FirebaseDatabase.getInstance();
    DatabaseReference ref = fireRef.getReference();
    Button addEmployee;
    String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",",");
    EditText emailOfEmployee;
    DatabaseReference mRef;
    String previousEmployee, employeeEmail;
    DataSnapshot employeeNumber;
    TextView displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);
        items = new ArrayList<>();
        Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        items.add("Employees");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EmployeeList.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        emailOfEmployee = (EditText) findViewById(R.id.employeeEmail);
        previousEmployee = "";
        displayName = (TextView) findViewById(R.id.displayName);
        displayName.setText("Hi " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + ",");
        ref.child("employee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(int i =0; i < dataSnapshot.getChildrenCount(); i++)
                {
//                    items.add(dataSnapshot.child(dataSnapshot.getChildren().toString().replace(",",".")));
                }

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
                employeeEmail = emailOfEmployee.getText().toString();
                mRef = FirebaseDatabase.getInstance().getReference().child("employer").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",","));
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            employeeNumber = dataSnapshot.child("employer").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".",",")).child("employees");
//                            System.out.println(dataSnapshot.child("employer").child(user).child("employees").child("employee" + String.valueOf(employeeNumber.getChildrenCount())).child("name").getValue().toString());
                            if (dataSnapshot.child("employee").child(emailOfEmployee.getText().toString().replace(".", ",")).getValue().toString() != null) {

                                DataSnapshot employee =dataSnapshot.child("employee").child(emailOfEmployee.getText().toString().replace(".", ","));
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("/employees/" + employee.child("name").getValue().toString() + "/", toMap(employee.child("latitude"), employee.child("longitude")));
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

    public Map<String, Object> toMap(DataSnapshot latitude, DataSnapshot longitude)
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("latitude", latitude.getValue().toString());
        result.put("longitude", longitude.getValue().toString());
        return result;
    }


}
