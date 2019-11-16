package com.example.smartdispenserv1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.smartdispenserv1.User.UserLoginActivity;
import com.example.smartdispenserv1.User.UserMainActivity;
import com.example.smartdispenserv1.admin.AdminLoginActivity;
import com.example.smartdispenserv1.admin.AdminMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginMenuActivity extends AppCompatActivity {

    Button adminButton, userButton;
    TextView curentText;


    FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);


        adminButton=findViewById(R.id.ButtonAdmin);
        userButton=findViewById(R.id.ButtonUser);
        curentText=findViewById(R.id.curentusertext);


        firebaseAuth=FirebaseAuth.getInstance();


        //   penentuRoleAuth();
        penentuRoleAuth();




        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginMenuActivity.this, AdminLoginActivity.class));

            }
        });

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginMenuActivity.this, UserLoginActivity.class));

            }
        });

    }

    public void penentuRoleAuth(){


        if(firebaseAuth.getCurrentUser()!=null){

            String CurrentUserLogged= firebaseAuth.getCurrentUser().getEmail().replace(".","");

          //  curentText.setText(CurrentUserLogged+" udah login tapi ga pindah");
            DatabaseReference checkRole =  database.getReference("user")
                    .child(CurrentUserLogged)
                    .child("role");

            checkRole.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String theRole = dataSnapshot.getValue(String.class);

                    if(theRole.equals("user")){
                        finish();
                        startActivity(new Intent(LoginMenuActivity.this, UserMainActivity.class));
                    }
                    else if (theRole.equals("admin")){
                        finish();
                        startActivity(new Intent(LoginMenuActivity.this, AdminMainActivity.class));
                    }
                    else{
                        curentText.setText("Already Login,,..,,.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }
}
