package com.nguyentrongtoan.knockapp.base;

import android.app.ProgressDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.nguyentrongtoan.knockapp.R;

import butterknife.BindView;

/**
 * Created by nguyentrongtoan on 5/17/17.
 */

public class BaseActivity extends AppCompatActivity {

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


    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mLayout;

    public void showSnackbar(String message) {
        Snackbar.make(mLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
