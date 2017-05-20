package com.nguyentrongtoan.knockapp.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nguyentrongtoan.knockapp.data.model.PersonFollow;
import com.nguyentrongtoan.knockapp.data.model.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nguyentrongtoan on 5/19/17.
 */

public class FollowPersonData implements BaseRepository<PersonFollow>{


    private List<PersonFollow> mList;

    private static FollowPersonData mFollowPersonData;
    private FirebaseDatabase mDb;


    public static FollowPersonData getInstance() {
        if (mFollowPersonData == null) {
            mFollowPersonData = new FollowPersonData();
        }
        return mFollowPersonData;
    }

    private FollowPersonData() {
        mDb = FirebaseDatabase.getInstance();
        mList = new ArrayList<>();

    }


    @Override
    public void forceLoad(LoadDataCompleted<PersonFollow> callback) {

    }

    @Override
    public void forceLoadTotal(final LoadTotalCompleted<PersonFollow> callback) {
        mList.clear();
        Query query = mDb.getReference().child("user-follower");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    PersonFollow personFollow = item.getValue(PersonFollow.class);
                    personFollow.setUID(item.getKey());
                    mList.add(personFollow);
                }
                callback.onComplete(mList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void forceLoadAItem(String UID, LoadDataCompleted<PersonFollow> mCallback) {

    }

    @Override
    public void unsubscribe() {

    }

    public static void onDestroy() {
        mFollowPersonData =null;
    }


}
