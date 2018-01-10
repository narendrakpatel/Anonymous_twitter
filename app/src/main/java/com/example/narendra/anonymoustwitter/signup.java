package com.example.narendra.anonymoustwitter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class signup extends AppCompatActivity {

    private EditText emailregister, passwordregister,confirmpasswordregister,usernameregister;
    private Button registerbutton;

    private FirebaseAuth mauth;
    private CoordinatorLayout coordinatorLayout;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailregister = (EditText) findViewById(R.id.emailregister);
        passwordregister = (EditText) findViewById(R.id.passwordregister);
        confirmpasswordregister = (EditText) findViewById(R.id.confirmpasswordregister);
        registerbutton = (Button) findViewById(R.id.registerbutton);
        mauth = FirebaseAuth.getInstance();
        usernameregister = (EditText) findViewById(R.id.usernameregister);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayoutregister);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User Info");

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerbutton.setEnabled(false);
                createAccount();
            }
        });

    }

    private void createAccount(){

        String email = emailregister.getText().toString().trim();
        String password = passwordregister.getText().toString().trim();
        String confirmpassword = confirmpasswordregister.getText().toString().trim();
        final String username = usernameregister.getText().toString().trim();

        if (email.isEmpty()){
            emailregister.setError("Required");
            registerbutton.setEnabled(true);
            return;
        }
        else if (password.isEmpty()){
            passwordregister.setError("Required");
            registerbutton.setEnabled(true);
            return;
        }
        else if (confirmpassword.isEmpty()){
            confirmpasswordregister.setError("Required");
            registerbutton.setEnabled(true);
            return;
        }
        else if (username.isEmpty()){
            usernameregister.setError("Required");
            registerbutton.setEnabled(true);
            return;
        }
        else  if (password.length()<6){
            passwordregister.setError("PASSWORD TOO SHORT");
        }
        else if (!(password.equals(confirmpassword))) {
            confirmpasswordregister.setError("Password doesn't match");
            registerbutton.setEnabled(true);
            return;
        }
        else{
            mauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(signup.this, feed.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        FirebaseUser user = mauth.getCurrentUser();
                        Random random = new Random();
                        int red,green,blue;
                        do {
                            red = random.nextInt(256);
                            green = random.nextInt(256);
                            blue = random.nextInt(256);
                        }
                        while (red<200&&green<200&&blue<200);

                        UserDetails userDetails = new UserDetails();
                        userDetails.setUsername(username);
                        userDetails.setColorblue(blue);
                        userDetails.setColorgreen(green);
                        userDetails.setColorred(red);
                        assert user != null;
                        userDetails.setUseruid(user.getUid());

                        databaseReference.child(user.getUid()).setValue(userDetails);

                    }
                    else{

                        Snackbar snackbar = Snackbar.make(coordinatorLayout,"Registraion Failed",Snackbar.LENGTH_LONG);
                        snackbar.setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createAccount();
                            }
                        });
                        snackbar.show();


                    }
                }
            });
        }

    }
}
