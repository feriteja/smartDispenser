package com.example.smartdispenserv1.User;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.smartdispenserv1.R;
import com.example.smartdispenserv1.User.UserSupport.TemplateChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChartFragment extends Fragment {


    //Chart
    LineChart mlineChart;
    LineDataSet lineDataSet = new LineDataSet (null,null);
    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    LineData lineData;
    ArrayList<Entry>yValue;


    //Firebase databasae path and Auth
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference userCupDB=database.getReference("userCup");
    DatabaseReference userCodeRFID = database.getReference("ID_RFID");
     FirebaseAuth firebaseAuth;


    //get DATE in second
    Date now = new Date();
    long tanggalpenentu = now.getTime()/1000;
    SimpleDateFormat sdf = new SimpleDateFormat("mm-HH-dd");



    //Get Fragment_user_chart Layout
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_user_chart,container,false);
    }


    //Main Code
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final TextView grafik =view.findViewById(R.id.grafikHarga);



        mlineChart=view.findViewById(R.id.lineChart);
        mlineChart.setDragEnabled(true);
        mlineChart.setScaleEnabled(true);
        styleGraph();

        yValue = new ArrayList<>(7);


        //GET DATA FIREBASE TO CHART

        getUserCodeRfid(new codeRFID() {
            @Override
            public void onCallback(String value) {
                grafik.setText(value);

                userCupDB.child(value).child("history").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()){

                            for(DataSnapshot mydatasnapshot : dataSnapshot.getChildren()){

//                                if (dataSnapshot.child("time").exists()&&dataSnapshot.child("volume").exists()) {
                                TemplateChart datapoint = mydatasnapshot.getValue(TemplateChart.class);

                                if (1538927258 <= datapoint.getTime()) {
                                    yValue.add(new Entry(datapoint.getTime(), datapoint.getWaterUsage()));

                                }


                            }
                            showChart(yValue);
                        }
                        else {
                            mlineChart.clear();
                            mlineChart.invalidate();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        getPathUser(new MyCallback() {
            @Override
            public void onCallback(String value) {



            }
        });





    }

    // get firebase path for current user
    public interface MyCallback {
        void onCallback(String value);
    }
    private  void getPathUser(final MyCallback myCallback){
        firebaseAuth=FirebaseAuth.getInstance();

        String userNya = firebaseAuth.getCurrentUser().getEmail();
        String userBase = userNya.replace(".","").toLowerCase();

        DatabaseReference pathUserDB=database.getReference("user").child(userBase);

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

    public interface codeRFID {
        void onCallback(String value);
    }

    private  void getUserCodeRfid (final codeRFID mycallback ){
        getPathUser(new MyCallback() {
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






    private void showChart(ArrayList<Entry> yValue) {

        //CONFIGURATION LINE CHART
        lineDataSet.setValues(yValue);
        lineDataSet.setValueTextSize(15f);
        lineDataSet.setCircleRadius(8f);
        lineDataSet.setLabel("Jumlah air yang diminum");
        iLineDataSets.clear();
        iLineDataSets.add(lineDataSet);

        lineData=new LineData(iLineDataSets);
        mlineChart.clear();
        mlineChart.setData(lineData);
        mlineChart.invalidate();

    }

    private void styleGraph () {

        //CUSTOM X AND Y AXIS
        final XAxis xAxis = mlineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        mlineChart.getAxisRight().setEnabled(false);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                xAxis.setLabelCount(4,true);
                long datenya = (long) value;

                return sdf.format(datenya*1000) ;
            }
        });

    }



}
