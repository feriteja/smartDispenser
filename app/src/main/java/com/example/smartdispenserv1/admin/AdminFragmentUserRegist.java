package com.example.smartdispenserv1.admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartdispenserv1.R;
import com.google.android.gms.common.internal.StringResourceValueReader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminFragmentUserRegist extends Fragment {

    //deklarasi objek koding
    private ProgressDialog progressDialog;
    EditText mEditTextEmail, mEditTextPassword, mEditTextName, mEditTextRFID;
    FloatingActionButton registerButton;

    //deklarasi firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRoleUser = database.getReference("user");
    DatabaseReference userCodeRFID = database.getReference("ID_RFID");
    DatabaseReference userCupDB = database.getReference("userCup");
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        firebaseAuth=FirebaseAuth.getInstance();


        progressDialog=new ProgressDialog(view.getContext());

        //Link activity dengan xml
        registerButton =view.findViewById(R.id.buttonRegsterUser);
        mEditTextEmail=view.findViewById(R.id.editTextEmailRegister);
        mEditTextPassword=view.findViewById(R.id.editTextPasswordRegister);
        mEditTextName = view.findViewById(R.id.editTextNameRegister);
        mEditTextRFID=view.findViewById(R.id.editTextIdRFID);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }


    public void register() {
        final String email = mEditTextEmail.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();
        final String name = mEditTextName.getText().toString();
        final String rfid = mEditTextRFID.getText().toString();




        if(TextUtils.isEmpty(email)){
            //memunculkan peringatan email kosong
            Toast.makeText(getView().getContext(), "please enter email",Toast.LENGTH_SHORT).show();
            // menghentikan peringatan
            return;
        }
        if(TextUtils.isEmpty(password)){
            //memunculkan peringatan password kosong
            Toast.makeText(getView().getContext(), "please enter password",Toast.LENGTH_SHORT).show();
            // menghentikan peringatan
            return;
        }

        progressDialog.setMessage("Registering user.....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //register  berhasil
                            Toast.makeText(getView().getContext(), "Registered Successfully",Toast.LENGTH_SHORT).show();
                            theRole(email, name,rfid);
                            mEditTextEmail.setText("");
                            mEditTextName.setText("");
                            mEditTextPassword.setText("");
                            mEditTextRFID.setText("");
                            progressDialog.dismiss();
                            firebaseAuth.signOut();








                        }else{
                            //register  gagal
                            Toast.makeText(getView().getContext(), "Registered failed, please try again",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                });

    }

    public void theRole(String email, String name, final String rfid ){

        final String userBase = email.replace(".","").toLowerCase();



        myRoleUser.child(userBase).child("role").setValue("user");
        myRoleUser.child(userBase).child("name").setValue(name);
        userCupDB.child(rfid).child("name").setValue(name);
        userCupDB.child(rfid).child("totalUsage").setValue(0);
        userCupDB.child(rfid).child("waterType").setValue("");
        userCupDB.child(rfid).child("waterReq").setValue(0);


        userCodeRFID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long jumlahchild = dataSnapshot.getChildrenCount();

                int xaax  = (int) jumlahchild +1;

                userCodeRFID.child("user"+xaax).setValue(rfid);
                myRoleUser.child(userBase).child("RFID_code").setValue("user"+xaax);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




}
