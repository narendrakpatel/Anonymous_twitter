package com.example.narendra.anonymoustwitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;
import static android.text.format.DateUtils.formatDateRange;
import static com.example.narendra.anonymoustwitter.R.drawable.abc_action_bar_item_background_material;
import static com.example.narendra.anonymoustwitter.R.drawable.ic_thumb_up_black_24dp;

/**
 * Created by Narendra on 09-01-2018.
 */

public class RecyclerViewFeedAdapter extends RecyclerView.Adapter<RecyclerViewFeedAdapter.FeedViewholder> {

    Context context;
    List<Postdetails> postdetailsList;
    Postdetails postdetails;
    private DatabaseReference databaseReferencelikes;
    private DatabaseReference databaseReferencedislikes;
    private DatabaseReference databaseReference;
    private int likes=0,dislikes=0;
    private DatabaseReference databaseReferencefirstsync;
    String currentuserid;

    public RecyclerViewFeedAdapter(Context context, List<Postdetails> postdetailsList) {
        this.context = context;
        this.postdetailsList = postdetailsList;
    }

    @Override
    public FeedViewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.cardview,parent,false);
        return new FeedViewholder(view);
    }

    @Override
    public void onBindViewHolder(final FeedViewholder holder, final int position) {
        postdetails = postdetailsList.get(position);

        //https://stackoverflow.com/questions/7666589/using-getresources-in-non-activity-class
        //rountextview
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(dptopx(20));
        gradientDrawable.setColor(Color.argb(255,postdetails.getUserred(),postdetails.getUsergreen(),postdetails.getUserblue()));
        holder.roundtextview.setBackground(gradientDrawable);

        //username
        holder.usernametextview.setText(postdetails.getUsername());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.usernametextview.setTextAppearance(R.style.CardUsername);
        }

        //timeago
        holder.timeago.setText(DateUtils.getRelativeTimeSpanString(postdetails.getTime(),System.currentTimeMillis(),MINUTE_IN_MILLIS));

        //bodytext
        holder.desctextview.setText(postdetails.getDesc());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.desctextview.setTextAppearance(R.style.Cardbodytext);
        }

        //imageview
        if (postdetails.getImagedownloadurl()!=null){
            Picasso.with(context).load(postdetails.getImagedownloadurl()).placeholder(R.drawable.placeholder).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(postdetails.getImagedownloadurl()).placeholder(R.drawable.placeholder).error(R.drawable.errorplaceholder).into(holder.imageView);
                }
            });
        }
        else {
            Picasso.with(context).load(postdetails.getImagedownloadurl()).into(holder.imageView);
        }

        databaseReferencefirstsync.child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentuserid).hasChild(postdetails.getPostkey())){
                    holder.likebtn.setImageResource(R.drawable.ic_thumb_up_black_24dp_active);
                }
                else {
                    holder.likebtn.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReferencefirstsync.child("Dislikes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentuserid).hasChild(postdetails.getPostkey())){
                    holder.dislikebtn.setImageResource(R.drawable.ic_thumb_down_black_24dp_active);
                }
                else {
                    holder.dislikebtn.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return postdetailsList.size();
    }

    public class FeedViewholder extends RecyclerView.ViewHolder {

        TextView roundtextview,usernametextview,desctextview,timeago;
        ImageView imageView;
        ImageButton likebtn,dislikebtn;

        public FeedViewholder(View itemView) {
            super(itemView);
            roundtextview = itemView.findViewById(R.id.roundtextviewcard);
            usernametextview =itemView.findViewById(R.id.usernamecard);
            desctextview = itemView.findViewById(R.id.desccard);
            timeago = itemView.findViewById(R.id.timeagocard);
            imageView = itemView.findViewById(R.id.imageviewcard);
            likebtn = itemView.findViewById(R.id.likebtncard);
            dislikebtn = itemView.findViewById(R.id.dislikebtncard);

            if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
                currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
            databaseReferencelikes = FirebaseDatabase.getInstance().getReference().child("Likes");
            databaseReferencelikes.keepSynced(true);

            databaseReferencedislikes = FirebaseDatabase.getInstance().getReference().child("Dislikes");
            databaseReferencedislikes.keepSynced(true);

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
            databaseReference.keepSynced(true);

            databaseReferencefirstsync = FirebaseDatabase.getInstance().getReference();

            likebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     databaseReferencelikes.addListenerForSingleValueEvent(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             if (dataSnapshot.child(currentuserid).hasChild(postdetailsList.get(getLayoutPosition()).getPostkey())){
                                 databaseReferencelikes.child(currentuserid).child(postdetailsList.get(getLayoutPosition()).getPostkey()).removeValue();
                                 likebtn.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                             }
                             else {
                                 databaseReferencelikes.child(currentuserid).child(postdetailsList.get(getLayoutPosition()).getPostkey()).setValue(postdetailsList.get(getLayoutPosition()));
                                 likebtn.setImageResource(R.drawable.ic_thumb_up_black_24dp_active);
                             }
                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {

                         }
                     });
                }
            });

            dislikebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReferencedislikes.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(currentuserid).hasChild(postdetailsList.get(getLayoutPosition()).getPostkey())){
                                databaseReferencedislikes.child(currentuserid).child(postdetailsList.get(getLayoutPosition()).getPostkey()).removeValue();
                                dislikebtn.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                            }
                            else {
                                databaseReferencedislikes.child(currentuserid).child(postdetailsList.get(getLayoutPosition()).getPostkey()).setValue(postdetailsList.get(getLayoutPosition()));
                                dislikebtn.setImageResource(R.drawable.ic_thumb_down_black_24dp_active);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            /*likebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //credits:piyush makhwana
                    updatelike(view,postdetailsList.get(getLayoutPosition()));
                }
            });

            //getlikes(postdetailsList.get(getLayoutPosition()));

            dislikebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updatedislike(view,postdetailsList.get(getLayoutPosition()));
                }
            });

            //getDislikes(postdetailsList.get(getLayoutPosition()));
            */

        }
    }

    //https://gist.github.com/laaptu/7867851
    public float dptopx(float dp){
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        float px = dp*displayMetrics.densityDpi/160f;
        return px;
    }
/*
    public void updatelike(final View view, final Postdetails postdetails1){

        View parent = (View) view.getParent();
        final TextView liketext = (TextView) parent.findViewById(R.id.likedisplay);

                databaseReferencelikes.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(postdetails1.getPostkey()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                databaseReferencelikes.child(postdetails1.getPostkey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                likes--;
                                liketext.setText(Integer.toString(likes));
                            }
                            else {
                                databaseReferencelikes.child(postdetails1.getPostkey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(1);
                                likes++;
                                liketext.setText(Integer.toString(likes));
                            }
                        }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return;
    }

    public void getlikes(final Postdetails postdetails){

        databaseReferencedislikes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    likes++;
                }
                databaseReference.child(postdetails.getPostkey()).child("nooflikes").setValue(likes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updatedislike(View view,Postdetails postdetails1){

    }

    public void getDislikes(final Postdetails postdetails){
        databaseReferencedislikes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    dislikes++;
                }
                databaseReference.child(postdetails.getPostkey()).child("noofdislikes").setValue(dislikes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    */
}
