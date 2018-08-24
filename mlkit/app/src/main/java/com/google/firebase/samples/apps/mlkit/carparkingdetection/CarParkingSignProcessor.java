package com.google.firebase.samples.apps.mlkit.carparkingdetection;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.amazonaws.polly.AmazonPolly;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.samples.apps.mlkit.FrameMetadata;
import com.google.firebase.samples.apps.mlkit.GraphicOverlay;
import com.google.firebase.samples.apps.mlkit.R;
import com.google.firebase.samples.apps.mlkit.textrecognition.TextRecognitionProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangding
 */

public class CarParkingSignProcessor extends TextRecognitionProcessor {

    public interface ProcessorListener {
        void onStart();

        void onStop();
    }

    private final AmazonPolly polly;

    private List<Integer> images;
    private String message;

    private Runnable mRunnable;

    private final ProcessorListener listener;

    public CarParkingSignProcessor(Context context, ProcessorListener listener) {
        this.listener = listener;
        polly = new AmazonPolly(context);
        images = new ArrayList<>();
        message = "";
    }

    @Override
    protected void onSuccess(
            @NonNull final FirebaseVisionText results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull final GraphicOverlay graphicOverlay) {
        super.onSuccess(results, frameMetadata, graphicOverlay);

        listener.onStart();

        processResult(results.getText());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] resArr = new int[images.size()];
                for (int i = 0; i < images.size(); i++) {
                    resArr[i] = images.get(i);
                }
                ImageGraphic imageGraphic = new ImageGraphic(graphicOverlay, resArr);
                graphicOverlay.add(imageGraphic);

                listener.onStop();
            }
        }, 4000);

//        if (mRunnable != null) {
//            return;
//        }
//        mRunnable = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                polly.prepareTextForPolly(message);
//            }
//        });
//        new Handler().postDelayed(mRunnable, 4000);
    }

    private void processResult(String text) {

        images.clear();

        if (text.contains("7") && !text.contains("PERMIT")) {
            images.add(R.drawable.left_green);
            images.add(R.drawable.right_no_stop);
            message += " You cannot park to your right.";
        } else if (text.contains("PERMIT") || text.contains("ZONE") || text.contains("HORSE")) {
            images.add(R.drawable.left_no_stop);
            images.add(R.drawable.right_permit);
            message = "You cannot park to your left. You can park to your right if you have a " +
                    "permit.";
        } else {
            images.add(R.drawable.left_green);
            message = "You are good for 15 minutes to your left.";
        }
    }
}
