package com.example.narendra.anonymoustwitter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MostLikedPosts extends Fragment {

    private RecyclerView recyclerView;
    private List<Postdetails> postdetailsList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mauth;
    private String userid;
    private SwipeRefreshLayout swipeRefreshLayout;

    public MostLikedPosts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_most_liked_posts, container, false);

        postdetailsList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewlike);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout = view.findViewById(R.id.swiperefreshlike);

        mauth = FirebaseAuth.getInstance();
        userid = mauth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    postdetailsList.add(snapshot.getValue(Postdetails.class));
                }
                Collections.reverse(postdetailsList);
                recyclerView.setAdapter(new RecyclerViewFeedAdapter(getContext(), postdetailsList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.feedcontainer,new NewFeedFragment());
                fragmentTransaction.commit();
            }
        });
        swipeRefreshLayout.setRefreshing(false);

        return view;
    }

}
