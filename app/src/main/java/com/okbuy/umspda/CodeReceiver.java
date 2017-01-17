package com.okbuy.umspda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by okbuy on 17-1-17.
 */

public class CodeReceiver extends BroadcastReceiver{

    public static final String CODE_NAME = "scan_code";

    private OnReceiverListener mOnReceiverListener;

    public CodeReceiver(OnReceiverListener listener) {
        super();
        mOnReceiverListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String code = intent.getExtras().getString(CODE_NAME);
        if (mOnReceiverListener != null) {
            mOnReceiverListener.onReceiverData(code);
        }
    }
}
