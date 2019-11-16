package com.example.smartdispenserv1.admin;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.smartdispenserv1.R;

import java.util.ArrayList;

import me.itangqi.waveloadingview.WaveLoadingView;


public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {


    private ArrayList<AdminItem> mAdminList;
    private  OnItemClickListener mListener;

    public interface  OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int positin);
    }

    public  void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public static class AdminViewHolder extends RecyclerView.ViewHolder {

        public  TextView namauser;
        public  WaveLoadingView volumeair;
        public ImageButton deleteButton;

        public AdminViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);


            namauser = itemView.findViewById(R.id.userNama);
            volumeair =itemView.findViewById(R.id.waveLoadingViewAdmin);
            deleteButton =itemView.findViewById(R.id.listDelete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position =getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }

                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position =getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

    public AdminAdapter(ArrayList<AdminItem> adminList){
        mAdminList = adminList;

    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.monitor_admin,viewGroup,false);
        AdminViewHolder  avh = new AdminViewHolder(v, mListener);
        return avh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder adminViewHolder, int position) {
        AdminItem currentItem = mAdminList.get(position);

        adminViewHolder.volumeair.setProgressValue(currentItem.getVolume());
        adminViewHolder.namauser.setText(currentItem.getName());

        adminViewHolder.volumeair.setCenterTitle(String.format("%d%%",mAdminList.get(position).getVolume()));

    }

    @Override
    public int getItemCount() {
        return mAdminList.size();
    }



}
