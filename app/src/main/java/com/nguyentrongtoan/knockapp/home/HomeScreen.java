package com.nguyentrongtoan.knockapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nguyentrongtoan.knockapp.R;
import com.nguyentrongtoan.knockapp.chat.ChatListFragment;
import com.nguyentrongtoan.knockapp.chat.ChatPresenter;
import com.nguyentrongtoan.knockapp.data.BaseRepository;
import com.nguyentrongtoan.knockapp.data.ChatListData;
import com.nguyentrongtoan.knockapp.data.FollowPersonData;
import com.nguyentrongtoan.knockapp.data.PeopleListData;
import com.nguyentrongtoan.knockapp.data.model.Person;
import com.nguyentrongtoan.knockapp.follow.FollowFragment;
import com.nguyentrongtoan.knockapp.follow.FollowPresenter;
import com.nguyentrongtoan.knockapp.profile.ProfileScreen;
import com.nguyentrongtoan.knockapp.signin.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nguyentrongtoan on 5/19/17.
 */

public class HomeScreen extends AppCompatActivity {

    @BindView(R.id.navigationView)
    NavigationView mNavigationView;
    @BindView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolBar)
    Toolbar mToolbar;
    ActionBarDrawerToggle mToggle;

    FragmentManager fm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        ButterKnife.bind(this);

        //setup navigation
        mNavigationView.inflateMenu(R.menu.navi_menu);
        View view = LayoutInflater.from(this).inflate(R.layout.header, mNavigationView, false);
        mNavigationView.addHeaderView(view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        //set up toolbar
        setSupportActionBar(mToolbar);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.syncState();

        //Init fragment chat


        fm = getSupportFragmentManager();

        replaceChatView();

        mAuth = FirebaseAuth.getInstance();
        loadUser();

    }

    private FirebaseAuth mAuth;
    private Person mPerson;

    private void loadUser() {
        View v = mNavigationView.getHeaderView(0);
        final ImageView mCover = (ImageView) v.findViewById(R.id.imageCover);
        final ImageView mAvatar = (ImageView) v.findViewById(R.id.imageAvatar);
        final TextView mName = (TextView) v.findViewById(R.id.textName);

        FirebaseUser mUser = mAuth.getCurrentUser();
        PeopleListData.getInstance().forceLoadAItem(mUser.getUid(), new BaseRepository.LoadDataCompleted<Person>() {
            @Override
            public void onComplete(Person item) {
                mPerson = item;
                mName.setText(item.name);
                if (item.photoUrl != null) {
                    Glide.with(getApplicationContext()).load(item.photoUrl).into(mAvatar);
                }
                if (item.coverUrl != null) {
                    Glide.with(getApplicationContext()).load(item.coverUrl).into(mCover);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.actionChat:
                cleanStack();
                return true;
            case R.id.actionFollow:
                replaceFollowView();
                return true;
            case R.id.actionProfile:
                directProfileScreen();
                return true;
            case R.id.actionLogout:
                mAuth.signOut();
                FollowPersonData.onDestroy();
                PeopleListData.onDestroy();
                ChatListData.onDestroy();

                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void replaceChatView() {
        setTitle("Chat");
        //setup MVP
        ChatListFragment chatListScreen = new ChatListFragment();
        ChatListData chatListData = ChatListData.getInstance();
        ChatPresenter chatPresenter = new ChatPresenter(chatListData, chatListScreen);
        fm.beginTransaction().replace(R.id.container, chatListScreen)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void replaceFollowView() {
        cleanStack();
        setTitle("Follow");
        mNavigationView.getMenu().findItem(R.id.actionFollow).setChecked(true);
        //setup MVP
        FollowFragment followFragment = new FollowFragment();
        FollowPersonData followPersonData = FollowPersonData.getInstance();
        FollowPresenter followPresenter = new FollowPresenter(followPersonData, followFragment);
        fm.beginTransaction().replace(R.id.container, followFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void directProfileScreen() {
        cleanStack();
        Intent intent = new Intent(this, ProfileScreen.class);
        intent.putExtra(ProfileScreen.KEY_UID, mAuth.getCurrentUser().getUid());
        startActivity(intent);
    }

    private void cleanStack() {
        setTitle("Chat");
        mNavigationView.getMenu().findItem(R.id.actionChat).setChecked(true);
        //first stack is chat
        while (fm.getBackStackEntryCount() > 1) {
            fm.popBackStackImmediate();
        }
    }

    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
            mNavigationView.getMenu().findItem(R.id.actionChat).setChecked(true);
            setTitle("Chat");
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Toan","direct destroy");
    }
}
