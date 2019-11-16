package com.example.smartdispenserv1.admin;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.smartdispenserv1.LoginMenuActivity;
import com.example.smartdispenserv1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.smartdispenserv1.NotificationChannelCap.CHANNEL_1_ID;


public class AdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Nav_drawer and Toolbar
    private Toolbar mTopToolbar;
    private NotificationManagerCompat notificationManager;
    DrawerLayout drawer;
    FrameLayout frameLayout;


    //Listview dan Adapter
    RecyclerView myRecycleView;
    AdminAdapter myAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<AdminItem> AdminList;
    ArrayList<NotificationArray> notificationCap;


    //Firebase Auth & Database
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myDevice = database.getReference("userDevice");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);


        // FIREBASE_AUTH STATE
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginMenuActivity.class));
        }



        /// TOOLBAR AND NAV_DRAWER
        frameLayout = findViewById(R.id.fragment_container);
        mTopToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mTopToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            frameLayout.setVisibility(View.GONE);
            navigationView.setCheckedItem(R.id.nav_home);
        }




        ///NOTIFICATION
        notificationManager = NotificationManagerCompat.from(this);

        ///CONTENT LIST
        AdminList = new ArrayList<>();

        notificationCap = new ArrayList<>();

        creatList(); // MEMASUKAN DATA KE LIST

        myRecycleView = findViewById(R.id.recycleData);
        myRecycleView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 2); //MEMBUAT LIST JADI 2 KOLOM
        myAdapter = new AdminAdapter(AdminList);

        myRecycleView.setLayoutManager(mLayoutManager);
        myRecycleView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new AdminAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick(int position) {

                alertDelete(AdminList.get(position).getName(), position);

            }
        });
    }


    //NAVIGATION LISTENER
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                frameLayout.setVisibility(View.GONE);
                break;
            case R.id.nav_regis_user:
                frameLayout.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AdminFragmentUserRegist()).commit();
                break;
            case R.id.nav_logout:
                alertLogOut(firebaseAuth.getCurrentUser().getEmail());
                break;
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void alertLogOut(String nama) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(nama);
        builder1.setMessage("Logout ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(AdminMainActivity.this, LoginMenuActivity.class));
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


    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //REMOVE ITEM
    public void removeItem(int position) {

        AdminList.remove(position);
        myAdapter.notifyItemRemoved(position);

    }


    //SHOW ALERT IF BASED ON DELETE BUTTON
    public void alertDelete(String nama, final int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminMainActivity.this);
        builder1.setTitle(nama);
        builder1.setMessage("Are you sure you want to delete this user");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        myDevice.child(AdminList.get(position).getKey()).removeValue();

                        dialog.cancel();
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


    //GET LIST CONTENT
    public void creatList() {


        myDevice.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                String keyDB = dataSnapshot.getKey();
                String nameDB = dataSnapshot.child("name").getValue(String.class);
                Integer volumeDB = dataSnapshot.child("volume").getValue(Integer.class);
                AdminList.add(new AdminItem(volumeDB, nameDB, keyDB));
                notificationCap.add(new NotificationArray(true));
                myAdapter.notifyDataSetChanged();

                int i;
                for (i = 0; i < AdminList.size(); i++) {
                    if (AdminList.get(i).getVolume() <= 100 && AdminList.get(i).getVolume() >= 40) {

                        notificationCap.set(i, new NotificationArray(false, true, true));


                    } else if (AdminList.get(i).getVolume() <= 39 && AdminList.get(i).getVolume() >= 1) {

                        notificationCap.set(i, new NotificationArray(true, false, true));


                    } else if (AdminList.get(i).getVolume() == 0) {

                        notificationCap.set(i, new NotificationArray(true, true, false));


                    }

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                //Start Mencari Index dari Arraylist, dan replace
                String keyDB = dataSnapshot.getKey();
                for (int i = 0; i < AdminList.size(); i++) {
                    AdminItem auction = AdminList.get(i);
                    if (keyDB.equals(auction.getKey())) {
                        String description = dataSnapshot.child("name").getValue(String.class);
                        Integer quantity = dataSnapshot.child("volume").getValue(Integer.class);
                        AdminList.set(i, new AdminItem(quantity, description, keyDB));


                        if (AdminList.get(i).getVolume() <= 100 && AdminList.get(i).getVolume() >= 40) {
                            if (notificationCap.get(i).fullCap) {
                                shownotification(description, description + " Fully loaded", i);
                                notificationCap.set(i, new NotificationArray(false, true, true));

                            }


                        } else if (AdminList.get(i).getVolume() <= 39 && AdminList.get(i).getVolume() >= 1) {
                            if (notificationCap.get(i).quarterCap) {
                                shownotification(description, "The water in " + description + " nearly empty", i);
                                notificationCap.set(i, new NotificationArray(true, false, true));

                            }

                        } else if (AdminList.get(i).getVolume() == 0) {
                            if (notificationCap.get(i).emptyCap) {
                                shownotification(description, "The water in " + description + " has been empty", i);

                                notificationCap.set(i, new NotificationArray(true, true, false));

                            }

                        }


                    }
                }

                myAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                //Start Mencari Index dari Arraylist, dan replace
                String keyDB = dataSnapshot.getKey();
                for (int i = 0; i < AdminList.size(); i++) {
                    AdminItem auction = AdminList.get(i);
                    if (keyDB.equals(auction.getKey())) {
                        removeItem(i);

                    }
                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    //NOTIF CONFIGURATION
    public void shownotification(String title, String content, int id) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.icon_android)
                        .setContentTitle(title)
                        .setContentText(content);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, builder.build());
        Intent notificationIntent = new Intent(this, LoginMenuActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, id, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());

    }


}
