package com.google.firebase.samples.apps.mlkit.carparkingdetection;

import android.support.annotation.NonNull;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.samples.apps.mlkit.FrameMetadata;
import com.google.firebase.samples.apps.mlkit.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.R;
import com.google.firebase.samples.apps.mlkit.textrecognition.TextRecognitionProcessor;

/**
 * @author liangding
 */

public class CarParkingSignProcessor extends TextRecognitionProcessor {

    @Override
    protected void onSuccess(
            @NonNull FirebaseVisionText results,
            @NonNull FrameMetadata frameMetadata, @NonNull GraphicOverlay graphicOverlay) {
        super.onSuccess(results, frameMetadata, graphicOverlay);

        ImageGraphic imageGraphic =
                new ImageGraphic(graphicOverlay, R.drawable.test, R.drawable.test);
        graphicOverlay.add(imageGraphic);
    }
}
