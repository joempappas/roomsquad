package com.example.mishaberkovich.roomsquad;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MySuggestionsActivity extends FragmentActivity {

    SuggestionsCollectionPagerAdapter mSuggestionsCollectionPagerAdapter;
    ViewPager mViewPager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_suggestions);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSuggestionsCollectionPagerAdapter = new SuggestionsCollectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSuggestionsCollectionPagerAdapter);

        Firebase.setAndroidContext(this);


    }

    protected void onStop(){
        super.onStop();
    }

}

class SuggestionsCollectionPagerAdapter extends FragmentStatePagerAdapter{

    Firebase roomsquad_firebase = new Firebase("https://roomsquad.firebaseio.com/");
    Firebase Users = roomsquad_firebase.child("users");
    ChildEventListener rs_users;
    final Map<String,String> SuggestionsMap = new HashMap<>();
    final ArrayList<String> SuggestionsArray = new ArrayList<>();
    int next_user_no=0;


    public SuggestionsCollectionPagerAdapter(FragmentManager fm) {
        super(fm);
        rs_users = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("profile").child("name")!=null){
                    SuggestionsArray.add(dataSnapshot.getKey());
                    SuggestionsMap.put(dataSnapshot.getKey(), "undecided");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        Users.addChildEventListener(rs_users);
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e){

        }
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new SuggestionObjectFragment();
        Bundle args = new Bundle();
        if(SuggestionsArray.size()>next_user_no){
            String next_user = SuggestionsArray.get(next_user_no);
            next_user_no++;
            if(next_user_no == SuggestionsArray.size()){
                next_user_no=0;
            }
            args.putString("user id", next_user);
        } else {
            args.putString("user id", "null");
        }

        fragment.setArguments(args);
        return fragment;


    }

    @Override
    public int getCount() {
        //return SuggestionsArray.size();
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}

