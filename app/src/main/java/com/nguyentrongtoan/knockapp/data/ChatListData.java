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
import com.nguyentrongtoan.knockapp.data.model.Chatbox;
import com.nguyentrongtoan.knockapp.data.model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

public class ChatListData implements BaseRepository<Chatbox> {
    private final String TAG = "Toan" + this.getClass().getSimpleName();
    private static ChatListData mChatListData;
    private FirebaseDatabase mDb;

    private String UID;


    public static ChatListData getInstance() {
        if (mChatListData == null) {
            mChatListData = new ChatListData();
        }

        return mChatListData;
    }

    private List<Chatbox> mList;


    private ChatListData() {
        mDb = FirebaseDatabase.getInstance();
        mList = new ArrayList<>();
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private Query mQuery;
    private ValueEventListener mListener;
    private LoadTotalCompleted<Chatbox> mCallback;

    @Override
    public void unsubscribe() {
        if (mCallback != null) {
            mCallback = null;
        }
        if (mListener != null) {
            mQuery.removeEventListener(mListener);
            mListener = null;
            mQuery = null;
        }
    }

    @Override
    public void forceLoadTotal(LoadTotalCompleted<Chatbox> callback) {
        mCallback = callback;
        mQuery = mDb.getReference().child("chat-boxes")
                .orderByChild("member/" + UID)
                .equalTo(true);

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                mCallback.onComplete(null);
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    final Chatbox chatbox = item.getValue(Chatbox.class);
                    chatbox.setKeyBox(item.getKey());

                    for (String uid : chatbox.member.keySet()) {
                        if (!uid.equals(UID)) {
                            PeopleListData.getInstance().forceLoadAItem(uid, new LoadDataCompleted<Person>() {
                                @Override
                                public void onComplete(Person item) {
                                    chatbox.setPerson(item);
                                    mList.add(chatbox);
                                    mCallback.onComplete(mList);
                                }
                            });
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mQuery.addValueEventListener(mListener);


    }

    @Override
    public void forceLoad(LoadDataCompleted<Chatbox> callback) {

    }

    @Override
    public void forceLoadAItem(String UID, LoadDataCompleted<Chatbox> mCallback) {

    }

    public static void onDestroy() {
        mChatListData = null;
    }
}
