package com.google.zxing.client.android;

import android.os.Looper;

import java.util.concurrent.CountDownLatch;

final class DecodeThread extends Thread {
    private final CameraActivity activity;
    private DecodeHandler handler;
    private final CountDownLatch handlerInitLatch;

    DecodeThread(CameraActivity activity) {
        this.activity = activity;

        handlerInitLatch = new CountDownLatch(1);
    }

    DecodeHandler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(activity);
        handlerInitLatch.countDown();
        Looper.loop();
    }
}