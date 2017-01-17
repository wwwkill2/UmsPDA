package com.okbuy.umspda;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.afollestad.materialdialogs.MaterialDialog;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, OnReceiverListener {

    private static final String BROADCAST_NAME = "com.android.server.scannerservice.broadcastokbuy";

    private Switch mSwitchVoice;
    private Switch mSwitchVibration;
    private Switch mSwitchContinue;
    private Switch mSwitchOverride;
    private EditText mEditText;
    private MaterialDialog mDialog;

    private boolean mContentOverride;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeDialog();
            mEditText.getText().clear();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        sendDefaultBroadcast();
        registerBroadcast();
    }

    private void sendDefaultBroadcast() {
        Intent intent = new Intent("com.android.scanner.service_settings");
        // 条码广播名称
        intent.putExtra("action_barcode_broadcast", BROADCAST_NAME);
        // 条码键值名称
        intent.putExtra("key_barcode_broadcast", CodeReceiver.CODE_NAME);
        // 连续扫描
        intent.putExtra("scan_continue", false);
        // 扫描声音
        intent.putExtra("sound_play", false);
        // 扫描震动
        intent.putExtra("viberate", false);
        // 扫描模式：广播
        intent.putExtra("barcode_send_mode", "BROADCAST");
        // 条码结束符
        intent.putExtra("endchar", "NONE");
        sendBroadcast(intent);
    }

    private void registerBroadcast() {
        CodeReceiver receiver = new CodeReceiver(this);
        IntentFilter filter = new IntentFilter(BROADCAST_NAME);
        registerReceiver(receiver, filter);
    }

    private void findViews() {
        mSwitchVoice = (Switch) findViewById(R.id.switch_voice);
        mSwitchVibration = (Switch) findViewById(R.id.switch_vibration);
        mSwitchContinue = (Switch) findViewById(R.id.switch_continue);
        mSwitchOverride = (Switch) findViewById(R.id.switch_override);
        mEditText = (EditText) findViewById(R.id.edit);
        mSwitchVoice.setOnCheckedChangeListener(this);
        mSwitchVibration.setOnCheckedChangeListener(this);
        mSwitchContinue.setOnCheckedChangeListener(this);
        mSwitchOverride.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean b) {
                mContentOverride = b;
            }
        });
        mDialog = new MaterialDialog.Builder(this).content("Loading...").progress(true, 0).cancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface anInterface) {
                mHandler.removeMessages(0);
            }
        }).build();
    }

    private void showDialog() {
        mDialog.show();
    }

    private void closeDialog() {
        mDialog.dismiss();
    }

    public void clear(View v) {
        showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = mHandler.obtainMessage();
                message.what = 0;
                mHandler.sendMessageDelayed(message, 2000);
            }
        }).start();
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean b) {
        Intent intent = new Intent("com.android.scanner.service_settings");
        switch (button.getId()) {
            case R.id.switch_voice:
                intent.putExtra("sound_play", b);
                break;
            case R.id.switch_vibration:
                intent.putExtra("viberate", b);
                break;
            case R.id.switch_continue:
                intent.putExtra("scan_continue", b);
                break;
        }
        sendBroadcast(intent);
    }

    @Override
    public void onReceiverData(String data) {
        if (mContentOverride) {
            mEditText.getText().clear();
            ;
        }
        if (mEditText.getText().length() == 0) {
            mEditText.getText().append(data);
        } else {
            mEditText.getText().append("," + data);
        }
    }
}
