package com.nguyentrongtoan.knockapp.chat;

import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.util.Log;

import com.nguyentrongtoan.knockapp.chatroom.ChatRoom;
import com.nguyentrongtoan.knockapp.data.BaseRepository;
import com.nguyentrongtoan.knockapp.data.model.Chatbox;

import java.util.List;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

public class ChatPresenter implements Contract.Presenter {
    private Contract.View mView;
    private BaseRepository<Chatbox> mRepository;


    public ChatPresenter(BaseRepository<Chatbox> repository, Contract.View view) {
        mView = view;
        mRepository = repository;
        mView.setPresenter(this);
    }


    @Override
    public void subscribe() {
        mRepository.forceLoadTotal(new BaseRepository.LoadTotalCompleted<Chatbox>() {
            @Override
            public void onComplete(List<Chatbox> items) {
                if (items != null) {
                    mView.swapData(items);
                } else {
                    mView.notifyLoadsuccess();
                }
            }
        });
    }

    @Override
    public void unsubscribe() {
        mRepository.unsubscribe();
    }

    @Override
    public void onRowClick(String keybox, String uid1, String uid2) {
        Bundle bundle = new Bundle();
        bundle.putString(ChatRoom.EXTRA_KEY_KEYBOX, keybox);
        bundle.putString(ChatRoom.EXTRA_KEY_UID_1, uid1);
        bundle.putString(ChatRoom.EXTRA_KEY_UID_2, uid2);
        mView.directChatRoom(bundle);
    }


}
