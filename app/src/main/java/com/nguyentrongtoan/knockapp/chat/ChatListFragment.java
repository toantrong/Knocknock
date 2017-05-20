package com.nguyentrongtoan.knockapp.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.nguyentrongtoan.knockapp.R;
import com.nguyentrongtoan.knockapp.chatroom.ChatRoom;
import com.nguyentrongtoan.knockapp.data.model.Chatbox;
import com.nguyentrongtoan.knockapp.data.model.Person;
import com.nguyentrongtoan.knockapp.home.HomeScreen;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.media.CamcorderProfile.get;

/**
 * Created by nguyentrongtoan on 5/17/17.
 */

public class ChatListFragment extends Fragment implements Contract.View {
    public static final String WELCOME_KEY = "WELCOME_KEY";

    private FirebaseAuth mAuth;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;
    private ChatListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_list_screen, container, false);
        return v;
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

        //Setup Recycler
        if (mAdapter == null) {
            mAdapter = new ChatListAdapter();
        } else {
            showEmptyScreen();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
        Log.d("Toan", "frag destroy");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("Toan", "frag destroy view");

    }

    @OnClick(R.id.buttonAdd)
    public void onAdd(View view) {
        ((HomeScreen) getActivity()).replaceFollowView();
    }

    public String getUID() {
        return mAuth.getCurrentUser().getUid();
    }

    private Contract.Presenter mPresenter;

    @Override
    public void setPresenter(Contract.Presenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void newInsertItem(Chatbox item) {

    }

    boolean test = false;

    @Override
    public void swapData(List<Chatbox> list) {
        if (list != null && test ) {
            notifyNotification();
            test = true;
        }
        mData = list;
        mAdapter.notifyDataSetChanged();
        showEmptyScreen();
    }

    private void notifyNotification() {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getActivity(), HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pending = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(getActivity())
                .setContentText("You have new messages")
                .setContentTitle("Knocknock!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pending)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);
    }

    @Override
    public void notifyLoadsuccess() {
        showEmptyScreen();
    }

    @BindView(R.id.empty)
    View view;


    private void showEmptyScreen() {
        if (mData.size() == 0) {
            view.setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.textViewEmpty)).setText("You don't have any message!");
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * Class inner for recycle adapter
     * Inner for easily managing cycle activity and presenter
     */
    List<Chatbox> mData;

    class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
        public ChatListAdapter() {
            if (mData == null) {
                mData = new ArrayList<Chatbox>(0);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ava_with_2line, parent, false);

            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Chatbox message = mData.get(position);

            String lastMessage = message.lastMessage;

            if (lastMessage != null) {
                holder.mLastMessage.setText(lastMessage);
            } else {
                holder.mLastMessage.setText(null);
            }

            Person person = message.getPerson();
            String photo = person.photoUrl;
            if (photo != null) {
                Glide.with(ChatListFragment.this).load(photo).into(holder.mPhoto);
            }

            String name = person.name;
            holder.mName.setText(name);

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.textViewLine1)
            TextView mName;
            @BindView(R.id.textViewLine2)
            TextView mLastMessage;
            @BindView(R.id.imageView)
            ImageView mPhoto;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                            Chatbox chatbox = mData.get(getAdapterPosition());
                            String UID2 = "";
                            for (String uid : chatbox.member.keySet()) {
                                if (!uid.equals(getUID())) {
                                    UID2 = uid;
                                    break;
                                }
                            }

                            mPresenter.onRowClick(chatbox.getKeyBox(), getUID(), UID2);

                        }
                    }
                });
            }
        }

    }

    @Override
    public void directChatRoom(Bundle bundle) {
        Intent intent = new Intent(mCtx, ChatRoom.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
