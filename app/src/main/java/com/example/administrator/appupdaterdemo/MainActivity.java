package com.example.administrator.appupdaterdemo;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.king.app.updater.AppUpdater;
import com.king.app.updater.UpdateConfig;
import com.king.app.updater.callback.UpdateCallback;

import java.io.File;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private String mUrl,
            convertedProgressBarValue,
            mPath = Environment.getExternalStorageDirectory() + File.separator + "AppUpdaterDemo";
    private int currentVersion;

    private final Object mLock = new Object();
    private Toast toast;
    private TextView mainTextView, progressBarValueTextView;
    private ImageView mainImageView;
    private ProgressBar progressBar;
    private NumberFormat numberFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializationView();

        currentVersion = 1;

        switch (currentVersion) {
            case 1:
                mainTextView.setText("第一版");
                mainImageView.setImageResource(R.drawable.pichu2);
                mUrl = "https://github.com/tenSunFree/CombineBitmapDemo/raw/master/app-debug2.apk";
                break;
            case 2:
                mainTextView.setText("第二版");
                mainImageView.setImageResource(R.drawable.pikachu2);
                mUrl = "https://github.com/tenSunFree/CombineBitmapDemo/raw/master/app-debug3.apk";
                break;
            case 3:
                mainTextView.setText("第三版");
                mainImageView.setImageResource(R.drawable.raichu2);
                mUrl = "";
                break;
        }
    }

    /**
     * 初始化View
     */
    private void initializationView() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        progressBarValueTextView = findViewById(R.id.progressBarValueTextView);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(progressBarValueTextView, TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(progressBarValueTextView, 5, 100, 1, TypedValue.COMPLEX_UNIT_SP);
        mainTextView = findViewById(R.id.mainTextView);
        mainImageView = findViewById(R.id.mainImageView);
    }

    /**
     * 解決progressBarValueTextView的顯示bug
     */
    @Override
    protected void onResume() {
        super.onResume();
        progressBarValueTextView.setText("0%");
    }

    /**
     * 下載新版本的Apk, 並且自動跳出詢問安裝
     */
    private void clickBtn2() {
        if (mUrl.equals("")) {
            showToast("已經是最新的第三版了");
        } else {
            UpdateConfig config = new UpdateConfig();
            config.setUrl(mUrl);
            config.setPath(mPath);
            new AppUpdater(getContext(), config)
                    .setUpdateCallback(new UpdateCallback() {
                        @Override
                        public void onDownloading(boolean isDownloading) {
                            if (isDownloading) {
                                showToast("已經在下載中, 請勿重複下載");
                            }
                        }

                        @Override
                        public void onStart(String url) {
                            progressBar.setProgress(0);
                            progressBar.setVisibility(View.VISIBLE);
                            progressBarValueTextView.setText("0%");
                        }

                        @Override
                        public void onProgress(int progress, int total, boolean isChange) {
                            if (isChange) {
                                progressBar.setMax(total);
                                progressBar.setProgress(progress);

                                /** 將數值轉換成整數百分比 並賦予progressBarValueTextView */
                                numberFormat = NumberFormat.getInstance();                                 // 创建一个数值格式化对象
                                numberFormat.setMaximumFractionDigits(0);                               // 设置精确到小数点后2位
                                convertedProgressBarValue = numberFormat.format((float) progress / (float) total * 100);
                                progressBarValueTextView.setText(convertedProgressBarValue + "%");
                            }
                        }

                        @Override
                        public void onFinish(File file) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancel() {
                            progressBar.setVisibility(View.INVISIBLE);
                            progressBar.setProgress(0);
                            progressBarValueTextView.setText("0%");
                        }
                    })
                    .start();
        }
    }

    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.btn2:
                clickBtn2();
                break;
        }
    }

    public Context getContext() {
        return this;
    }

    public void showToast(String text) {
        if (toast == null) {
            synchronized (mLock) {
                if (toast == null) {
                    toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
                }
            }
        }
        toast.setText(text);
        toast.show();
    }
}
