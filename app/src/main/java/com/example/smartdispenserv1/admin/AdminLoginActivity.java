package com.example.smartdispenserv1.admin;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdispenserv1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {


    //Objek Coding
    EditText mEditTextEmail, mEditTextPassword;
    TextView percobaan;
    FloatingActionButton  loginButton;
    Button registerButton;
    private ProgressDialog progressDialog;


    ////Bagian Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);


        firebaseAuth=FirebaseAuth.getInstance();



        mEditTextEmail=findViewById(R.id.editTextEmailAdmin);
        mEditTextPassword=findViewById(R.id.editTextPasswordAdmin);
        registerButton =findViewById(R.id.buttonRegsterAdmin);
        loginButton =findViewById(R.id.buttonLoginAdmin);


        progressDialog=new ProgressDialog(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLoginActivity.this, RegisterAdmin.class));
            }
        });
    }


    private void signin(){
        final  String Email = mEditTextEmail.getText().toString().trim();
        final  String Password = mEditTextPassword.getText().toString().trim();

        progressDialog.setMessage("Sedang Login" );
        progressDialog.show();


        if(TextUtils.isEmpty(Email)){
            Toast.makeText(this,"Please enter the email",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        if(TextUtils.isEmpty(Password)) {
            Toast.makeText(this, "Please enter the Password", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

            return;
        }


        progressDialog.setMessage("Please wait" );
        progressDialog.show();

        penentuRoleAuth(Email,Password);


    }

    public void penentuRoleAuth(final String userEmail, final String Password ){

        String EmailTanpaTitik=userEmail.replace(".","");

        DatabaseReference userRole = database.getReference("user")
                .child(EmailTanpaTitik)
                .child("role");

        userRole.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String rolenya =dataSnapshot.getValue(String.class);

                if (rolenya.equals("admin")){

                    firebaseAuth.signInWithEmailAndPassword(userEmail,Password)
                            .addOnCompleteListener(AdminLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        //Start the profile Activity

                                        Toast.makeText(getBaseContext(), "Wellcome", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), AdminMainActivity.class));


                                    } else if (!task.isSuccessful()) {
                                        Toast.makeText(getBaseContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }

                            });

                }
                else if (rolenya.equals("user")){

                    Toast.makeText(getBaseContext(), "you are not an admin", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();


                }
                else {
                    Toast.makeText(getBaseContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





}
