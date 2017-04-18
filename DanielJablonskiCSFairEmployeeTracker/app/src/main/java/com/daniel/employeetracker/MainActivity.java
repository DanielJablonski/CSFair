package com.daniel.employeetracker;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    CardView employeeCardview, employerCardview;
    TextView question, title, employeeText, employerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        employeeCardview = (CardView) findViewById(R.id.employeeCardview);
        employerCardview = (CardView) findViewById(R.id.employerCardview);
        title = (TextView) findViewById(R.id.title1);
        question = (TextView) findViewById(R.id.textView);
        employeeText = (TextView) findViewById(R.id.employeeText);
        employerText = (TextView) findViewById(R.id.employerText);



        AssetManager am = MainActivity.this.getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "timeburnerbold.ttf"));

        employeeText.setTypeface(typeface);
        employerText.setTypeface(typeface);
        title.setTypeface(typeface);
        question.setTypeface(typeface);

        employeeCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Intent intent = new Intent(MainActivity.this, EmployeeLoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, CheckInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                catch (Exception e) {}
            }
        });

        employerCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Intent intent = new Intent(MainActivity.this, EmployerLoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, EmployeeList.class);
                        startActivity(intent);
                        finish();
                    }
                }
                catch (Exception e) {}
            }
        });
    }
}
