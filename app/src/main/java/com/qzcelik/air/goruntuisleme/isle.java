package com.qzcelik.air.goruntuisleme;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class isle extends AppCompatActivity  implements CameraBridgeViewBase.CvCameraViewListener2{
         Button blue;
         final String cihazName="HC-05";//bluetooth modülümüzün adı adresi ile ulaşmakta mümkün
         final UUID bluetootAdres=UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//standart bluetooth adresi
         BluetoothSocket soket;
         BluetoothDevice cihaz;
         OutputStream disari;//dışarı göndereceğimiz veriler
         InputStream iceri;//içeri alacağımız veriler
         Spinner combo;

         Bitmap bitmap;
         CameraBridgeViewBase kamera;
         Boolean blueKont=false;

        ArrayList<String>renkKod=new ArrayList<String>();


    int kMaxDeger,kMinDeger,yMaxDeger,yMinDeger,mMaxDeger,mMinDeger;

    BaseLoaderCallback base=new BaseLoaderCallback(this)//kameradan görüntüyü vermek için
    {
        @Override
        public void onManagerConnected(int status) {
            kamera.enableView();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.qzcelik.air.goruntuisleme.R.layout.activity_isle);
        blue=(Button)findViewById(com.qzcelik.air.goruntuisleme.R.id.blue);
        combo=(Spinner)findViewById(com.qzcelik.air.goruntuisleme.R.id.spinner);


        if(!OpenCVLoader.initDebug())
        {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this,base);//eğer opencv manager yüklü değilse onu kurmak için
        }

        kamera=(CameraBridgeViewBase)findViewById(com.qzcelik.air.goruntuisleme.R.id.kamera);
        kamera.setVisibility(View.VISIBLE);
        kamera.setCvCameraViewListener(this);
        kamera.setCameraIndex(0);
        kamera.setMaxFrameSize(176,144);

        renkKod.add("Renk Seç");
        renkKod.add("Kırmızı");
        renkKod.add("Yesil");
        renkKod.add("Mavi");


        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, com.qzcelik.air.goruntuisleme.R.layout.support_simple_spinner_dropdown_item,renkKod);
        combo.setAdapter(adapter);
        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        if(i==1)
        {
            renkSkala("255#180#255#61#205#145");//kirmizi
            Toast.makeText(isle.this,"Seçilen Renk Kırmızı",Toast.LENGTH_SHORT).show();
        }
        if(i==2)
        {
           renkSkala("255#130#255#127#255#0");//yesil-sari
            Toast.makeText(isle.this,"Seçilen Renk Yeşil",Toast.LENGTH_SHORT).show();

        }
        if(i==3)
        {
            renkSkala("255#83#255#0#136#107");//mavi
            Toast.makeText(isle.this,"Seçilen Renk Mavi",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
});


        renkSkala(getIntent().getExtras().getString("sonAyar"));
        blue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                kont();
                blueKont=baglan();
            }
        });
    }

     



    public  void renkSkala(String gelenRenk)//renk ayarından gelen renk kodları parcalıyoruz scalar değişkenlerinin içine atıyoruz
    {
        String []parcala=gelenRenk.split("#");
        kMaxDeger=Integer.parseInt(parcala[0]);
        kMinDeger=Integer.parseInt(parcala[1]);
        yMaxDeger=Integer.parseInt(parcala[2]);
        yMinDeger=Integer.parseInt(parcala[3]);
        mMaxDeger=Integer.parseInt(parcala[4]);
        mMinDeger=Integer.parseInt(parcala[5]);
    }

    @Override
    public void onBackPressed()//geri tusuna baıldığı zaman olacak olay bluetooth u devere dışı bırakıyorum yoksa
                             //çıkıp tekrar girdiğim zaman bluetootha hala bağlı olduğu için yeni rengi algılamıyor
    {
        try {
            soket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent anaSayfa=new Intent(isle.this,MainActivity.class);
        startActivity(anaSayfa);
    }

    //-------------------------------kamera  metodları---------------------------------------------------------
    @Override
    public void onCameraViewStarted(int width, int height)
    {

    }

    @Override
    public void onCameraViewStopped()
    {

    }
    Mat goruntu,hsv,siyahBeyaz;
    int alan,xOrta,yOrta,solSag=0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1)
        {
            if(data!=null)
            {
                ArrayList<String> sesDegerleri=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Toast.makeText(isle.this,sesDegerleri.get(0),Toast.LENGTH_SHORT).show();
            }
        }
    }
   boolean sesKontrol=false;
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        if(blueKont==true)
        {
            //buradaki temel mantık kameradan her seferinde bir frame alınır ve bu frame bitmap'e aktarılıp resim resim olarak işlenir

            goruntu = inputFrame.rgba();
            hsv=new Mat();
            siyahBeyaz=new Mat();
            Imgproc.cvtColor(goruntu,hsv,Imgproc.COLOR_BGR2HSV_FULL,4);
            Core.inRange(hsv,new Scalar(mMinDeger,yMinDeger,kMinDeger),new Scalar(mMaxDeger,yMaxDeger,kMaxDeger),siyahBeyaz);


            bitmap = Bitmap.createBitmap(siyahBeyaz.cols(), siyahBeyaz.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(siyahBeyaz, bitmap);

            int x = 0, y = 0, topX = 0, topY = 0;

            while (x < 176) {
                while (y < 144) {
                    int piksel = bitmap.getPixel(x, y);//tek tek pixelleri elde ediyoruz
                    int kirmizi = Color.red(piksel);//renklere göre değerleri değişkenlere aktarıyoruz
                    int yesil = Color.green(piksel);//bu değerler 0-255 arasında değişmektedir
                    int mavi = Color.blue(piksel);
                    if (kirmizi ==255 && yesil == 255 && mavi ==255)//burada hedef nesnemizi yani beyaz olan kısmı alıyoruz
                    {
                        alan++;//şart sağlanırsa objenini alanını tespit ediyoruz
                        topX = topX + x;//ekrandaki koordinat bilgilerine ulaşıyoruz
                        topY = topY + y;
                    }
                    y++;//144 den küçük olana kadar arttırıyoruz
                }
                x++;
                y = 0;//değeri sıfırlamalıyız yoksa kaldığı yerden devam eder
            }

            x = 0;
            y = 0;

            if (alan > 100)
            {
                xOrta = topX / alan;
                yOrta = topY / alan;
                Point merkez = new Point(xOrta, yOrta);
                Imgproc.ellipse(goruntu, merkez, new Size(20, 20), 0, 0, 360, new Scalar(0, 0, 255), 4, 8, 0);

                if (alan > 100 && alan < 9000)//ileri
                {
                    try
                    {
                        disari.write(1);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    if(xOrta<50)//sağ
                    {
                        try
                        {
                            disari.write(4);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        solSag=4;
                    }
                    if(xOrta>130)//sol
                    {
                        try
                        {
                            disari.write(5);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        solSag=5;
                    }
             }
                else if (alan > 10000)//geri
                {
                    try {
                        disari.write(2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                 }
                sesKontrol=false;
           }



             if(alan==0)//hedef arama
              {


                  try {
                      disari.write(solSag);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             }

            alan = 0;
            topX=0;
            topY=0;


        }

        return goruntu;
    }


   /* public void sesIste()
    {

        Intent ses=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        ses.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(ses,1);
    }*/
//----------------------bluetooth metodları--------------------------------------------

    private void   kont()
    {
        boolean bulundu=false;
        BluetoothAdapter blu=BluetoothAdapter.getDefaultAdapter();
        if(blu==null)
        {
            Toast.makeText(this,"hata oluştu",Toast.LENGTH_SHORT).show();
        }

        if(!blu.isEnabled())//bluetooth kapalı ise açma isteği yolluyor
        {
            Intent goster=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(goster,0);//0 default değeri göndermek için boş olduğundan
        }

        Set<BluetoothDevice> bondedDevices=blu.getBondedDevices();//bağlı eşleştirilmiş cihazları bulup aktarıyor

        if(bondedDevices.isEmpty())//eğer hiç cihaz yoksa uyarı veriyor
        {
            Toast.makeText(this,"boş",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)//cihazlar varsa aradığımız cihazı buluyor
            {
                iterator.getName().equals(cihazName);//önceden belirlediğimiz cihazımız buluyoruz
                cihaz=iterator;//cihazı bağlıyoruz
                bulundu=true;
                break;

            }
        }

    }
    private boolean baglan()
    {
        boolean kont=true;

        try {
            soket=cihaz.createInsecureRfcommSocketToServiceRecord(bluetootAdres);//soket ile cihazı bağlıyoruz
            soket.connect();//bağlantı...

        }  catch (IOException e) {
            e.printStackTrace();
            kont = false;
        }
        if(kont)//eğer bir sorun ile karşılaşmadıysak dışarıdan gelen verileri ve gidecek verileri global değişkenlere aktarıyoruz
        {       //istediğimiz yerden ulaşabilelim diye
            try
            {
                disari=soket.getOutputStream();//dışarı ile bağlantı sağlandı
            } catch (IOException e) {
                e.printStackTrace();
            }
            try
            {
                iceri=soket.getInputStream();//içeriye veri alınmak istenirse kullanılacak soket
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return kont;
    }




}

