package com.google.zxing.client.android;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.client.android.camera.CameraManager;

import io.weichao.opencv.R;

public final class CameraActivityHandler extends Handler {
    private static final String TAG = "CameraActivityHandler";

    private CameraActivity activity;
    private DecodeThread decodeThread;
    private State state;
    private CameraManager cameraManager;

    private enum State {
        DECODE_BITMAP,
        PREVIEW,
        SUCCESS,
        DONE
    }

    CameraActivityHandler(CameraActivity activity, CameraManager cameraManager) {
        this.activity = activity;
        this.cameraManager = cameraManager;

        decodeThread = new DecodeThread(activity);
        decodeThread.start();
        state = State.SUCCESS;

        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.restart_preview:
                restartPreviewAndDecode();
                break;
            case R.id.decode_succeeded:
                state = State.SUCCESS;
//                Bundle bundle = message.getData();
//                Bitmap barcode = null;
//                float scaleFactor = 1.0f;
//                if (bundle != null) {
//                    byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
//                    if (compressedBitmap != null) {
//                        barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
//                        barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
//                    }
//                    scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
//                }
//                activity.handleDecode((Result) message.obj, barcode, scaleFactor);
                activity.handleDecode((Float) message.obj);
                break;
            case R.id.decode_failed:
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    private void restartPreviewAndDecode() {
        Log.d(TAG, "restartPreviewAndDecode()");
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        }
    }
}