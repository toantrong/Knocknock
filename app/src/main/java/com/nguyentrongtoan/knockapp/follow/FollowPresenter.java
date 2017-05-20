package com.nguyentrongtoan.knockapp.follow;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.nguyentrongtoan.knockapp.data.BaseRepository;
import com.nguyentrongtoan.knockapp.data.model.PersonFollow;
import com.nguyentrongtoan.knockapp.profile.ProfileScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

public class FollowPresenter implements Contract.Presenter {
    private Contract.View mView;
    private BaseRepository<PersonFollow> mRepository;
    String myUID;

    public FollowPresenter(BaseRepository<PersonFollow> repository, Contract.View view) {
        mView = view;
        mRepository = repository;
        mView.setPresenter(this);
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    public void subscribe() {
        mRepository.forceLoadTotal(new BaseRepository.LoadTotalCompleted<PersonFollow>() {
            @Override
            public void onComplete(List<PersonFollow> items) {
                List<PersonFollow> list = new ArrayList<>();

                for (PersonFollow item : items) {
                    if (!item.getUID().equals(myUID)) {
                        list.add(item);
                    }
                }

                mView.swapData(list);

            }
        });

    }

    @Override
    public void unsubscribe() {
        mRepository.unsubscribe();
    }

    @Override
    public void onRowClick(String uid) {
        Bundle bundle = new Bundle();
        bundle.putString(ProfileScreen.KEY_UID, uid);
        bundle.putString(ProfileScreen.KEY_WATCHED, "watched");
        mView.directProfile(bundle);
    }
}
