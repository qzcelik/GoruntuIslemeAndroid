package com.qzcelik.air.goruntuisleme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button basla,renkAyar,kontrolSinyal;

    ArrayList<String> dizi=new ArrayList<String>();
     int kontrolDeger=0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.qzcelik.air.goruntuisleme.R.layout.activity_main);

        SharedPreferences sakla=this.getPreferences(MODE_PRIVATE);

        final SharedPreferences.Editor editor=sakla.edit();
        editor.putInt("kontrol",0);
        editor.commit();


        basla=(Button)findViewById(com.qzcelik.air.goruntuisleme.R.id.buton);
        renkAyar=(Button)findViewById(com.qzcelik.air.goruntuisleme.R.id.renkAyar);
        kontrolSinyal=(Button)findViewById(com.qzcelik.air.goruntuisleme.R.id.button);






        basla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent isle=new Intent(MainActivity.this, isle.class);
                    isle.putExtra("sonAyar", getIntent().getExtras().getString("ayar"));
                    startActivity(isle);

            }
        });

        renkAyar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                kontrolDeger=1;
                editor.putInt("kontrol",kontrolDeger);
                editor.commit();

                Intent ayar=new Intent(MainActivity.this,renkAyarla.class);
                startActivity(ayar);
            }
        });
        kontrolSinyal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getir=new Intent(MainActivity.this,kontrol.class);
                startActivity(getir);
            }
        });
    }
}
