package com.example.narendra.anonymoustwitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class feed extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    android.support.v7.widget.Toolbar toolbar;
    android.support.v4.app.FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //coordinatorlayout
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Feed");

        drawerLayout = findViewById(R.id.drawerlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(feed.this, drawerLayout,toolbar, R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

       // @NonNull boolean b = true;
        //getSupportActionBar().setDisplayHomeAsUpEnabled(b);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.feedcontainer,new NewFeedFragment());
        fragmentTransaction.commit();

        navigationView = findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.myposts:
                        drawerLayout.closeDrawer(navigationView);
                        //item.setChecked(true);
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.feedcontainer, new MyPostFragment());
                        fragmentTransaction.commit();
                        toolbar.setTitle("My Posts");
                        break;

                    case R.id.likeposts:
                        drawerLayout.closeDrawer(navigationView);
                        //item.setChecked(true);
                        fragmentTransaction =getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.feedcontainer, new MostLikedPosts());
                        fragmentTransaction.commit();
                        toolbar.setTitle("Liked");
                        break;

                    case R.id.signout:
                        drawerLayout.closeDrawer(navigationView);
                        new AlertDialog.Builder(feed.this,android.R.style.Theme_Material_Light_Dialog_Alert)
                                .setTitle("Sign Out").setMessage("Do you really want to Sign Out?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(feed.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                        break;
                }

                return false;
            }
        });

    }
}
