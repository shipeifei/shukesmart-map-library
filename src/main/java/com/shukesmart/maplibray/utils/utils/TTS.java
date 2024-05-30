package com.shukesmart.maplibray.utils.utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.Connection;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TTS {

    private final SpeechConfig speechConfig;
    private SpeechSynthesizer synthesizer;
    private Connection connection;
    private final AudioTrack audioTrack = new AudioTrack(
            new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
            new AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(24000)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
            AudioTrack.getMinBufferSize(
                    24000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT) * 2,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE);
    ;

    ExecutorService singleThreadExecutor;
    SpeakingRunnable speakingRunnable;
    private final Object synchronizedObj = new Object();
    private boolean stopped = false;

    public TTS() {
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        speakingRunnable = new SpeakingRunnable();
        String serviceRegion = "centralindia";
        String speechSubscriptionKey = "426a2c5afdc34b72bbd57cac3ea80415";
        speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
        // Use 24k Hz format for higher quality.
        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Raw24Khz16BitMonoPcm);
        // Set voice name.
        speechConfig.setSpeechSynthesisVoiceName("en-US-AvaMultilingualNeural");
    }

    public void startSpeed(String text,Boolean isOpenTts) {
        if(isOpenTts) {


            if (synthesizer != null) {
                speechConfig.close();
                synthesizer.close();
                connection.close();
            }

            synthesizer = new SpeechSynthesizer(speechConfig, null);
            connection = Connection.fromSpeechSynthesizer(synthesizer);


            connection.connected.addEventListener((o, e) -> {
                Log.e("connected", "Connection established.\n");
            });

            connection.disconnected.addEventListener((o, e) -> {
                Log.e("connected", "Connection disconnected.\n");
            });

            synthesizer.SynthesisStarted.addEventListener((o, e) -> {

                Log.e("SynthesisStarted", String.format(
                        "Synthesis started. Result Id: %s.\n",
                        e.getResult().getResultId()));

                e.close();
            });

            synthesizer.Synthesizing.addEventListener((o, e) -> {

                e.close();
            });

            synthesizer.SynthesisCompleted.addEventListener((o, e) -> {
                e.close();
            });

            synthesizer.SynthesisCanceled.addEventListener((o, e) -> {

                e.close();
            });


            speakingRunnable.setContent(text);
            singleThreadExecutor.execute(speakingRunnable);
        }
    }

    public void stopSynthesizing() {
        if (synthesizer != null) {
            synthesizer.StopSpeakingAsync();
        }
        synchronized (synchronizedObj) {
            stopped = true;
        }
        audioTrack.pause();
        audioTrack.flush();
        this.onDestroy();
    }

    public void onDestroy() {


        // Release speech synthesizer and its dependencies
        if (synthesizer != null) {
            synthesizer.close();
            connection.close();
        }
        if (speechConfig != null) {
            speechConfig.close();
        }

        singleThreadExecutor.shutdownNow();
        audioTrack.stop();
        audioTrack.release();
    }

    class SpeakingRunnable implements Runnable {
        private String content;

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public void run() {
            try {
                audioTrack.play();
                synchronized (synchronizedObj) {
                    stopped = false;
                }

                SpeechSynthesisResult result = synthesizer.StartSpeakingTextAsync(content).get();
                AudioDataStream audioDataStream = AudioDataStream.fromResult(result);

                // Set the chunk size to 50 ms. 24000 * 16 * 0.05 / 8 = 2400
                byte[] buffer = new byte[2400];
                while (!stopped) {
                    long len = audioDataStream.readData(buffer);
                    if (len == 0) {
                        break;
                    }
                    audioTrack.write(buffer, 0, (int) len);
                }

                audioDataStream.close();
                stopSynthesizing();
            } catch (Exception ex) {
                Log.e("Speech Synthesis Demo", "unexpected " + ex.getMessage());

            }
        }
    }
}