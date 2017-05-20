package com.nguyentrongtoan.knockapp.follow;

import android.os.Bundle;

import com.nguyentrongtoan.knockapp.base.BasePresenter;
import com.nguyentrongtoan.knockapp.base.BaseView;
import com.nguyentrongtoan.knockapp.data.model.Chatbox;
import com.nguyentrongtoan.knockapp.data.model.Person;
import com.nguyentrongtoan.knockapp.data.model.PersonFollow;

import java.util.List;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

interface Contract {
    interface Presenter extends BasePresenter{
        void onRowClick(String uid);
    }

    interface View extends BaseView<Presenter> {
        void newInsertItem(PersonFollow item);
        void directProfile(Bundle bundle);
        void swapData(List<PersonFollow> lists);
    }

}
