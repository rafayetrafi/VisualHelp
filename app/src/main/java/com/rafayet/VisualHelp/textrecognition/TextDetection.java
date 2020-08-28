package com.rafayet.VisualHelp.textrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.rafayet.VisualHelp.R;

import java.util.List;
import java.util.Locale;

public class TextDetection extends AppCompatActivity {

    SurfaceView mCameraView;
    TextView mTextView;
    CameraSource mCameraSource;
    TextToSpeech toSpeech;
    int counter = 0;

    private static final String TAG = "TextDetection";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_detection);

        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.text_view);
        startCameraSource();
        textToSpeechSyn();


        private void textToSpeechSyn() {

        toSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR)
                {
                    toSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {

        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    //Toast.makeText(MainActivity.this, "Changes Surface", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        mTextView.post(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());

                                }

                                mTextView.setText(stringBuilder.toString());
                                toSpeech.speak(stringBuilder.toString(), TextToSpeech.QUEUE_FLUSH,null);
                            }
                        });
                    }
                }
            });
        }
    }

    public void onPause(){
        if(toSpeech !=null){
            toSpeech.stop();
            toSpeech.shutdown();
        }
        super.onPause();
    }
}
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if (blockList.size() == 0)
        {
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show();
            textToSpeech.speak("Sorry! No text Found", TextToSpeech.QUEUE_FLUSH, null);
        }else {
            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()){
                String text = block.getText();
                textView.setText(text);
                int speechstatus = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                if (speechstatus == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
            }
        }

    }

}