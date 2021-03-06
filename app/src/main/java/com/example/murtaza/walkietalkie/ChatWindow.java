package com.example.murtaza.walkietalkie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.skyfishjy.library.RippleBackground;

public class ChatWindow extends AppCompatActivity {
    private static final String TAG = "ChatWindow";
    Button send_btn;
    private RippleBackground rippleBackground;
    private MicRecorder micRecorder;
    Thread t;

    private boolean isButtonDown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        send_btn = (Button) findViewById(R.id.send_file_btn);

        rippleBackground = (RippleBackground) findViewById(R.id.content);

        send_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        pushToTalk();
                        break;
                }
                return true;
            }
        });
        startService(new Intent(getApplicationContext(), AudioStreamingService.class));
    }

    private void pushToTalk() {
        if (send_btn.getText().toString().equals("TALK")) {
            // stream audio
            send_btn.setText("OVER");
            micRecorder = new MicRecorder();
            t = new Thread(micRecorder);
            if (micRecorder != null) {
                MicRecorder.keepRecording = true;
            }
            t.start();

            // start animation
            rippleBackground.startRippleAnimation();

        } else if (send_btn.getText().toString().equals("OVER")) {
            send_btn.setText("TALK");
            if (micRecorder != null) {
                MicRecorder.keepRecording = false;
            }

            // stop animation
            rippleBackground.clearAnimation();
            rippleBackground.stopRippleAnimation();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == 1015)
            if (isButtonDown)
                if (send_btn.getText().toString().equals("OVER")) {
                    send_btn.setText("TALK");
                    if (micRecorder != null) {
                        MicRecorder.keepRecording = false;
                    }

                    // stop animation
                    rippleBackground.clearAnimation();
                    rippleBackground.stopRippleAnimation();
                }
        isButtonDown = false;
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == 1015)
            if (!isButtonDown)
                if (send_btn.getText().toString().equals("TALK")) {
                    // stream audio
                    send_btn.setText("OVER");
                    micRecorder = new MicRecorder();
                    t = new Thread(micRecorder);
                    if (micRecorder != null) {
                        MicRecorder.keepRecording = true;
                    }
                    t.start();

                    // start animation
                    rippleBackground.startRippleAnimation();

                }
        isButtonDown = true;
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (micRecorder != null) {
            MicRecorder.keepRecording = false;
        }
    }
}
