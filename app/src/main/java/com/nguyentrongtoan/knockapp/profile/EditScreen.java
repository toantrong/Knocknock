package com.nguyentrongtoan.knockapp.profile;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyentrongtoan.knockapp.R;
import com.nguyentrongtoan.knockapp.base.BaseActivity;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.data;

/**
 * Created by nguyentrongtoan on 5/20/17.
 */

public class EditScreen extends BaseActivity {

    @BindView(R.id.buttonAction)
    FloatingActionButton mButton;
    @BindView(R.id.toolBar)
    Toolbar mToolbar;
    @BindView(R.id.imageAvatar)
    ImageButton mAvatar;
    @BindView(R.id.imageCover)
    ImageButton mCover;
    @BindView(R.id.textViewLine1)
    EditText mName;
    @BindView(R.id.textViewLine2)
    EditText mStatus;


    FirebaseStorage mStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_screen);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit");

        getExtras();

        mStorage = FirebaseStorage.getInstance();

    }

    public static final String KEY_UID = "KEY_UID";
    public static final String KEY_PHOTO = "KEY_PHOTO";
    public static final String KEY_COVER = "KEY_COVER";
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_STATUS = "KEY_STATUS";

    private String UID;
    String name;
    String photoUrl;
    String coverUrl;
    String status;

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();

        UID = bundle.getString(KEY_UID);
        if (UID == null) {
            onBackPressed();
        }

        name = bundle.getString(KEY_NAME);
        photoUrl = bundle.getString(KEY_PHOTO);
        coverUrl = bundle.getString(KEY_COVER);
        status = bundle.getString(KEY_STATUS);

        mName.setText(name);
        if (status != null) {
            mStatus.setText(status);
        }

        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).into(mAvatar);
        }

        if (coverUrl != null) {
            Glide.with(this).load(coverUrl).into(mCover);
        }

    }

    private static final int RC_AVATAR = 1;
    private static final int RC_COVER = 2;

    @OnClick(R.id.imageAvatar)
    public void onAvatar(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RC_AVATAR);
    }

    @OnClick(R.id.imageCover)
    public void onCover(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RC_COVER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_AVATAR) {
                loadImage(data.getData(), mAvatar);
            }

            if (requestCode == RC_COVER) {
                loadImage(data.getData(), mCover);

            }
        }
    }

    private void loadImage(Uri url, final ImageButton val) {
        Cursor returnCursor =
                getContentResolver().query(url, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

        String name;
        long size;
        if (returnCursor.moveToFirst()) {
            name = returnCursor.getString(nameIndex);
            size = returnCursor.getLong(sizeIndex);

            if (size > 3000000) {
                showSnackbar("Image too big");
            } else {
                if (val == mAvatar) {
                    uploadAvatar(url);
                } else {
                    uploadCover(url);
                }
            }
        }
    }

    private void uploadAvatar(Uri url) {
        showProgressDialog();
        StorageReference ref = mStorage.getReference()
                .child("photo").child(UID + "-" + new Date().getTime());

        ref.putFile(url)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        photoUrl = taskSnapshot.getDownloadUrl().toString();
                        Glide.with(EditScreen.this).load(photoUrl).into(mAvatar);
                        hideProgressDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSnackbar("Upload error!");
                        hideProgressDialog();
                    }
                });
    }

    private void uploadCover(Uri url) {
        showProgressDialog();
        StorageReference ref = mStorage.getReference()
                .child("cover").child(UID + "-" + new Date().getTime());

        ref.putFile(url)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        hideProgressDialog();
                        coverUrl = taskSnapshot.getDownloadUrl().toString();
                        Glide.with(EditScreen.this).load(coverUrl).into(mCover);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressDialog();
                        showSnackbar("Upload error!");
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
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.buttonAction)
    public void onApply(View v) {
        name = mName.getText().toString().trim();
        if (name.length() < 4) {
            showSnackbar("Name is too short");
            return;
        }
        status = mStatus.getText().toString().trim();

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("photoUrl", photoUrl);
        FirebaseDatabase.getInstance().getReference()
                .child("user-follower").child(UID)
                .updateChildren(map);
        map.put("status", status);
        map.put("coverUrl", coverUrl);
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(UID)
                .updateChildren(map);

        setResult(RESULT_OK);
        finish();

    }
}
