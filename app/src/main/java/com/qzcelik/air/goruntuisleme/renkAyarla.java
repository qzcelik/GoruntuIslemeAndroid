package com.qzcelik.air.goruntuisleme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class renkAyarla extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2
{


    SeekBar kMin,yMin,mMin,kMax,yMax,mMax;

    int kMaxDeger,kMinDeger,yMaxDeger,yMinDeger,mMaxDeger,mMinDeger;
    CameraBridgeViewBase kamera;
    Mat hsv,siyahBeyaz,frame;
    Button gonder,sifirla;


    BaseLoaderCallback base=new BaseLoaderCallback(this)//kameradan görüntüyü vermek için
    {
        @Override
        public void onManagerConnected(int status) {
            kamera.enableView();
        }
    };
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.qzcelik.air.goruntuisleme.R.layout.activity_renk_ayarla);
        gonder=(Button)findViewById(com.qzcelik.air.goruntuisleme.R.id.ayarGonder);
        sifirla=(Button)findViewById(com.qzcelik.air.goruntuisleme.R.id.sifirla);
        if(!OpenCVLoader.initDebug())
        {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this,base);//eğer opencv manager yüklü değilse onu kurmak için
        }

        kamera=(CameraBridgeViewBase)findViewById(com.qzcelik.air.goruntuisleme.R.id.kameraAyar);
        kamera.setVisibility(View.VISIBLE);
        kamera.setCvCameraViewListener(this);
        kamera.setCameraIndex(0);
        kamera.setMaxFrameSize(176,144);

        kMax=(SeekBar)findViewById(com.qzcelik.air.goruntuisleme.R.id.s1);
        yMax=(SeekBar)findViewById(com.qzcelik.air.goruntuisleme.R.id.s2);
        mMax=(SeekBar)findViewById(com.qzcelik.air.goruntuisleme.R.id.s3);
        kMin=(SeekBar)findViewById(com.qzcelik.air.goruntuisleme.R.id.s4);
        yMin=(SeekBar)findViewById(com.qzcelik.air.goruntuisleme.R.id.s5);
        mMin=(SeekBar)findViewById(com.qzcelik.air.goruntuisleme.R.id.s6);
        seekBarGoster();

    sifirla.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view)
    {
        kMin.setProgress(0);
        kMax.setProgress(0);
        yMin.setProgress(0);
        yMax.setProgress(0);
        mMax.setProgress(0);
        mMin.setProgress(0);
    }
});

        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String ayar;
                ayar= String.valueOf(kMaxDeger+"#"+kMinDeger+"#"+yMaxDeger+"#"+yMinDeger+"#"+mMaxDeger+"#"+mMinDeger);
                Intent gonder=new Intent(renkAyarla.this,MainActivity.class);
                gonder.putExtra("ayar",ayar);
                startActivity(gonder);
            }
        });
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        frame=inputFrame.rgba();
        hsv=new Mat();
        siyahBeyaz=new Mat();
        Imgproc.cvtColor(frame,hsv,Imgproc.COLOR_BGR2HSV_FULL,4);
        Core.inRange(hsv,new Scalar(mMinDeger,yMinDeger,kMinDeger),new Scalar(mMaxDeger,yMaxDeger,kMaxDeger),siyahBeyaz);
        return  siyahBeyaz;
    }



    private void seekBarGoster()
    {


        kMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                kMinDeger=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        yMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                yMinDeger=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mMinDeger=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        kMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                kMaxDeger=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        yMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                yMaxDeger=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mMaxDeger=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


}
