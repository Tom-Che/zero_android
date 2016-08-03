package com.che.zero.activity;

import com.che.zero.R;
import com.che.zero.common.BaseActivity;

/**
 * Created by Che on 2016/07/25.
 */
public class MainActivity extends BaseActivity {
    @Override
    protected void init() {
        setContent(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        setTitleEx("主页");
    }

    @Override
    protected void loadData() {
    }
}
