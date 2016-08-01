package com.che.zero.common;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.che.zero.R;

/**
 * Created by Che on 2016/07/25.
 */
public class BaseActivity extends AppCompatActivity {
    protected BaseActivity mContext;
    protected ProgressDialog progressDialog;
    protected ProgressInterruptListener progressInterruptListener;
    private boolean loadViewed = false;//是否已经加载视图
    protected Toolbar toolbar;//标题栏
    private int conRes = 0;//内容资源文件
    private RelativeLayout content;

    public RelativeLayout getContent() {
        return content;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        getApplicationEx().addActivities(this);

        init();

        //处理状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        super.setContentView(R.layout.activity_base);

        //设置actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        content = (RelativeLayout) findViewById(R.id.b_content);

        View view = LayoutInflater.from(this).inflate(conRes, null);
        content.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loadViewed = true;

        initView();

        progressDialog = new ProgressDialog(this) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                if (progressInterruptListener != null) {
                    progressInterruptListener.onProgressInterruptListener(progressDialog);
                }
            }
        };
        progressDialog.setCanceledOnTouchOutside(false);
        loadData();

    }

    public void setContentFull(boolean contentFull) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) content.getLayoutParams();
        if (contentFull) {
            params.removeRule(RelativeLayout.BELOW);
        } else {
            params.addRule(RelativeLayout.BELOW, R.id.toolbar);
        }
    }

    protected <T extends View> T findViewFromContent(@IdRes int r) {
        if (loadViewed) {
            return (T) super.findViewById(R.id.b_content).findViewById(r);
        } else {
            throw new RuntimeException("没有初始化内容");
        }
    }

    protected void setContent(@LayoutRes int resId) {
        if (loadViewed) {
            throw new RuntimeException("内容页必须在init里面设置");
        }
        conRes = resId;
    }

    protected void hideTitle() {
        toolbar.setVisibility(View.GONE);
    }

    protected void showTitle() {
        toolbar.setVisibility(View.VISIBLE);
    }

    public void setTitleEx(String title) {
        getSupportActionBar().setTitle(title);
    }

    protected void init() {
    }

    protected void initView() {
    }

    protected void loadData() {
    }

    public void showLoading(final String msg, ProgressInterruptListener progressInterruptListener) {
        this.progressInterruptListener = progressInterruptListener;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && !isFinishing()) {
                    progressDialog.setMessage(msg);
                    progressDialog.show();
                }
            }
        });
    }

    public void hideLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.hide();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        getApplicationEx().removeActivities(this);
    }

    private static Toast toast = null;

    public void showTip(final String msg) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                } else {
                    toast.cancel();
                    toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
    }

    private int statusBarHeight = 0;

    protected int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }

    protected ApplicationEx getApplicationEx() {
        return (ApplicationEx) getApplication();
    }

    public interface ProgressInterruptListener {
        void onProgressInterruptListener(ProgressDialog progressDialog);
    }
}
