package com.example.narendra.anonymoustwitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle saveInstanceState){

        super.onCreate(saveInstanceState);

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        startActivity(intent);
        finish();

    }

}
