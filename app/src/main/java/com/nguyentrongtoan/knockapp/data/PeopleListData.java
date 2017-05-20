package com.nguyentrongtoan.knockapp.data;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nguyentrongtoan.knockapp.data.model.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

public class PeopleListData implements BaseRepository<Person> {

    private static final String TAG = "Toan" + "PeopleListData";

    private List<Person> mPerson;

    private static PeopleListData mPeopleListData;
    private FirebaseDatabase mDb;


    public static PeopleListData getInstance() {
        if (mPeopleListData == null) {
            Log.d(TAG, "People instance list!");
            mPeopleListData = new PeopleListData();
        }
        return mPeopleListData;
    }

    private HashMap<String, Person> mData;

    private PeopleListData() {
        mDb = FirebaseDatabase.getInstance();
        mData = new HashMap<>();

    }

    @Override
    public void forceLoadAItem(final String UID, final LoadDataCompleted<Person> mCallback) {
        if (mData.containsKey(UID)) {
            mCallback.onComplete(mData.get(UID));
        } else {
            DatabaseReference ref = mDb.getReference().child("users").child(UID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Person person = dataSnapshot.getValue(Person.class);
                    person.setUID(dataSnapshot.getKey());

                    mData.put(dataSnapshot.getKey(), person);

                    mCallback.onComplete(person);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void forceReLoadAItem(final String UID, final LoadDataCompleted<Person> mCallback) {
        if (mData.containsKey(UID)) {
            mData.remove(UID);
        }
        forceLoadAItem(UID, mCallback);
    }


    public void reLoadItem(String UID) {

    }


    @Override
    public void forceLoadTotal(LoadTotalCompleted<Person> callback) {

    }

    @Override
    public void forceLoad(LoadDataCompleted<Person> callback) {

    }

    @Override
    public void unsubscribe() {

    }

    public static void onDestroy() {
        mPeopleListData =null;
    }
}
