package com.example.narendra.anonymoustwitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailfield, passwordfield;
    private Button loginbutton;
    private Button signuplink;

    private FirebaseAuth mauth;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailfield = (EditText) findViewById(R.id.email);
        passwordfield = (EditText) findViewById(R.id.password);
        loginbutton = (Button) findViewById(R.id.login);
        signuplink = (Button) findViewById(R.id.signuplink);

        mauth = FirebaseAuth.getInstance();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.loginactivity);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        signuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, signup.class));
            }
        });

    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            startActivity(new Intent(LoginActivity.this, feed.class));
        }
        else{
            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
        }

    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser currentuser = mauth.getCurrentUser();
        if(currentuser!=null){
            updateUI(currentuser);
        }

    }

    public void signIn(){

        String email = emailfield.getText().toString().trim();
        String password = passwordfield.getText().toString().trim();

        if (!(email.isEmpty()||password.isEmpty())){

            final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Logging in...");
            dialog.show();

            mauth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mauth.getCurrentUser();
                                dialog.dismiss();
                                updateUI(user);
                            } else {
                                dialog.dismiss();
                                Snackbar snackbar = Snackbar.make(coordinatorLayout,"LOG IN FAILED",Snackbar.LENGTH_LONG);
                                snackbar.setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        signIn();
                                    }
                                });
                                updateUI(null);
                            }
                        }
                    });
        }
        else{
            if (email.isEmpty()){
                emailfield.setError("REQUIRED");
            }
            else if (password.isEmpty()){
                passwordfield.setError("REQUIRED");
            }
        }

    }

}
