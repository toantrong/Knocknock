package com.nguyentrongtoan.knockapp.chatroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nguyentrongtoan.knockapp.R;
import com.nguyentrongtoan.knockapp.base.BaseActivity;
import com.nguyentrongtoan.knockapp.data.BaseRepository;
import com.nguyentrongtoan.knockapp.data.PeopleListData;
import com.nguyentrongtoan.knockapp.data.model.Person;
import com.nguyentrongtoan.knockapp.data.model.SMS;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nguyentrongtoan.knockapp.R.id.textView;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

public class ChatRoom extends BaseActivity {
    private final String TAG = "Toan" + this.getClass().getSimpleName();

    public static final String EXTRA_KEY_KEYBOX = "EXTRA_KEY_KEYBOX";
    public static final String EXTRA_KEY_UID_1 = "EXTRA_KEY_UID_1";
    public static final String EXTRA_KEY_UID_2 = "EXTRA_KEY_UID_2";

    private String UID1;
    private String UID2;
    private String KEYBOX;

    private Person user2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room_screen);
        ButterKnife.bind(this);

        getExtradata(getIntent().getExtras());
        mDb = FirebaseDatabase.getInstance();

        //Setup toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("Chat room");

        loadDataUser();

        //setup Recycler
        mAdapter = new ChatRoomAdapter();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Setup text box message
        addTextChange();



    }

    private FirebaseDatabase mDb;
    private DatabaseReference mRef;
    private ChildEventListener mListener;

    public void loadDataUser() {
        showProgressDialog();
        PeopleListData.getInstance().forceLoadAItem(UID2, new BaseRepository.LoadDataCompleted<Person>() {
            @Override
            public void onComplete(Person item) {
                user2 = item;
                hideProgressDialog();
                loadMess();
            }
        });

    }


    private void loadMess() {
        mRef = mDb.getReference().child("chats").child(KEYBOX);
        mListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SMS sms = dataSnapshot.getValue(SMS.class);
                sms.setKeyBox(dataSnapshot.getKey());
                newInsertRow(sms);
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
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mRef.addChildEventListener(mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mRef.removeEventListener(mListener);
            mListener = null;
            mRef = null;
        }
    }

    private void addTextChange() {
        mButtonSend.setEnabled(false);
        mMessageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    mButtonSend.setEnabled(false);
                } else {
                    mButtonSend.setEnabled(true);
                }

            }
        });
    }

    private void getExtradata(Bundle bundle) {
        String keybox = bundle.getString(EXTRA_KEY_KEYBOX);
        String uid1 = bundle.getString(EXTRA_KEY_UID_1);
        String uid2 = bundle.getString(EXTRA_KEY_UID_2);

        if (keybox == null || keybox.isEmpty()) {
            finish();
        } else if (uid1 == null || uid1.isEmpty()) {
            finish();
        } else if (uid2 == null || uid2.isEmpty()) {
            finish();
        } else {
            UID1 = uid1;
            UID2 = uid2;
            KEYBOX = keybox;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.editText)
    EditText mMessageText;
    @BindView(R.id.toolBar)
    Toolbar mToolbar;
    @BindView(R.id.buttonSend)
    ImageButton mButtonSend;

    LinearLayoutManager mLayoutManager;
    ChatRoomAdapter mAdapter;


    @OnClick(R.id.buttonSend)
    public void doSendMessage(View v) {
        String message = mMessageText.getText().toString().trim();
        SMS sms = new SMS(UID1, message, null);
        sendMes(sms);
        mMessageText.setText(null);

    }

    private  void sendMes(SMS sms ) {
        mDb.getReference().child("chats").child(KEYBOX)
                .push().setValue(sms);
        mDb.getReference().child("chat-boxes").child(KEYBOX)
                .child("lastMessage").setValue(sms.message);
    }

    public void newInsertRow(SMS item) {
        mData.add(item);
        mAdapter.notifyItemInserted(mData.size() - 1);
        mLayoutManager.scrollToPosition(mData.size() - 1);

    }


    private List<SMS> mData;

    class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int USER1 = 1;
        private static final int USER2 = 2;

        public ChatRoomAdapter() {
            mData = new ArrayList<>(0);
        }

        @Override
        public int getItemViewType(int position) {
            String uid = mData.get(position).authorUID;
            if (uid.equals(UID1)) {
                return USER1;
            } else {
                return USER2;
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater v = LayoutInflater.from(parent.getContext());
            if (viewType == USER1) {
                return new ViewHolderUser1(v.inflate(R.layout.chat_item_user1, parent, false));
            } else {
                return new ViewHolderUser2(v.inflate(R.layout.chat_item_user2, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


            int type = getItemViewType(position);
            SMS sms = mData.get(position);

            if (type == USER1) {
                ((ViewHolderUser1) holder).mTextView.setText(sms.message);
            } else {
                ViewHolderUser2 holderYour = (ViewHolderUser2) holder;
                holderYour.mTextView.setText(sms.message);

                Glide.with(ChatRoom.this).load(user2.photoUrl).into(holderYour.mImage);
            }
        }

        class ViewHolderUser1 extends RecyclerView.ViewHolder {
            TextView mTextView;

            public ViewHolderUser1(final View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(textView);
            }

        }

        class ViewHolderUser2 extends RecyclerView.ViewHolder {
            TextView mTextView;
            ImageView mImage;

            public ViewHolderUser2(final View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(textView);
                mImage = (ImageView) itemView.findViewById(R.id.imageView);

            }
        }

    }

}


