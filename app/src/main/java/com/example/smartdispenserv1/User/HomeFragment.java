package com.example.smartdispenserv1.User;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.smartdispenserv1.LoginMenuActivity;
import com.example.smartdispenserv1.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.itangqi.waveloadingview.WaveLoadingView;

import static com.example.smartdispenserv1.NotificationChannelCap.CHANNEL_1_ID;

public class HomeFragment extends Fragment {

    FloatingActionButton buttonWarm, buttonNormal, buttonHot;
    WaveLoadingView airJumlah;
    SeekBar seekBarAir;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userCupDB = database.getReference("userCup");
    DatabaseReference userCodeRFID = database.getReference("ID_RFID");
    private FirebaseAuth firebaseAuth;

    boolean gelasBol=true;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_user_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        buttonHot = view.findViewById(R.id.fab_hotWater);
        buttonWarm = view.findViewById(R.id.fab_warmWater);
        buttonNormal = view.findViewById(R.id.fab_normalWater);
        seekBarAir = view.findViewById(R.id.airReqUser);
        airJumlah = view.findViewById(R.id.jumlahairwave);






        getUserCodeRfid(new codeRFID() {
            @Override
            public void onCallback(String value) {

                // PATH TO FIREBASE BASED ON USER'S RFID
               final  DatabaseReference setWaterTypeDB = userCupDB.child(value).child("waterType");
               final  DatabaseReference setWaterAmountDB = userCupDB.child(value).child("waterReq");


               //SEND DATA TO DATABASE BASED ON BUTTON CLICKED
                buttonHot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setWaterTypeDB.setValue("panas");
                        airJumlah.setTopTitle("PANAS");




                    }
                });
                buttonWarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setWaterTypeDB.setValue("hangat");
                        airJumlah.setTopTitle("HANGAT");




                    }
                });
                buttonNormal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setWaterTypeDB.setValue("normal");
                        airJumlah.setTopTitle("NORMAL");




                    }
                });




                //SET WAVE CAP AMOUNT
                seekBarAir.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        airJumlah.setProgressValue(progress);
                        airJumlah.setCenterTitle(String.valueOf(progress+"%"));

                        if(progress>=5&&!gelasBol){
                            setWaterAmountDB.setValue(progress);
                            gelasBol=true;
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });




                //RESET PROGRESS
                setWaterAmountDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer airReq =dataSnapshot.getValue(Integer.class);

                        if(airReq<5&&gelasBol){
                            seekBarAir.setProgress(airReq);
                            shownotification("Glass","the water has been filled",10);
                            gelasBol=false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    // GET RFID PATH FOR USER IN DATABASE (support)
    public interface MyCallback {
        void onCallback(String value);
    }

    // GET CODE USER NUMBER IN DATABASE (main)
    public void getpathUser(final MyCallback myCallback){

        firebaseAuth = FirebaseAuth.getInstance();

        String userNya = firebaseAuth.getCurrentUser().getEmail();
        String userBase = userNya.replace(".", "").toLowerCase();

        DatabaseReference pathUserDB = database.getReference("user").child(userBase);

        pathUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String RFID_ID = dataSnapshot.child("RFID_code").getValue(String.class);

                myCallback.onCallback(RFID_ID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // GET USER_CODERFID FROM USER IN DATABASE
    public interface codeRFID {
        void onCallback(String value);
    }

    private  void getUserCodeRfid (final codeRFID mycallback ){
        getpathUser(new MyCallback() {
            @Override
            public void onCallback(String value) {

                userCodeRFID.child(value).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String RFID_ID = dataSnapshot.getValue(String.class);

                        mycallback.onCallback(RFID_ID);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    public void shownotification(String title, String content, int id) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getContext(), CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.icon_android)
                        .setContentTitle(title)
                        .setContentText(content);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(id, builder.build());
        Intent notificationIntent = new Intent(getContext(), LoginMenuActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), id, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());

    }



}
