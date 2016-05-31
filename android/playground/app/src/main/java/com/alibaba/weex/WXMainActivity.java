package com.alibaba.weex;

import com.google.zxing.client.android.CaptureActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.weex.commons.util.ScreenUtil;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXRenderErrorCode;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.utils.WXFileUtils;

import java.util.HashMap;

public class WXMainActivity extends WXBaseActivity {

  private static final int CAMARA_PERMISSION_REQUEST_CODE = 0x1;

  private static final String TAG = "IndexFragment";
  private static String CURRENT_IP="your_current_IP"; // your_current_IP
  private static final String WEEX_INDEX_URL = "http://"+CURRENT_IP+":12580/examples/build/index.js";

  private ProgressBar mProgressBar;
  private TextView mTipView;

  @Override
  protected ViewGroup initLayout() {
    setContentView(R.layout.activity_wx_main);
    return (ViewGroup) findViewById(R.id.index_container);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle("WEEX");
    setSupportActionBar(toolbar);

    mProgressBar = (ProgressBar) findViewById(R.id.index_progressBar);
    mTipView = (TextView) findViewById(R.id.index_tip);
    if (!WXEnvironment.isSupport()) {
      Toast.makeText(this,"The current device does not support Weex!",Toast.LENGTH_SHORT).show();
      finish();
      return;
    }
    mProgressBar.setVisibility(View.VISIBLE);
    mTipView.setVisibility(View.VISIBLE);
  }


  @Override
  protected String getBundleUrl() {
    if(TextUtils.equals(CURRENT_IP,"your_current_IP")){
      return WXFileUtils.loadFileContent("index.js", this);
    }else{
      return WEEX_INDEX_URL;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if(TextUtils.equals(CURRENT_IP,"your_current_IP")){
      getMenuInflater().inflate(R.menu.main_scan,menu);
    }else{
      getMenuInflater().inflate(R.menu.main, menu);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_refresh) {
      if(!TextUtils.equals(CURRENT_IP,"your_current_IP")){
        rebuildWeexInstance();
        return true;
      }
    } else if (id == R.id.action_scan) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
          Toast.makeText(this, "please give me the permission", Toast.LENGTH_SHORT).show();
        } else {
          ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMARA_PERMISSION_REQUEST_CODE);
        }
      } else {
        startActivity(new Intent(this, CaptureActivity.class));
      }
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == CAMARA_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      startActivity(new Intent(this, CaptureActivity.class));
    } else {
      Toast.makeText(this, "request camara permission fail!", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onRenderSuccess(WXSDKInstance wxsdkInstance, int i, int i1) {
    mProgressBar.setVisibility(View.GONE);
    mTipView.setVisibility(View.GONE);
  }

  @Override
  public void onException(WXSDKInstance wxsdkInstance, String s, String s1) {
    mProgressBar.setVisibility(View.GONE);
    mTipView.setVisibility(View.VISIBLE);
    if (TextUtils.equals(s, WXRenderErrorCode.WX_NETWORK_ERROR)) {
      mTipView.setText(R.string.index_tip);
    } else {
      mTipView.setText("render error:" + s1);
    }
  }
}

