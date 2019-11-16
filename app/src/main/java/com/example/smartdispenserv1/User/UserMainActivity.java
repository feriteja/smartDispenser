package com.example.smartdispenserv1.User;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.smartdispenserv1.LoginMenuActivity;
import com.example.smartdispenserv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.example.smartdispenserv1.NotificationChannelCap.CHANNEL_1_ID;

public class UserMainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;



    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        firebaseAuth=FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {

            finish();
            startActivity(new Intent(this, LoginMenuActivity.class));
        }

        bottomNav =findViewById(R.id.bottom_nav);

        bottomNav.setOnNavigationItemSelectedListener(navListener);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerUser,
                new HomeFragment()).commit();






    }

    private  BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment =null;
                    firebaseAuth=FirebaseAuth.getInstance();


                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerUser,
                                    new HomeFragment()).commit();
                            break;
                        case R.id.nav_chart:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerUser,
                                    new ChartFragment()).commit();
                            break;
                        case R.id.nav_logoutUser:
                            alertLogOut(firebaseAuth.getCurrentUser().getEmail());
                            break;
                    }

                    return true;
                }
            };


    public void alertLogOut(String nama) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(UserMainActivity.this);
        builder1.setTitle(nama);
        builder1.setMessage("Logout ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(UserMainActivity.this, LoginMenuActivity.class));
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }






}
