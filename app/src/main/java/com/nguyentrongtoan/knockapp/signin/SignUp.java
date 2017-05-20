package com.nguyentrongtoan.knockapp.signin;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nguyentrongtoan.knockapp.R;
import com.nguyentrongtoan.knockapp.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.name;

/**
 * Created by nguyentrongtoan on 5/14/17.
 */

public class SignUp extends BaseActivity {
    private final String TAG = "Toan" + this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_screen);
        ButterKnife.bind(this);

    }


    @BindView(R.id.editTextEmail)
    EditText mMail;
    @BindView(R.id.editTextName)
    EditText mName;
    @BindView(R.id.editTextPass)
    EditText mPass;

    @OnClick(R.id.buttonSignUp)
    public void onSignUp(View view) {
        String mail = mMail.getText().toString().trim();
        String name = mName.getText().toString().trim();
        String pass = mPass.getText().toString();

        if (checkInput(mail, name, pass)) {
            showProgressDialog();
            doSignUp(mail, name, pass);
        } else {
            showSnackbar("Check your form input again!");
        }
    }

    private boolean checkInput(String mail, String name, String pass) {

        if (mail.isEmpty() || !mail.contains("@") || !mail.contains(".")) {
            return false;
        } else if (name.isEmpty()) {
            return false;
        } else if (pass.length() < 6) {
            return false;
        }

        return true;
    }

    private FirebaseUser mUser;

    private void doSignUp(final String mail, final String name, final String pass) {

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "Sign up: " + task.isSuccessful());

                        if (task.isSuccessful()) {

                            updateProfile(mail, name, auth.getCurrentUser().getUid());
                        } else {
                            showSnackbar("Check your form input again!");
                            hideProgressDialog();
                        }
                    }
                });

    }

    private void updateProfile(String email, String name, String UID) {
        DatabaseReference ref =
                FirebaseDatabase.getInstance().getReference().child("user-follower").child(UID);

        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        ref.setValue(map);

        ref = FirebaseDatabase.getInstance().getReference().child("users").child(UID);
        map.put("email", email);
        ref.setValue(map);

        finish();
    }

}
