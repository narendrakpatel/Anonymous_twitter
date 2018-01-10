package com.example.narendra.anonymoustwitter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class MyPostFragment extends Fragment {


    public MyPostFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private List<Postdetails> postdetailsList;
    private DatabaseReference databaseReference;
    private Query queryformypost;
    private FirebaseAuth mauth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_post,container,false);

        postdetailsList = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewmypost);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        mauth = FirebaseAuth.getInstance();
        String currentuseruid = mauth.getCurrentUser().getUid();
        queryformypost = databaseReference.orderByChild("useruid").equalTo(currentuseruid);


        return view;
    }

}
