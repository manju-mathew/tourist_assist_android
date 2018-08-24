package com.amazonaws.polly;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class AmazonPolly {

    private static final String TAG = AmazonPolly.class.getName();
    private AmazonPollyPresigningClient client;

    public AmazonPolly(Context applicationContext) {
        initializePolly(applicationContext);
    }

    private void initializePolly(Context applicationContext) {
        // Cognito pool ID. Pool needs to be unauthenticated pool with
        // Amazon Polly permissions.
        String COGNITO_POOL_ID = "us-east-1:bf110895-0454-47af-8f08-c68ed3a15db4";

        // Region of Amazon Polly.
        Regions MY_REGION = Regions.US_EAST_1;

        // Initialize the Amazon Cognito credentials provider.
        CognitoCachingCredentialsProvider credentialsProvider =
                new CognitoCachingCredentialsProvider(
                        applicationContext,
                        COGNITO_POOL_ID,
                        MY_REGION
                );

        // Create a client that supports generation of presigned URLs.
        client = new AmazonPollyPresigningClient(credentialsProvider);
    }

    private List<Voice> getAvailableVoices() {
        // Create describe voices request.
        DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

        // Synchronously ask Amazon Polly to describe available TTS voices.
        DescribeVoicesResult describeVoicesResult = client.describeVoices(describeVoicesRequest);
        return describeVoicesResult.getVoices();
    }

    public void prepareTextForPolly() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        List<Voice> voices = getAvailableVoices();
        // Create speech synthesis request.
        SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest = new
                SynthesizeSpeechPresignRequest().withText("You are good to park here!")
                .withVoiceId(pickDeviceLocale(voices).getId()).withOutputFormat(OutputFormat.Mp3);

        // Get the presigned URL for synthesized speech audio stream.
        playPolly(client.getPresignedSynthesizeSpeechUrl
                (synthesizeSpeechPresignRequest));
    }

    private Voice pickDeviceLocale(List<Voice> voices) {
        String deviceLang = Locale.getDefault().getCountry();
        for (Voice voice: voices) {
            if(deviceLang.contains(voice.getLanguageCode())){
                return voice;
            }
        }
        return voices.get(6);
    }

    private void playPolly(URL presignedSynthesizeSpeechUrl) {
        // Use MediaPlayer: https://developer.android.com/guide/topics/media/mediaplayer.html

        // Create a media player to play the synthesized audio stream.
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            // Set media player's data source to previously obtained URL.
            mediaPlayer.setDataSource(presignedSynthesizeSpeechUrl.toString());
        } catch (IOException e) {
            Log.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
        }

        // Prepare the MediaPlayer asynchronously (since the data source is a network stream).
        mediaPlayer.prepareAsync();

        // Set the callback to start the MediaPlayer when it's prepared.
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        // Set the callback to release the MediaPlayer after playback is completed.
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }
}
