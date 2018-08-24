package com.google.firebase.samples.apps.mlkit.carparkingdetection;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.samples.apps.mlkit.CameraSource;
import com.google.firebase.samples.apps.mlkit.FrameMetadata;
import com.google.firebase.samples.apps.mlkit.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.R;
import com.google.firebase.samples.apps.mlkit.textrecognition.TextRecognitionProcessor;

/**
 * @author liangding
 */

public class CarParkingSignProcessor extends TextRecognitionProcessor {

    private final CameraSource mCameraSource;

    public CarParkingSignProcessor(CameraSource cameraSource) {
        mCameraSource = cameraSource;
    }

    @Override
    protected void onSuccess(
            @NonNull final FirebaseVisionText results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull final GraphicOverlay graphicOverlay) {
        super.onSuccess(results, frameMetadata, graphicOverlay);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCameraSource.release();
                showImage(graphicOverlay, results.getText());
            }
        }, 5000);
    }

    private void showImage(GraphicOverlay graphicOverlay, String text) {
        ImageGraphic imageGraphic = new ImageGraphic(graphicOverlay, R.drawable.left_green);
        if (text.contains("7") && !text.contains("PERMIT")) {
            imageGraphic = new ImageGraphic(graphicOverlay, R.drawable.left_green, R.drawable
                    .right_no_stop);
        } else if (text.contains("PERMIT") || text.contains("ZONE") || text.contains("HORSE")) {
            imageGraphic = new ImageGraphic(graphicOverlay, R.drawable.left_no_stop, R.drawable
                    .right_permit);
        }
        graphicOverlay.add(imageGraphic);
    }
}
