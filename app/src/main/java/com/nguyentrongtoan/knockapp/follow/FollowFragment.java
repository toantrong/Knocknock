package com.nguyentrongtoan.knockapp.follow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.nguyentrongtoan.knockapp.R;
import com.nguyentrongtoan.knockapp.data.model.PersonFollow;
import com.nguyentrongtoan.knockapp.profile.ProfileScreen;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

public class FollowFragment extends Fragment implements Contract.View{
    private final String TAG = "Toan" + this.getClass().getSimpleName();

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty)
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.follow_screen, container, false);
    }

    private Context mCtx;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCtx = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCtx = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(mCtx);
        mAdapter = new FollowAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mPresenter.subscribe();


    }

    private LinearLayoutManager mLayoutManager;
    private FollowAdapter mAdapter;

    private List<PersonFollow> mData;

    Contract.Presenter mPresenter;
    @Override
    public void setPresenter(Contract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void newInsertItem(PersonFollow item) {

    }

    @Override
    public void swapData(List<PersonFollow> lists) {
        mData = lists;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void directProfile(Bundle bundle) {
        Intent intent = new Intent(getActivity(), ProfileScreen.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showEmptyView() {
        if (mData.size() == 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
        FollowAdapter() {
            mData = new ArrayList<>(0);
        }

        @Override
        public int getItemCount() {
            showEmptyView();
            return mData.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.follow_item, parent, false);
            return new ViewHolder(v);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mName;
            ImageView mPhoto;

            public ViewHolder(View itemView) {
                super(itemView);
                mName = (TextView) itemView.findViewById(R.id.textView);
                mPhoto = (ImageView) itemView.findViewById(R.id.imageView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mPresenter.onRowClick(mData.get(position).getUID());
                        }
                    }
                });
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PersonFollow person = mData.get(position);
            String name = person.name;
            String photoUrl = person.photoUrl;

            if (name != null) {
                holder.mName.setText(name);
            }

            if (photoUrl != null) {
                Glide.with(FollowFragment.this).load(photoUrl).into(holder.mPhoto);
            }

        }

    }
}
