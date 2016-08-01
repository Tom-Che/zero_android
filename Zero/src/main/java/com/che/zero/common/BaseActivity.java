package com.che.zero.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Che on 2016/07/25.
 */
public class BaseActivity extends AppCompatActivity {
    protected BaseActivity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

    }
}
