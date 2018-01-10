package com.example.narendra.anonymoustwitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class NewPost extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private TextView roundtextview,usernametextview;
    private ImageView imageView;
    private EditText descEditText;
    private Button photoselectbtn,camerabtn;

    private static final int CAMERA_INTENT=55;
    private static final int GALLERY_INTENT=556;
    private Uri imageurinewpost;

    private FirebaseAuth mauth;
    private DatabaseReference databaseReferenceuserinfo;
    private DatabaseReference databaseReferenceposts;
    private StorageReference storageReference;
    private String currentuseruid;
    private UserDetails currentuserDetails;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //coordinatorlayout
        coordinatorLayout = findViewById(R.id.coordinatorlayoutnewpost);

        //toolbar
        toolbar = findViewById(R.id.toolbarnewpost);
        setSupportActionBar(toolbar);

        roundtextview = findViewById(R.id.roundtextviewnewpost);
        usernametextview = findViewById(R.id.usernametextviewnewpost);

        imageView = findViewById(R.id.imageviewnewpost);

        descEditText = findViewById(R.id.desceditTextnewpost);

        //select photo from gallery
        photoselectbtn = findViewById(R.id.photoselectbtnnewpost);
        photoselectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryintent = new Intent(Intent.ACTION_PICK);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GALLERY_INTENT);
            }
        });

        //capture photo from camera

        camerabtn = findViewById(R.id.camerabtnnewpost);
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = null;

                try {
                    file = File.createTempFile("temp",".png", Environment.getExternalStorageDirectory());
                imageurinewpost = FileProvider.getUriForFile(NewPost.this,BuildConfig.APPLICATION_ID+".provider", file);
                    Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,imageurinewpost);
                    startActivityForResult(cameraintent,CAMERA_INTENT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        //getting username and roundtextview
        mauth = FirebaseAuth.getInstance();
        databaseReferenceuserinfo = FirebaseDatabase.getInstance().getReference().child("User Info");
        databaseReferenceuserinfo.keepSynced(true);

        //setting username and roudtextview
        currentuserDetails = new UserDetails();
        currentuseruid = mauth.getCurrentUser().getUid();
        DatabaseReference reference = databaseReferenceuserinfo.child(currentuseruid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setCornerRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20,getResources().getDisplayMetrics()));
                gradientDrawable.setColor(Color.argb(255,userDetails.getColorred(),userDetails.getColorgreen(),userDetails.getColorblue()));
                roundtextview.setBackground(gradientDrawable);
                usernametextview.setText(userDetails.getUsername());
                currentuserDetails = userDetails;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //posting new post
        databaseReferenceposts = FirebaseDatabase.getInstance().getReference().child("Posts");
        storageReference = FirebaseStorage.getInstance().getReference().child("Photos");

        progressDialog = new ProgressDialog(this);

    }


    //Activity result for camera and gallery intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //gallery
        if (requestCode==GALLERY_INTENT&&resultCode==RESULT_OK){
            imageurinewpost = data.getData();
            imageView.setImageURI(imageurinewpost);
            //https://stackoverflow.com/questions/26083678/set-programmatically-margin-in-android
            //https://stackoverflow.com/questions/12728255/in-android-how-do-i-set-margins-in-dp-programmatically
            ViewGroup.MarginLayoutParams marginimageview = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            marginimageview.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16, this.getResources().getDisplayMetrics());
            imageView.setLayoutParams(marginimageview);
            return;
        }

        //camera

        if (requestCode==CAMERA_INTENT&&resultCode==RESULT_OK){
            imageView.setImageURI(imageurinewpost);
            //https://stackoverflow.com/questions/26083678/set-programmatically-margin-in-android
            //https://stackoverflow.com/questions/12728255/in-android-how-do-i-set-margins-in-dp-programmatically
            ViewGroup.MarginLayoutParams marginimageview = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            marginimageview.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16, this.getResources().getDisplayMetrics());
            imageView.setLayoutParams(marginimageview);
        }

    }


    //oncreateOptionsMenu newpost btn
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.postnewpostmenu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.getItem(0).setEnabled(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    //onmenuitemselect
    //https://stackoverflow.com/questions/35648913/how-to-set-menu-to-toolbar-in-android
    //https://stackoverflow.com/questions/30886567/how-add-a-single-menu-item-left-to-the-toolbar-in-android
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.newpostmenubtn:
                postToFirebase();
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void postToFirebase() {
        final String desc = descEditText.getText().toString().trim();
        final long time = System.currentTimeMillis();
        final String username = currentuserDetails.getUsername();
        final String useruid = currentuserDetails.getUseruid();
        final int userred = currentuserDetails.getColorred();
        final int usergreen = currentuserDetails.getColorgreen();
        final int userblue = currentuserDetails.getColorblue();

        final String postkey = databaseReferenceposts.push().getKey();

        if (imageurinewpost!=null){
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference storeref = storageReference.child(postkey);
            storeref.putFile(imageurinewpost)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Uri uri = taskSnapshot.getDownloadUrl();

                    Postdetails postdetailsnew = new Postdetails();
                    postdetailsnew.setDesc(desc);
                    postdetailsnew.setTime(time);
                    postdetailsnew.setUsername(username);
                    postdetailsnew.setUseruid(useruid);
                    postdetailsnew.setUserred(userred);
                    postdetailsnew.setUsergreen(usergreen);
                    postdetailsnew.setUserblue(userblue);
                    postdetailsnew.setImagedownloadurl(uri.toString());
                    postdetailsnew.setPostkey(postkey);

                    databaseReferenceposts.child(postkey).setValue(postdetailsnew);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(coordinatorLayout,"Upload Failed",Snackbar.LENGTH_LONG);
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            postToFirebase();
                            finish();
                        }
                    });
                    snackbar.show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Double progress = 100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(String.valueOf(progress) + "% Uploaded" );
                }
            });

            return;
        }

        Postdetails postdetails = new Postdetails();
        postdetails.setDesc(desc);
        postdetails.setTime(time);
        postdetails.setUsername(username);
        postdetails.setUseruid(useruid);
        postdetails.setUserred(userred);
        postdetails.setUsergreen(usergreen);
        postdetails.setUserblue(userblue);
        postdetails.setPostkey(postkey);

        databaseReferenceposts.child(postkey).setValue(postdetails);

    }

    //View=DecorView@93108f5[Uploading] not attached to window manager
    //solution https://stackoverflow.com/questions/22924825/view-not-attached-to-window-manager-crash
    @Override
    protected void onDestroy() {
        if (progressDialog!=null&&progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDestroy();
    }
}
