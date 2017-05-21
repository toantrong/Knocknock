package com.nguyentrongtoan.knockapp.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nguyentrongtoan.knockapp.R;
import com.nguyentrongtoan.knockapp.chatroom.ChatRoom;
import com.nguyentrongtoan.knockapp.data.BaseRepository;
import com.nguyentrongtoan.knockapp.data.ChatListData;
import com.nguyentrongtoan.knockapp.data.PeopleListData;
import com.nguyentrongtoan.knockapp.data.model.Chatbox;
import com.nguyentrongtoan.knockapp.data.model.Person;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nguyentrongtoan on 5/18/17.
 */

public class ProfileScreen extends AppCompatActivity {
    private final String TAG = "Toan" + this.getClass().getSimpleName();
    public static final String KEY_UID = "KEY_UID";
    public static final String KEY_WATCHED = "KEY_WATCHED";
    private String UID;


    @BindView(R.id.imageCover)
    ImageView mCover;
    @BindView(R.id.imageAvatar)
    ImageView mAvatar;
    @BindView(R.id.textViewLine1)
    TextView mName;
    @BindView(R.id.textViewLine2)
    TextView mStatus;
    Person mPerson;
    @BindView(R.id.layout)
    View layout;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.buttonAction)
    FloatingActionButton mAction;

    @BindView(R.id.toolBar)
    Toolbar mToolbar;

    private boolean isWatched = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle bundle = getIntent().getExtras();
        UID = bundle.getString(KEY_UID);

        if (UID == null) {
            finish();
        }

        //if is my profile have only edit action button  -> default
        //else have only send message action button
        String watched = bundle.getString(KEY_WATCHED);

        if (watched == null) {

        } else {
            mAction.setImageDrawable(getDrawable(R.drawable.ic_send_grey));
            isWatched = true;
        }

        forceLoad();


    }

    private void forceLoad() {
        layout.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        PeopleListData.getInstance().forceReLoadAItem(UID, new BaseRepository.LoadDataCompleted<Person>() {
            @Override
            public void onComplete(Person item) {
                mPerson = item;
                if (item.coverUrl != null) {
                    Glide.with(ProfileScreen.this).load(item.coverUrl).into(mCover);
                }
                if (item.photoUrl != null) {
                    Glide.with(ProfileScreen.this).load(item.photoUrl).into(mAvatar);
                }
                if (item.status != null) {
                    mStatus.setText(item.status);
                } else {
                    mStatus.setText("Something to introduce yourself, write here");
                }

                mName.setText(item.name);
                setTitle(item.name);

                layout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);

            }
        });
    }

    @OnClick(R.id.buttonAction)
    public void onAction(View view) {
        if (isWatched) {
            onSendMessage();
        } else {
            directEdit();
        }

    }

    private static final int RC_EDIT =1;

    private void directEdit() {
        Intent intent = new Intent(this, EditScreen.class);
        intent.putExtra(EditScreen.KEY_UID, UID);
        intent.putExtra(EditScreen.KEY_NAME, mPerson.name);
        intent.putExtra(EditScreen.KEY_COVER, mPerson.coverUrl);
        intent.putExtra(EditScreen.KEY_PHOTO, mPerson.photoUrl);
        intent.putExtra(EditScreen.KEY_STATUS, mPerson.status);

        startActivityForResult(intent, RC_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_EDIT & resultCode == RESULT_OK) {
            forceLoad();
        }
    }

    String keybox = null;


    private void onSendMessage() {
        showProgressDialog();
        final String uid1 = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Query query =
                FirebaseDatabase.getInstance().getReference().
                        child("user-chatboxes").child(UID)
                        .orderByChild(uid1)
                        .equalTo(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren().iterator().hasNext()) {
                    //Path [uid]/[key box]/{uid = true}
                    keybox = dataSnapshot.getChildren().iterator().next().getKey();

                } else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    keybox = ref.push().getKey();
                    Map<String, Boolean> member = new HashMap<>();
                    member.put(UID, true);
                    member.put(uid1, true);

                    Map<String, Object> map = new HashMap<>();
                    map.put("chat-boxes/" + keybox + "/member", member);
                    map.put("user-chatboxes/" + UID + "/" + keybox + "/" + uid1, true);
                    map.put("user-chatboxes/" + uid1 + "/" + keybox + "/" + UID, true);

                    ref.updateChildren(map);
                }

                hideProgressDialog();

                Intent intent = new Intent(ProfileScreen.this, ChatRoom.class);
                intent.putExtra(ChatRoom.EXTRA_KEY_KEYBOX, keybox);
                intent.putExtra(ChatRoom.EXTRA_KEY_UID_1, uid1);
                intent.putExtra(ChatRoom.EXTRA_KEY_UID_2, UID);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ProgressDialog mDialog;

    public void showProgressDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setIndeterminate(false);
            mDialog.setMessage("Stay stun!");
            mDialog.setCancelable(false);
        }

        mDialog.show();
    }

    public void hideProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
