package com.example.murtaza.walkietalkie;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class AudioStreamingService extends Service {
    private static final int SAMPLE_RATE = 16000;
    public boolean keepPlaying = true;
    private AudioTrack audioTrack;
    byte[] buffer;

    public AudioStreamingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startStreaming();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        keepPlaying = false;
        if(audioTrack != null)
            audioTrack.release();
    }



    public void startStreaming() {
        Runnable audioPlayerRunnable = new Runnable() {
            @Override
            public void run() {
                int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
                    bufferSize = SAMPLE_RATE * 2;
                }

                Log.d("PLAY", "buffersize = "+bufferSize);

                 audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize,
                        AudioTrack.MODE_STREAM);

                audioTrack.play();

                Log.v("PLAY", "Audio streaming started");

                byte[] buffer = new byte[bufferSize];
                int offset = 0;

                try {
                    InputStream inputStream = SocketHandler.getSocket().getInputStream();

                    while(keepPlaying) {
                        inputStream.read(buffer, 0, bufferSize);
                        audioTrack.write(buffer, 0,buffer.length);
                        Log.d("audio", "writing");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t = new Thread(audioPlayerRunnable);
        t.start();
    }
}
