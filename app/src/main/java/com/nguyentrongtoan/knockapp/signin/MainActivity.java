package com.nguyentrongtoan.knockapp.signin;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nguyentrongtoan.knockapp.R;
import com.nguyentrongtoan.knockapp.base.BaseActivity;
import com.nguyentrongtoan.knockapp.chat.ChatListFragment;
import com.nguyentrongtoan.knockapp.home.HomeScreen;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    private final String TAG = "Toan" + this.getClass().getSimpleName();

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseUser mUser;

    private FirebaseDatabase mDatabase;

    @BindView(R.id.imageView)
    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        ((Animatable) mImageView.getDrawable()).start();

    }

    private boolean isFirstSignIn = false;

    @Override
    protected void onStart() {
        super.onStart();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if (mUser != null) {
                    if (!isFirstSignIn) {
                        direct();
                    }
                } else {

                }
            }
        };

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void direct() {
        Log.d(TAG, "directing");
        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
        intent.putExtra(ChatListFragment.WELCOME_KEY, "lio");
        startActivity(intent);
        finish();
    }

    @BindView(R.id.editTextEmail)
    EditText mMail;
    @BindView(R.id.editTextPass)
    EditText mPass;

    @BindView(R.id.buttonGoogle)
    SignInButton mButtonGoogle;

    @OnClick(R.id.buttonLogin)
    public void onLogin(View view) {
        isFirstSignIn = false;

        String mail = mMail.getText().toString();
        String pass = mPass.getText().toString();

        //check not empty, password more than 6 chars
        if (!mail.isEmpty() && !pass.isEmpty() && pass.length() >= 6) {
            showProgressDialog();
            mAuth.signInWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "Sign in: " + task.isSuccessful());
                            hideProgressDialog();

                            if (task.isSuccessful()) {
                                //direct automatic

                            } else {
                                showSnackbar("Check your email and pass word again!");
                            }

                        }
                    });
        } else {
            showSnackbar("Check your email and pass word again!");
        }
    }

    //BEGIN FOR GOOGLE SIGN IN API
    private GoogleApiClient.OnConnectionFailedListener mFailedListener;
    private static final int RC_SIGN_IN = 1;

    GoogleApiClient mGoogleApiClient;

    @OnClick(R.id.buttonGoogle)
    public void signInGoogle(View v) {
        isFirstSignIn = true;

        showProgressDialog();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                showSnackbar("Connection failed!");

            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(MainActivity.this /* FragmentActivity */, mFailedListener /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.d(TAG, "gg sign out");

        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                final GoogleSignInAccount account = result.getSignInAccount();

                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                mAuth.signInWithCredential(authCredential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "Sign in: " + task.isSuccessful());

                                if (task.isSuccessful()) {

                                    //Only for the first time sign up with GG
                                    updateProfile(account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString());

                                } else {
                                    showSnackbar("Connection failed!");
                                }

                            }
                        });

            } else {
                showSnackbar("Connection failed!");
            }


        }
    }

    private void updateProfile(final String name, final String email, final String photoUrl) {
        String UID = mUser.getUid();

        //check create profile
        final DatabaseReference ref =
              mDatabase.getReference().child("users").child(UID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(DatabaseError databaseError) {
                showSnackbar("Connection failed!");
            }

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild("name")) {
                    Log.d(TAG, "Create new follow file");
                    updateFollow(name, photoUrl);

                    Log.d(TAG, "Create new profile");

                    Map<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("photoUrl", photoUrl);
                    ref.setValue(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        hideProgressDialog();
                                        direct();
                                    } else {
                                        hideProgressDialog();
                                        showSnackbar("Connection failed!");
                                    }
                                }
                            });
                } else {
                    Log.d(TAG, "Old profile");
                    hideProgressDialog();
                    direct();
                }
            }
        });
    }

    private void updateFollow(final String name, final String photoUrl) {
        String UID = mUser.getUid();
        DatabaseReference ref = mDatabase.getReference().child("user-follower")
                .child(UID);

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("photoUrl", photoUrl);

        ref.updateChildren(map);
    }


    @OnClick(R.id.buttonNew)
    public void signUp(View view) {
        isFirstSignIn = false;
        startActivity(new Intent(MainActivity.this, SignUp.class));
    }


}
