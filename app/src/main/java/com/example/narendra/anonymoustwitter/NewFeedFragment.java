package com.example.narendra.anonymoustwitter;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewFeedFragment extends Fragment {


    public NewFeedFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private List<Postdetails> postdetailsList;
    private DatabaseReference databaseReference;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_feed,container, false);

        //initializing swipe refresh
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        //intializing recycler view
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewnewfeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postdetailsList = new ArrayList<>();

        //initializing databaseReference pointing to node Posts.
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Postdetails postdetails = snapshot.getValue(Postdetails.class);
                    postdetailsList.add(postdetails);
                }
                Collections.reverse(postdetailsList);
                recyclerView.setAdapter(new RecyclerViewFeedAdapter(getContext(),postdetailsList));
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

        //FAB
        floatingActionButton = view.findViewById(R.id.newpostfab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),NewPost.class));
            }
        });
        return  view;
    }

}
