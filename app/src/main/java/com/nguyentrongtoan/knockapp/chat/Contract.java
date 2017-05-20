package com.nguyentrongtoan.knockapp.chat;

import android.os.Bundle;

import com.nguyentrongtoan.knockapp.base.BasePresenter;
import com.nguyentrongtoan.knockapp.base.BaseView;
import com.nguyentrongtoan.knockapp.data.model.Chatbox;

import java.util.List;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

interface Contract {
    interface Presenter extends BasePresenter{
        void onRowClick(String keybox, String uid1, String uid2);
    }

    interface View extends BaseView<Presenter> {
        void newInsertItem(Chatbox item);
        void notifyLoadsuccess();
        void swapData(List<Chatbox> list);
        void directChatRoom(Bundle bundle);
    }

}
